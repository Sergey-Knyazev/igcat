package ru.biocad.ig.common.io.sff.common

import java.io.DataInput

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 12.02.14
 * Time: 15:07
 */
object SffCommon {
  def alignToBoundary(data : DataInput, skip : Int) : Unit = {
    var pos = skip % 8
    if (pos != 0) {
      pos = 8 - (pos % 8)
    }
    data.skipBytes(pos)
  }
}
