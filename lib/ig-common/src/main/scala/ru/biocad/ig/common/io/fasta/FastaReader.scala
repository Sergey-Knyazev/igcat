package ru.biocad.ig.common.io.fasta

import ru.biocad.ig.common.io.common.{SequenceReader, Sequence}
import java.io.{IOException, FileReader, BufferedReader, File}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 08.02.14
 * Time: 23:59
 */
class FastaReader(file : File, saveGaps : Boolean = false) extends SequenceReader {
  private val gaps = saveGaps
  private val reader = new BufferedReader(new FileReader(file))
  private var nextName : String = ""

  skipSpaces()

  override def hasNext : Boolean = nextName != null

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

      val sequence = seq.mkString.toUpperCase
      Sequence(name, if (gaps) sequence else sequence.replaceAll("-", ""))
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
  def sizeOf(file : File) : Int = {
    val fr = new FastaReader(file)
    var counter = 0
    fr.foreach(_ => counter += 1)
    fr.close()
    counter
  }

  def sizeOf(filename : String) : Int = sizeOf(new File(filename))
}
