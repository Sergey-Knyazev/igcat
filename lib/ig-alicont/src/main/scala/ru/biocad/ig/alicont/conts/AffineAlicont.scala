package ru.biocad.ig.alicont.conts

import ru.biocad.ig.alicont.common.Matrix

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 27.11.13
 * Time: 22:51
 */
abstract class AffineAlicont(maxheight : Int, query : String, gap_open : Double, gap_ext : Double,
                             score_matrix : Array[Array[Double]])
  extends AbstractAlicont(maxheight, query, score_matrix) {

  protected val _gap_open = gap_open
  protected val _gap_ext  = gap_ext
  protected val _substitutionMatrix = new Matrix(query.size, maxheight)
  protected val _horizontalMatrix   = new Matrix(query.size, maxheight)
  protected val _verticalMatrix    = new Matrix(query.size, maxheight)

  def pop() : Unit = {
    val ls = _strings.pop()
    _matrix.move(-ls.size)
    _substitutionMatrix.move(-ls.size)
    _horizontalMatrix.move(-ls.size)
    _verticalMatrix.move(-ls.size)
  }
}
