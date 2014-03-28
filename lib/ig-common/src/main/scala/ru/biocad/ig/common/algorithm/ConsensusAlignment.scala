package ru.biocad.ig.common.algorithm

import scala.collection.mutable

/**
 * Modified semiglobal alignment. Works on sequences of bags of symbols rather than individual symbols.
 * For two sequences, packages each symbol into its own bag, and runs semiglobal alignment on bags as if on regular strings.
 * The resulting traces are merged into a new sequence of bags, now every bag containing two symbols (one possibly
 * being a gap). Subsequent input sequences are aligned against the accumulate sequence-of-bags from previous iteration.
 * The final result, the one sequence assembled from input Sanger reads, is constructed as a consensus of symbols in
 * every bag. Ties in consensus for a position are resolved by taking one of the equally most frequent symbols.
 * To prevent gaps from appearing at ends of the resulting sequence, gap symbols do not participate in
 * consensus if every bag to the left or right up to sequence start or end also contains a gap.
 *
 * Example:
 * [W][A][T][L][R]
 * [T][D][R][S][W]
 * [L][R][A][W][A]
 * ->
 * [W][A][T][L][R][-][-][-]
 * [-][-][T][D][R][S][W][-]
 * [-][-][-][L][R][A][W][A]
 * ->
 * [W--][A--][TT-][LDL][RRR][-SA][-WW][--A]
 * ->
 * WATLRSWA
 * (gaps on ends ignored in calculating consensus)
 */
object ConsensusAlignment {
  private val GAP_PENALTY = -1
  private val MATCH_SCORE = 1
  private val MISMATCH_SCORE = -1

  def consensus(sequences : Iterable[String], wildcard : Character, gap : Double = GAP_PENALTY) : String = {
    alignAll(sequences, gap).strictConsensus(wildcard)
  }
  
  def merge(sequences : Iterable[String], gap : Double = GAP_PENALTY) : String = {
    alignAll(sequences, gap).mergeConsensus
  }


  private def alignAll(sequences : Iterable[String], gap : Double) : BagSequence = {
    if (sequences.size == 0) throw new IllegalArgumentException("Cannot process 0 sequences.")
    if (sequences.exists(seq => seq.contains('-'))) throw new IllegalArgumentException("One of input sequences contains gap.")

    var acc = new BagSequence(sequences.head)
    for (seq <- sequences.tail if seq.length > 0) {
      acc = align(acc, new BagSequence(seq), gap)
    }
    
    acc
  }

  private def align(seq1 : BagSequence, seq2 : BagSequence, gap : Double) : BagSequence = {
    val mat = matrix(seq1, seq2, gap)
    val (trace1, trace2) = backtrack(seq1, seq2, mat, gap)
    trace1 merge trace2
  }

  private def score(b1 : Bag, b2 : Bag) : Double = if (b1 intersects b2) MATCH_SCORE else MISMATCH_SCORE

  private def matrix(seq1 : BagSequence, seq2 : BagSequence, gap : Double) : Array[Array[Double]] = {

    val mat = Array.ofDim[Double](seq1.length + 1, seq2.length + 1)
    for (i <- 0 to seq1.length) mat(i)(0) = 0
    for (i <- 0 to seq2.length) mat(0)(i) = 0

    for (row <- 1 to seq1.length; col <- 1 to seq2.length) {
      mat(row)(col) = List(
        mat(row - 1)(col - 1) + score(seq1(row - 1), seq2(col - 1)),
        mat(row - 1)(col) + gap,
        mat(row)(col - 1) + gap
      ).max
    }

    mat
  }

