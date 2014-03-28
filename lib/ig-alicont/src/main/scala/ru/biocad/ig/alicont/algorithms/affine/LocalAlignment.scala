package ru.biocad.ig.alicont.algorithms.affine

import ru.biocad.ig.alicont.algorithms.AffineAlignment
import ru.biocad.ig.alicont.common.Matrix

/**
 * Created with IntelliJ IDEA.
 * User: Sergey Knyazev (sergey.n.knyazev@gmail.com)
 * Date: 28.11.13
 * Time: 15:11
 */
object LocalAlignment extends AffineAlignment {
  def extendMatrix(s : String, query : String, gapOpen : Double, gapExtend : Double, score_matrix : Array[Array[Double]],
                   insertion_matrix : Matrix, deletion_matrix : Matrix, matrix : Matrix) : Unit = {
    if (matrix.height == 0) {
      insertion_matrix.move(1)
      deletion_matrix.move(1)
      matrix.move(1)
      (0 to query.size).foreach(i => insertion_matrix.last(i) = 0)
      (0 to query.size).foreach(i => deletion_matrix.last(i) = 0)
      (0 to query.size).foreach(i => matrix.last(i) = 0)
    }
    (1 to s.size).foreach(i => {

      insertion_matrix.move(1)
      deletion_matrix.move(1)
      matrix.move(1)

      insertion_matrix.last(0) = insertion_matrix.pred(0) + gapExtend
      deletion_matrix.last(0) = deletion_matrix.pred(0) + gapExtend
      matrix.last(0) = matrix.pred(0) + gapExtend

      (1 to query.size).foreach(j => {
        val score = score_matrix(s(i - 1))(query(j - 1))

        insertion_matrix.last(j) = (insertion_matrix.pred(j) + gapExtend :: matrix.pred(j) + (gapOpen + gapExtend) ::
          .0 :: Nil).max
        deletion_matrix.last(j) = (deletion_matrix.last(j-1) + gapExtend :: matrix.last(j-1) + (gapOpen + gapExtend) ::
          .0 :: Nil).max
        matrix.last(j) = (matrix.pred(j - 1) + score :: insertion_matrix.last(j) :: deletion_matrix.last(j) :: Nil).max
      })
    })
  }

  def traceback(s : String, query : String, score_matrix : Array[Array[Double]],
                deletion_matrix : Matrix, insertion_matrix : Matrix, matrix : Matrix) : (Double, (String, String)) = {
    var (i, j) = (s.size, query.size)

    var score = Double.MinValue
    for (it <- 0 to s.size; jt <- 0 to query.size) {
      if (score < matrix(it)(jt)) {
        score = matrix(it)(jt)
        i = it
        j = jt
      }
    }

    val result_s = StringBuilder.newBuilder
    val result_q = StringBuilder.newBuilder

    while (i != 0 && j != 0 && matrix(i)(j) != 0) {
      val cs : Char = if (i > 0) s(i - 1) else 0
      val cq : Char = if (j > 0) query(j - 1) else 0
      if (i != 0 && matrix(i)(j) == deletion_matrix(i)(j)) {
        i -= 1
        result_s.append(cs)
        result_q.append('-')
      } else if (j != 0 && matrix(i)(j) == insertion_matrix(i)(j)) {
        j -= 1
        result_s.append('-')
        result_q.append(cq)
      } else if (matrix(i)(j) == matrix(i - 1)(j - 1) + score_matrix(cs)(cq)) {
        i -= 1
        j -= 1
        result_s.append(cs)
        result_q.append(cq)
      } else if (matrix(i)(j) != 0) {
        assert(false)
      }
    }

    (score, (result_q.reverse.mkString, result_s.reverse.mkString))
  }
}