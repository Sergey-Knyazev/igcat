package ru.biocad.ig.regions.annotators

import scala.collection.immutable
import scala.collection.mutable.ArrayBuffer
import ru.biocad.ig.igcont.Container
import ru.biocad.ig.common.io.MarkingUtils
import ru.biocad.ig.common.io.fasta.FastaReader
import java.io.File
import com.typesafe.scalalogging.LazyLogging
import ru.biocad.ig.common.io.common.Sequence

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 13.01.14
 * Time: 15:24
 */
object RegionAnnotatorUtils extends LazyLogging {
  val regs = Array("FR1", "CDR1", "FR2", "CDR2", "FR3", "CDR3", "FR4")
  val reg = "Region"

  def createRegionsContainer(cont : Container, fasta : File, kabat : File) : Unit = {
    cont.addAnnotations(reg, regs)

    // Load fasta
    logger.info(s"Loading reference sequences from fasta: ${fasta.getName}")
    new FastaReader(fasta).foreach(rec => cont.push(rec.sequence, rec.name))

    // Load kabat
    logger.info(s"Loading region bounds annotations: ${kabat.getName}")
    MarkingUtils.readMarking(kabat).foreach(tpl => {
      val (name, arr) = tpl
      try {
        val record = cont.record(name)
        processMarkup(arr, (pos, reg) => record.setAnnotation(pos, 0, reg))
      }
      catch {
        case e : NoSuchElementException =>
          logger.warn(s"Record $name was not found in container")
      }
    })
  }

  def processMarkup(arr : Array[(Int, Int)], callback : (Int, Int) => Unit) : Unit = {
    arr.zipWithIndex.foreach(tpl => {
      val ((start, end), reg) = tpl
      if (start != -1 && end != -1) {
        (start to end).foreach(pos => {
          callback(pos, reg)
        })
      }
    })
  }

  def a2r(a : immutable.HashMap[String, String]) : Int =
    if (a.contains(reg))
      regs.indexOf(a(reg))
    else
      7

  def annotations2regions(anno : Iterable[(Char, immutable.HashMap[String, String])]) : Iterable[Int] = {
    val result = ArrayBuffer.fill[Int](anno.size)(0)

    anno.zipWithIndex.foreach(tpl => {
      val (node, i) = tpl
      result(i) = a2r(node._2)
    })

    result
  }

  def constructMarkup(anno : String) : String = {
    val r = constructMarkupArray(anno)
    val sb = new StringBuilder()
    (0 until r.size).foreach(i => {
      sb.append("\t%d\t%d".format(r(i)(0), r(i)(1)))
    })

    sb.toString()
  }

  def constructMarkupArray(anno : String) : Array[Array[Int]] = {
    val r = Array.fill[Array[Int]](7)(Array.fill[Int](2)(0))
    anno.zipWithIndex.foreach(tpl => {
      val (c, i) = tpl
      val ci = c.asDigit
      if (ci != 7) {
        if (r(ci)(0) == 0) {
          r(ci)(0) = i + 1
        }
        else {
          r(ci)(1) = i + 1
        }
      }
    })
    r
  }

  def restoreAnnotation(markup : String) : String = {
    val line = markup.split('\t').filter(_ != "")
    val arr = MarkingUtils.markup2array(line)
    val anno = ArrayBuffer.empty[Int]

    processMarkup(arr, (_, reg) => anno += reg)
    anno.mkString
  }

  def simpleFilter(anno : String) : String =
    restoreAnnotation(constructMarkup(anno))

  def savingSimpleFilter(anno : String) : String = {
    val r = constructMarkupArray(anno)
    (0 until r.size).foreach(i =>
      if (i != r.size - 1 && r(i)(1) > r(i + 1)(0)) {
        r(i)(1) = r(i + 1)(0) - 1
      }
    )
    val s = anno.map(_ => 7).toArray
    r.zipWithIndex.foreach(tpl => {
      val (pos, region) = tpl
      if (!(pos(0) == 0 && pos(1) == pos(0))) (pos(0) - 1 until pos(1)).foreach(i => s(i) = region)
    })

    s.mkString
  }

  def makeRefsString(rec : Sequence, references : Iterable[(String, Double)]) : String =
    "> %s\n%s\n\n".format(rec.name, references.map(tpl =>
      "%s\t%f\n".format(tpl._1, tpl._2)).mkString
    )

  def makeRegions(rec : Sequence, markup : String, regions : Iterable[Int]) : Option[String] = {
    def check(arr : Array[(Int, Int)]) : Boolean = {
      implicit def tplTwo2traversable[T](a : (T, T)) : Traversable[T] = {
        Seq(a._1, a._2)
      }
      arr.flatten.sliding(2).forall(a => a.head < a.last)
    }

    val line = markup.split('\t').filter(_ != "")
    val arr = MarkingUtils.markup2array(line)

    if (check(arr)) {
      val sb = StringBuilder.newBuilder
      regions.foreach(r => sb ++= rec.sequence.substring(arr(r)._1, arr(r)._2 + 1) + '$')
      Some(sb.mkString.dropRight(1))
    }
    else {
      None
    }
  }
}
