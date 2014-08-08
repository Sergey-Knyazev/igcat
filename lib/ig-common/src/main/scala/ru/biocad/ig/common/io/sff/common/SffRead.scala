package ru.biocad.ig.common.io.sff.common

import java.io.{IOException, DataInput}
import com.typesafe.scalalogging.LazyLogging
import ru.biocad.ig.common.io.sff.common.SffRead.{SffReadHeader, SffReadData}
import ru.biocad.ig.common.io.common.Sequence


/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 12.02.14
 * Time: 15:11
 */
case class SffRead(header : SffReadHeader, data : SffReadData) {
  def sequence : Sequence = {
    val start = math.max(header.clip_adapter_left, header.clip_qual_left) - 1
    var end = header.nbases
    if (header.clip_adapter_right != 0 && header.clip_adapter_right < end) {
      end = header.clip_adapter_right
    }
    if (header.clip_qual_right != 0 && header.clip_qual_right < end) {
      end = header.clip_qual_right
    }

    Sequence(name = header.name, sequence = data.bases.substring(start, end), quality = data.quality.slice(start, end))
  }
}

object SffRead extends LazyLogging {
  private val READ_BLOCK_STATIC_SIZE = 2 + 2 + 4 + 2 + 2 + 2 + 2

  def fromDataInput(data : DataInput, common : SffHeader) : SffRead = {
    // Header
    val header = new SffReadHeader()

    header.header_len = data.readShort()
    header.name_len = data.readShort()
    header.nbases = data.readInt()
    header.clip_qual_left = data.readShort()
    header.clip_qual_right = data.readShort()
    header.clip_adapter_left = data.readShort()
    header.clip_adapter_right = data.readShort()

    val name = Array.fill[Byte](header.name_len)(0)
    data.readFully(name)
    header.name = name.map(_.toChar).mkString

    val offset = header.header_len - (header.name_len + READ_BLOCK_STATIC_SIZE)
    if (offset < 0) {
      val e = new IOException("Read header has wrong size")
      logger.warn("SFF problem", e)
      throw e
    }
    data.skipBytes(offset)

    // Data
    val readdata = new SffReadData()

    val flowgram = Array.fill[Byte](common.flow_len * 2)(0)
    val flow_index = Array.fill[Byte](header.nbases)(0)
    val bases = Array.fill[Byte](header.nbases)(0)
    val quality = Array.fill[Byte](header.nbases)(0)

    data.readFully(flowgram)
    data.readFully(flow_index)
    data.readFully(bases)
    data.readFully(quality)

    readdata.flowgram = Array.fill[Short](common.flow_len)(0)
    readdata.flow_index = flow_index
    readdata.bases = bases.map(_.toChar).mkString
    readdata.quality = quality

    flowgram.zipWithIndex.foreach(tpl => {
      val (v, i) = tpl
      val index = i / 2
      val offset = if (i % 2 == 0) 8 else 0
      readdata.flowgram(index) = (readdata.flowgram(index) + (v << offset)).toShort
    })

    SffCommon.alignToBoundary(data, flowgram.size + flow_index.size + bases.size + quality.size)

    logger.debug(s"Read ${header.name} was received.")

    SffRead(header, readdata)
  }

  class SffReadHeader {
    var header_len          : Short       = 0
    var name_len            : Short       = 0
    var nbases              : Int         = 0
    var clip_qual_left      : Short       = 0
    var clip_qual_right     : Short       = 0
    var clip_adapter_left   : Short       = 0
    var clip_adapter_right  : Short       = 0
    var name                : String      = null

    def dump() : Unit = {
      printf("Read header:\n")
      printf("    Header length:      %d\n", header_len)
      printf("    Name   length:      %d\n", name_len)
      printf("    # bases:            %d\n", nbases)
      printf("    Clip quality left:  %d\n", clip_qual_left)
      printf("    Clip quality rigth: %d\n", clip_qual_right)
      printf("    Clip adapter left:  %d\n", clip_adapter_left)
      printf("    Clip adapter right: %d\n", clip_adapter_right)
      printf("    Name:               %s\n", name)
    }
  }

  class SffReadData {
    var flowgram    : Array[Short] = null
    var flow_index  : Array[Byte]  = null
    var bases       : String       = null
    var quality     : Array[Byte]  = null

    def dump() : Unit = {
      printf("%s\n", bases)
    }
  }
}