  private def backtrack(seq1 : BagSequence, seq2 : BagSequence, mat : Array[Array[Double]], gap : Double) : (BagSequence, BagSequence) = {
    val trace1 = new BagSequence("", seq1.arity)
    val trace2 = new BagSequence("", seq2.arity)

    def prependBags(b1 : Bag, b2 : Bag) : Unit = {
      trace1.prepend(b1); trace2.prepend(b2)
    }
    def prependSequences(s1 : BagSequence, s2 : BagSequence) : Unit =
      (s1 zip s2).toSeq.reverse.foreach {
        case (b1, b2) => prependBags(b1, b2)
      }

    val best_row = (1 to seq1.length).maxBy(row => mat(row)(seq2.length))
    val best_col = (1 to seq2.length).maxBy(col => mat(seq1.length)(col))

    var (row, col) = List((best_row, seq2.length), (seq1.length, best_col)).maxBy {
      case (r, b) => mat(r)(b)
    }

    if (row == seq1.length)
      prependSequences(new BagSequence("-" * (seq2.length - col), seq1.arity), seq2.subsequence(col, seq2.length))
    else // col must be == seq2.length
      prependSequences(seq1.subsequence(row, seq1.length), new BagSequence("-" * (seq1.length - row), seq2.arity))

    while (row > 0 && col > 0) {
      val upperleft = mat(row - 1)(col - 1) + score(seq1(row - 1), seq2(col - 1))
      val upper = mat(row - 1)(col) + gap
      val left = mat(row)(col - 1) + gap
      mat(row)(col) match {
        case `upperleft` =>
          prependBags(seq1(row - 1), seq2(col - 1))
          row -= 1
          col -= 1
        case `upper` =>
          prependBags(seq1(row - 1), new Bag(Seq.fill(seq2.arity)('-') : _*))
          row -= 1
        case `left` =>
          prependBags(new Bag(Seq.fill(seq1.arity)('-') : _*), seq2(col - 1))
          col -= 1
        case _ => throw new RuntimeException("Bug in backtracking.")
      }
    }

    if (row == 0)
      prependSequences(new BagSequence("-" * col, seq1.arity), seq2.subsequence(0, col))
    else // col must be 0
      prependSequences(seq1.subsequence(0, row), new BagSequence("-" * row, seq2.arity))

    (trace1, trace2)
  }

  /**
   * Sequence of bags for the modified semiglobal alignment.
   **/

  private class BagSequence private(cont : Seq[Bag], val arity : Int) extends Iterable[Bag] {
    def this(init : String, arity : Int = 1) = this(init.map(b => new Bag(Seq.fill(arity)(b) : _*)).toSeq, arity)

    private val _cont = mutable.ArrayBuffer[Bag](cont : _*)

    override def iterator : Iterator[Bag] = _cont.iterator

    def apply(index : Int) : Bag = _cont(index)

    def length : Int = _cont.length

    def prepend(bag : Bag) : Unit = {
      if (bag.arity != arity) throw new IllegalArgumentException(s"Bag arity differs from the sequence's, $arity vs ${bag.arity}.")
      _cont.prepend(bag)
    }

    def subsequence(start : Int, end : Int) = new BagSequence(_cont.slice(start, end), arity)

    def merge(other : BagSequence) : BagSequence = {
      if (other.length != length) throw new IllegalArgumentException("Sequence differs in length.")
      _cont.zip(other._cont).foldRight(new BagSequence("", arity + other.arity)) {
        case ((bag1, bag2), acc) => acc.prepend(bag1 ++ bag2); acc
      }
    }

    def mergeConsensus : String = {
      val left = _cont.takeWhile(b => b.contains('-')).map(b => b.noGaps.majorityConsensus)
      val right = _cont.slice(left.length, _cont.length).reverse.takeWhile(b => b.contains('-')).reverse.map(b => b.noGaps.majorityConsensus)
      val center = _cont.slice(left.length, _cont.length - right.length).map(b => b.majorityConsensus)
      (left ++ center ++ right).mkString("").replace("-", "")
    }
    
    def strictConsensus(wildcard : Char) : String = {
      _cont.map(b => b.strictConsensus(wildcard)).mkString("")
    }

    override def toString() = _cont.mkString("")
  }

  /**
   * Element of BagSequence, a collection of characters.
   * @param init initial character to put in the bag
   */
  private class Bag(init : Char*) {
    private val _cont = mutable.ListBuffer[Char](init : _*)

    def arity : Int = _cont.length

    def add(c : Char) : Unit = _cont += c

    def contains(c : Char) : Boolean = _cont.contains(c)

    def intersects(other : Bag) : Boolean = _cont.exists(c => other.contains(c))

    def ++(other : Bag) : Bag = new Bag(_cont ++ other._cont : _*)

    def noGaps : Bag = new Bag(_cont.filter{ c => c != '-' } : _*)
    
    def majorityConsensus : Char = {
      val groups = _cont.groupBy(identity)
      val max_size = groups.maxBy { case (_, duplicates) => duplicates.size }._2.size
      val most_common = groups.filter { case (char, duplicates) => duplicates.size == max_size }
      most_common.head._1
    }
    
    def strictConsensus(wildcard : Char) : Char = {
      val first = _cont.head
      if (_cont.forall(c => c == first)) first else wildcard
    }

    override def toString = "[%s]".format(_cont.mkString(""))
  }

}
