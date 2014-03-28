package ru.biocad.ig.common.algorithm

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 21.11.13
 * Time: 17:28
 */
object PairSeqTools {
  def longestSubstr(s : String, t : String) : (Int, Int, Int) = {
    if (s.isEmpty || t.isEmpty) {
      return (0, 0, 0)
    }

    val m = s.length
    val n = t.length
    var cost = 0
    var maxLen = 0
    var posi = 0
    var posj = 0
    var p = Array.fill[Int](n)(0)
    var d = Array.fill[Int](n)(0)

    for (i <- 0 until m) {
      for (j <- 0 until n) {
        // calculate cost/score
        if (s.charAt(i) != t.charAt(j)) {
          cost = 0
        } else {
          if ((i == 0) || (j == 0)) {
            cost = 1
          } else {
            cost = p(j - 1) + 1
          }
        }
        d(j) = cost

        if (cost > maxLen) {
          posi = i
          posj = j
          maxLen = cost
        }
      } // for {}

      val swap = p
      p = d
      d = swap
    }

    (posi - maxLen + 1, posj - maxLen + 1, maxLen)
  }

  def getAlignedPattern(seq_a : String, pattern_a : String) : String = {
    var i = 0
    var j = seq_a.size - 1

    (seq_a zip pattern_a).takeWhile(c => c._2 == '-').foreach(_ => i += 1)
    (seq_a zip pattern_a).reverseIterator.takeWhile(c => c._2 == '-').foreach(_ => j -= 1)

    seq_a.substring(i, j + 1).replace("-", "")
  }
}
