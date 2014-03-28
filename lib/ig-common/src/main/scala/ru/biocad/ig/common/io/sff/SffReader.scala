package ru.biocad.ig.common.io.sff

import java.io._
import ru.biocad.ig.common.io.common.{Sequence, SequenceReader}
import ru.biocad.ig.common.io.sff.common.{SffRead, SffHeader}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 12.02.14
 * Time: 14:20
 */
class SffReader(file : File) extends SequenceReader {
  private val data   = new DataInputStream(new FileInputStream(file))
  private val header = SffHeader.fromDataInput(data)
  private var current = 0

  override def close(): Unit = data.close()

  override def next(): Sequence = {
    data.synchronized {
      val read = SffRead.fromDataInput(data, header)
      current += 1
      read.sequence
    }
  }

  override def hasNext: Boolean = current != header.nreads

  override def size : Int = header.nreads
}
