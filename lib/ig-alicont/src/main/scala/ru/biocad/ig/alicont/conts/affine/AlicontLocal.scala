package ru.biocad.ig.alicont.conts.affine

import ru.biocad.ig.alicont.conts.AffineAlicont
import ru.biocad.ig.alicont.algorithms.affine.LocalAlignment

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 22:39
 */
class AlicontLocal(maxheight : Int, query : String, gap_open : Double, gap_ext : Double, score_matrix : Array[Array[Double]])
  extends AffineAlicont(maxheight : Int, query : String, gap_open : Double, gap_ext : Double , score_matrix : Array[Array[Double]]) {

  def push(s : String) : Unit = {
    _strings.push(s)
    LocalAlignment.extendMatrix(s, _query, _gap_open, _gap_ext, _score,
                                horizontal_matrix = _horizontalMatrix,
                                vertical_matrix = _verticalMatrix,
                                substitution_matrix = _substitutionMatrix,
                                matrix = _matrix)
  }

  def alignment() : (Double, (String, String)) =
    LocalAlignment.traceback(target, _query,
                             horizontal_matrix = _horizontalMatrix,
                             vertical_matrix = _verticalMatrix,
                             substitution_matrix = _substitutionMatrix,
                             matrix = _matrix)
}
