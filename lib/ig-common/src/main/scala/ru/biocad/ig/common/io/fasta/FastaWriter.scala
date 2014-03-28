package ru.biocad.ig.common.io.fasta

import java.io.{FileOutputStream, File}
import ru.biocad.ig.common.io.common.{Sequence, SequenceWriter}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 09.02.14
 * Time: 0:02
 */
class FastaWriter(out : File) extends SequenceWriter {
  private val stream = new FileOutputStream(out)

  def this(filename : String) = {
    this(new File(filename))
  }

  override def writeSequence(rec: Sequence): Unit = {
    stream.synchronized {
      writeToStream(rec)
    }
  }

  override def writeSequences(recs: Iterable[Sequence]): Unit = {
    stream.synchronized {
      recs.foreach(writeToStream)
    }
  }

  override def close() : Unit = stream.close()

  private def writeToStream(rec : Sequence) : Unit = {
    stream.write((">" + rec.name + "\n").getBytes)
    stream.write((rec.sequence + "\n\n").getBytes)
  }
}
