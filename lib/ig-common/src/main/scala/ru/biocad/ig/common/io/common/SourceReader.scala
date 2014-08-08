package ru.biocad.ig.common.io.common

import scala.io.Source

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 08.04.14
 * Time: 12:23
 */
class SourceReader(source : Source) {
  var lines = source.getLines()

  def readLine() : String =
    if (lines.hasNext) lines.next() else null

  def close() : Unit =
    source.close()
}
