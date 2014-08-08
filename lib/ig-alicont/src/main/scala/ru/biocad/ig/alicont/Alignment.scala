package ru.biocad.ig.alicont

import ru.biocad.ig.alicont.common.Matrix
import ru.biocad.ig.alicont.algorithms.{simple, affine}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 14:09
 */
object Alignment {
  def global(s : String, t : String, gap : Double, matrix : Array[Array[Double]]) : (Double, (String, String)) = {
    val m = new Matrix(s.size, t.size)
    simple.GlobalAlignment.extendMatrix(s, t, gap, matrix, m)
    simple.GlobalAlignment.traceback(s, t, gap, matrix, m)
  }

  def global(s : String, t : String, gap_open : Double, gap_ext : Double,
             matrix : Array[Array[Double]]) : (Double, (String, String)) = {
    val v = new Matrix(s.size, t.size)
    val e = new Matrix(s.size, t.size)
    val f = new Matrix(s.size, t.size)
    val g = new Matrix(s.size, t.size)
    affine.GlobalAlignment.extendMatrix(s, t, gap_open, gap_ext, matrix, v, e, f, g)
    affine.GlobalAlignment.traceback(s, t, v, e, f, g)
  }

  def local(s : String, t : String, gap : Double, matrix : Array[Array[Double]]) : (Double, (String, String)) = {
    val m = new Matrix(s.size, t.size)
    simple.LocalAlignment.extendMatrix(s, t, gap, matrix, m)
    simple.LocalAlignment.traceback(s, t, gap, matrix, m)
  }

  def local(s : String, t : String, gap_open : Double, gap_ext : Double,
            matrix : Array[Array[Double]]) : (Double, (String, String)) = {
    val v = new Matrix(s.size, t.size)
    val e = new Matrix(s.size, t.size)
    val f = new Matrix(s.size, t.size)
    val g = new Matrix(s.size, t.size)
    affine.LocalAlignment.extendMatrix(s, t, gap_open, gap_ext, matrix, v, e, f, g)
    affine.LocalAlignment.traceback(s, t, v, e, f, g)
  }

  def semiglobal(s : String, t : String, gap : Double, matrix : Array[Array[Double]]) : (Double, (String, String)) = {
    val m = new Matrix(s.size, t.size)
    simple.SemiglobalAlignment.extendMatrix(s, t, gap, matrix, m)
    simple.SemiglobalAlignment.traceback(s, t, gap, matrix, m)
  }

  def semiglobal(s : String, t : String, gap_open : Double, gap_ext : Double,
                 matrix : Array[Array[Double]]) : (Double, (String, String)) = {
    val v = new Matrix(s.size, t.size)
    val e = new Matrix(s.size, t.size)
    val f = new Matrix(s.size, t.size)
    val g = new Matrix(s.size, t.size)
    affine.SemiglobalAlignment.extendMatrix(s, t, gap_open, gap_ext, matrix, v, e, f, g)
    affine.SemiglobalAlignment.traceback(s, t, v, e, f, g)
  }

  object tools {
    def getAlignedPattern(seq_a : String, pattern_a : String) : String = {
      var i = 0
      var j = seq_a.size - 1

      (seq_a zip pattern_a).takeWhile(c => c._2 == '-').foreach(_ => i += 1)
      (seq_a zip pattern_a).reverseIterator.takeWhile(c => c._2 == '-').foreach(_ => j -= 1)

      seq_a.substring(i, j + 1).replace("-", "")
    }

    def getLocalHamming(s : String, pattern : String) : Int = {
      var i = 0
      var j = s.size

      pattern.takeWhile(_ == '-').foreach(_ => i += 1)
      pattern.reverseIterator.takeWhile(_ == '-').foreach(_ => j -= 1)

      (s.substring(i, j) zip pattern.substring(i, j)).count(tpl => tpl._1 != tpl._2)
    }
  }
}
