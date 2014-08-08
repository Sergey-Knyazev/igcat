package ru.biocad.ig.alicont.algorithms.affine

import ru.biocad.ig.alicont.algorithms.AffineAlignment
import ru.biocad.ig.alicont.common.{PrepareIJ, Matrix}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 22:47
 */
object LocalAlignment extends AffineAlignment {
  override def extendMatrix(s : String, query : String, gapOpen : Double, gapExtend : Double,
                            score_matrix : Array[Array[Double]],
                            horizontal_matrix : Matrix, vertical_matrix : Matrix, substitution_matrix : Matrix,
                            matrix : Matrix): Unit = {
    def move() : Unit = {
      matrix.move(1)                 // V
      horizontal_matrix.move(1)      // E
      vertical_matrix.move(1)        // F
      substitution_matrix.move(1)    // G
    }

    if (matrix.height == 0) {
      move()

      // Zero line initial values
      (0 to query.size).foreach(i => {
        matrix.last(i)          = 0
        vertical_matrix.last(i) = 0
      })
      matrix.last(0) = 0
    }
    (1 to s.size).foreach(i => {
      move()

      // Init zero pos values
      matrix.last(0)            = 0
      horizontal_matrix.last(0) = 0

      (1 to query.size).foreach(j => {
        val score = score_matrix(s(i - 1))(query(j - 1))

        substitution_matrix.last(j) = matrix.pred(j - 1) + score
        horizontal_matrix.last(j)   = List(horizontal_matrix.last(j - 1) + gapExtend,
                                           matrix.last(j - 1) + gapOpen + gapExtend).max
        vertical_matrix.last(j)     = List(vertical_matrix.pred(j) + gapExtend,
                                           matrix.pred(j) + gapOpen + gapExtend).max

        matrix.last(j) = List(substitution_matrix.last(j),
                              horizontal_matrix.last(j),
                              vertical_matrix.last(j),
                              0.0).max
      })
    })
  }

  override def traceback(s: String, query: String, horizontal_matrix: Matrix, vertical_matrix: Matrix,
                         substitution_matrix: Matrix, matrix: Matrix): (Double, (String, String)) = {
    var (score, (i, j)) = PrepareIJ.local(s.size, query.size, matrix)

    val result_s = StringBuilder.newBuilder
    val result_q = StringBuilder.newBuilder

    while (i != 0 && j != 0 && matrix(i)(j) != 0) {
      val mij = matrix(i)(j)
      if (mij == substitution_matrix(i)(j) && i != 0 && j != 0) {
        result_s += s(i - 1)
        result_q += query(j - 1)
        i -= 1
        j -= 1
      }
      else if ((mij == vertical_matrix(i)(j) || j == 0) && i != 0) {
        result_s += s(i - 1)
        result_q += '-'
        i -= 1
      }
      else if ((mij == horizontal_matrix(i)(j) || i == 0) && j != 0) {
        result_s += '-'
        result_q += query(j - 1)
        j -= 1
      }
      else {
        assert(assertion = false)
      }
    }

    (score, (result_q.reverse.mkString, result_s.reverse.mkString))
  }
}
