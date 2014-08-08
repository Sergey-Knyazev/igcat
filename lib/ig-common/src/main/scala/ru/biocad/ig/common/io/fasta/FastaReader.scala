package ru.biocad.ig.common.io.fasta

import ru.biocad.ig.common.io.common.{SourceReader, SequenceReader, Sequence}
import java.io._
import scala.io.Source

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 08.02.14
 * Time: 23:59
 */
class FastaReader(source : Source, saveGaps : Boolean = false) extends SequenceReader {
  private val gaps = saveGaps
  private val reader = new SourceReader(source)
  private var nextName : String = ""

  def this(file : File) = {
    this(Source.fromFile(file), false)
  }

  def this(file : File, saveGaps : Boolean) = {
    this(Source.fromFile(file), saveGaps)
  }

  skipSpaces()

  override def hasNext : Boolean =
    nextName != null

  override def next() : Sequence = {
    reader.synchronized {
      if (!nextName.startsWith(">")) {
        throw new IOException("Wrong fasta format")
      }

      val (name, seq) = (nextName.drop(1).trim, StringBuilder.newBuilder)

      nextName = ""
      while (nextName != null && !nextName.startsWith(">")) {
        seq ++= nextName
        nextName = reader.readLine()
      }

      val sequence = seq.mkString.toUpperCase.replaceAll("\\s", "")
      Sequence(name = name, sequence = if (gaps) sequence else sequence.replaceAll("-", ""))
    }
  }

  override def close() : Unit = reader.close()

  private def skipSpaces() : Unit = {
    var spaces : String = ""
    while (spaces != null && spaces.isEmpty) {
      spaces = reader.readLine()
    }

    nextName = spaces
  }
}

object FastaReader {
  def sizeOf(file : File) : Int =
    new FastaReader(file).count(_ => true)

  def sizeOf(filename : String) : Int = sizeOf(new File(filename))
}
