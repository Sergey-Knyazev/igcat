package ru.biocad.ig.common.io

import scala.io.Source
import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 31.10.13
 * Time: 13:21
 */
object MarkingUtils {
  def readMarking(file : File) : Stream[(String, Array[(Int, Int)])] = {
    def parse(lines: Iterator[String]) : Stream[(String, Array[(Int, Int)])] = {
      if (lines.isEmpty) {
        return Stream[(String, Array[(Int, Int)])]()
      }

      val jline = lines.next().mkString
      val line = jline.split('\t').filter(_ != "")
      val name = line(0).trim
      val arr = markup2array(line, 1)

      (name, arr) #:: parse(lines)
    }

    parse(Source.fromFile(file).getLines())
  }

  def markup2array(line : Array[String], offset : Int = 0) : Array[(Int, Int)] = {
    val arr = Array.fill[(Int, Int)](7)((0, 0))
    (0 to 6).foreach(i => arr(i) = (line(offset + 2*i).toInt - 1, line(offset + 1 + 2*i).toInt - 1))
    arr
  }
}
