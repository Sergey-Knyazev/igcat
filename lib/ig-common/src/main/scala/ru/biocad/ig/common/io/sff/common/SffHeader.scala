package ru.biocad.ig.common.io.sff.common

import java.io.{DataInput, IOException}
import com.typesafe.scalalogging.slf4j.Logging

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 12.02.14
 * Time: 14:30
 */
class SffHeader {
  var magic            : Int         = 0
  var version          : Int         = 0
  var index_offset     : Long        = 0
  var index_len        : Int         = 0
  var nreads           : Int         = 0
  var header_len       : Short       = 0
  var key_len          : Short       = 0
  var flow_len         : Short       = 0
  var flowgram_format  : Byte        = 0
  var flow             : String      = null
  var key              : String      = null


  def dump() : Unit = {
    printf("Header:\n")
    printf("    MAGIC:           %d\n", magic)
    printf("    Version:         %d\n", version)
    printf("    Index offset:    %d\n", index_offset)
    printf("    Index length:    %d\n", index_len)
    printf("    # reads:         %d\n", nreads)
    printf("    Header length:   %d\n", header_len)
    printf("    Key length:      %d\n", key_len)
    printf("    Flow length:     %d\n", flow_len)
    printf("    Flowgram format: %d\n", flowgram_format)
    printf("    Flow:            %s\n", flow)
    printf("    Key:             %s\n", key)
  }
}

object SffHeader extends Logging {
  private val SFF_MAGIC_NUMBER = 779314790
  private val COMMON_HEADER_STATIC_SIZE = 4 + 4 + 8 + 4 + 4 + 2 + 2 + 2 + 1

  def fromDataInput(data : DataInput) : SffHeader = {
    val header = new SffHeader()

    header.magic = data.readInt()
    if (header.magic != SFF_MAGIC_NUMBER) {
      logger.error("Wrong MAGIC. File is not SFF.")
      throw new IOException("Not a sff file")
    }
    header.version = data.readInt()
    header.index_offset = data.readLong()
    header.index_len = data.readInt()
    header.nreads = data.readInt()
    header.header_len = data.readShort()
    header.key_len = data.readShort()
    header.flow_len = data.readShort()
    header.flowgram_format = data.readByte()

    val flow = Array.fill[Byte](header.flow_len)(0)
    data.readFully(flow)
    header.flow = flow.map(_.toChar).mkString

    val key = Array.fill[Byte](header.key_len)(0)
    data.readFully(key)
    header.key = key.map(_.toChar).mkString

    SffCommon.alignToBoundary(data, COMMON_HEADER_STATIC_SIZE + flow.size + key.size)

    logger.debug("SFF header was received.")
    header
  }
}
