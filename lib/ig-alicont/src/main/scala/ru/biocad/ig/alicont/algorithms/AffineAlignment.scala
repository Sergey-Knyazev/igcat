package ru.biocad.ig.alicont.algorithms

import ru.biocad.ig.alicont.common.Matrix

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 27.11.13
 * Time: 15:33
 */
trait AffineAlignment {
  def extendMatrix(s : String, query : String, gapOpen : Double, gapExtend : Double, score_matrix : Array[Array[Double]],
                   horizontal_matrix : Matrix, vertical_matrix : Matrix, substitution_matrix : Matrix, matrix : Matrix) : Unit
  def traceback(s : String, query : String, horizontal_matrix : Matrix, vertical_matrix : Matrix,
                substitution_matrix : Matrix, matrix : Matrix) : (Double, (String, String))
}
