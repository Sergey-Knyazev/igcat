package ru.biocad.ig.alicont.conts.simple

import ru.biocad.ig.alicont.algorithms.simple.GlobalAlignment
import ru.biocad.ig.alicont.conts.SimpleAlicont

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 27.11.13
 * Time: 23:32
 */
class AlicontGlobal(maxheight : Int, query : String, gap : Double, score_matrix : Array[Array[Double]])
  extends SimpleAlicont(maxheight : Int, query : String, gap : Double, score_matrix : Array[Array[Double]]) {

  def push(s : String) : Unit = {
    _strings.push(s)
    GlobalAlignment.extendMatrix(s, _query, _gap, _score, _matrix)
  }

  def alignment() : (Double, (String, String)) = {
    GlobalAlignment.traceback(target, _query, _gap, _score, _matrix)
  }
}
