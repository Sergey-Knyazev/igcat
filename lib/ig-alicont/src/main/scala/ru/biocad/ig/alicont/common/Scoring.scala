package ru.biocad.ig.alicont.common

import java.io.File
import scala.io.Source
import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 28.10.13
 * Time: 12:49
 */
object Scoring {
  def loadMatrix(source : Source) : Array[Array[Double]] = {
    val result = Array.fill(256, 256)(.0)
    var letters : String = null

    source.getLines().foreach(line => {
      if (line.startsWith("#") || line.isEmpty) {
      }
      else if (line.startsWith(" ")) {
        letters = line.split(' ').filter(_ != "").mkString("")
      }
      else {
        val current = line(0)
        line.substring(1).split(' ').filter(_ != "").zipWithIndex.foreach(tpl => {
          val (v, i) = tpl
          result(current)(letters(i)) = v.toInt
        })
      }
    })

    result
  }

  def loadMatrix(file : File) : Array[Array[Double]] = loadMatrix(Source.fromFile(file))

  def loadMatrix(url : URL) : Array[Array[Double]] = loadMatrix(Source.fromURL(url))

  def loadMatrix(filename : String) : Array[Array[Double]] = loadMatrix(new File(filename))

  def createMatrix(alphabet : String, special : Char)
                  (match_score : Double, mismatch_score : Double, special_score : Double) : Array[Array[Double]] = {
    val result = Array.fill[Double](256, 256)(0)
    alphabet.foreach(c1 => {
      alphabet.foreach(c2 => {
        result(c1)(c2) = if (c1 == c2) match_score else mismatch_score
      })
      result(c1)(special) = special_score
      result(special)(c1) = special_score
    })

    result
  }
}
