package ru.biocad.ig.common.io.abi

import java.io._
import com.typesafe.scalalogging.slf4j.Logging
import ru.biocad.ig.common.io.common.{Sequence, SequenceReader}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 13.02.14
 * Time: 10:00
 */
class ABIReader(file : File) extends SequenceReader with Logging {
  private val data = readData(file)
  private val sequal = getSeq
  private val sequence = Sequence(name = file.getName, sequence = sequal._1, quality = sequal._2.toArray)

  override def close(): Unit = {}

  override def next(): Sequence = sequence

  override def hasNext: Boolean = true

  private def readData(file : File) : Array[Byte] = {
    val is = new FileInputStream(file)
    val bytes = Array.ofDim[Byte](is.available)
    is.read(bytes)
    is.close()
    bytes
  }

  private def getSeq : (String, Iterable[Byte]) = {
    val junk = getJunk
    val (len, pbas2, pcon) = getIndex(junk)
    val seq = data.slice(pbas2, pbas2 + len).map(_.toChar).mkString
    val qual = data.slice(pcon, pcon + len)

    (seq, qual)
  }

  private def getIndex(junk : Int) : (Int, Int, Int) = {
    val absIndexBase = 26
    val recordsCount = intAt(absIndexBase - 8 + junk)
    val indexBase = intAt(absIndexBase + junk)

    var pbas2 = 0
    var pbasCounter = 0

    var pcon = 0
    var pconCounter = 0

    (0 until recordsCount).foreach(record => {
      val recordStart = indexBase + (record * 28)
      val recordName = data.slice(recordStart, recordStart + 4)
      if (recordName.map(_.toChar).mkString == "PBAS") {
        pbasCounter += 1
        if (pbasCounter == 2) {
          pbas2 = indexBase + (record * 28) + 20
        }
      }
      if (recordName.map(_.toChar).mkString == "PCON") {
        pconCounter += 1
        if (pconCounter == 2) {
          pcon = indexBase + (record * 28) + 20
        }
      }
    })

    (intAt(pbas2 - 4), intAt(pbas2) + junk, intAt(pcon) + junk)
  }

  private def getJunk : Int =
    if (checkABI(0)) 0 else if (checkABI(128)) 128 else {
      val e = new IOException("Not an ABI file")
      logger.error("ABI problem", e)
      throw e
    }

  private def checkABI(i : Int) : Boolean =
    data(i) == 'A' && data(i + 1) == 'B' && data(i + 2) == 'I'

  private def intAt(pos : Int) : Int =
    new DataInputStream(new ByteArrayInputStream(data.slice(pos, pos + 4))).readInt()
}
