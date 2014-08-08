package ru.biocad.ig.regions

import ru.biocad.ig.regions.annotators.{RegionAnnotatorUtils, RegionAnnotator}
import ru.biocad.ig.common.io.fasta.FastaReader
import java.io.{FileWriter, BufferedWriter, File}
import ru.biocad.ig.common.io.CachedMkdir
import ru.biocad.ig.alicont.algorithms.AlgorithmType._
import ru.biocad.ig.common.io.common.{SequenceType, Sequence}
import scala.Some
import com.typesafe.scalalogging.LazyLogging

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 07.03.14
 * Time: 13:50
 */
object RegionsRunner extends LazyLogging {
  private def constructAnnotator(amino : Boolean = false, fasta : File = null, kabat : File = null,
                                 gap_open : Double = -10, gap_ext : Double = -1, gap : Double = -5,
                                 scoring : Array[Array[Double]] = null,
                                 align : AlgorithmType = SEMIGLOBAL) : RegionAnnotator =
    new RegionAnnotator("Name", if (amino) SequenceType.AMINO else SequenceType.NUCLEO,
      fasta, kabat,
      (gap_open, gap_ext, gap),
      scoring, align)

  def run(amino : Boolean = false, fasta : File = null, kabat : File = null, source : File = null,
          count : Int = 3, gap_open : Double = -10, gap_ext : Double = -1, gap : Double = -5,
          scoring : Array[Array[Double]] = null, align : AlgorithmType = SEMIGLOBAL,
          marking : Boolean = false, filter : Boolean = false, outdir : File = null,
          par : Boolean = false, add_group : Boolean = false) : Unit = {
    val r = constructAnnotator(amino, fasta, kabat, gap_open, gap_ext, gap, scoring, align)
    val data = new FastaReader(source)
    val callback : ((Sequence, String, Iterable[(String, Double)]) => Unit) = if (outdir == null) {
      logger.info("Outdir is not specified; writing to STDOUT")

      (rec : Sequence, anno : String, _ : Iterable[(String, Double)]) => {
        if (!marking) {
          printf("> %s\n%s\n%s\n\n", rec.name, rec.sequence, anno)
        }
        else {
          printf("%s%s\n", rec.name, RegionAnnotatorUtils.constructMarkup(anno))
        }
      }
    }
    else {
      logger.info(s"Writing to ${outdir.getName}/")
      val refdir = new File(outdir, RegionsDataConstants.REFINFO_DIR)
      CachedMkdir.mkdirp(refdir.getAbsolutePath)

      val fastaLike = new BufferedWriter(new FileWriter(new File(outdir, RegionsDataConstants.FASTA_LIKE_FILE)))
      val igblastLike = new BufferedWriter(new FileWriter(new File(outdir, RegionsDataConstants.IGBLAST_LIKE_FILE)))
      val cdr3 = new BufferedWriter(new FileWriter(new File(outdir, RegionsDataConstants.CDR3_FILE)))
      val cdr123 = new BufferedWriter(new FileWriter(new File(outdir, RegionsDataConstants.CDR123_FILE)))
      val allregs = new BufferedWriter(new FileWriter(new File(outdir, RegionsDataConstants.REGIONS_FILE)))

      (rec : Sequence, anno : String, references : Iterable[(String, Double)]) => {
        val markup = RegionAnnotatorUtils.constructMarkup(anno)
        val cdr3seq = RegionAnnotatorUtils.makeRegions(rec, markup, Array(5))
        val cdr123seq = RegionAnnotatorUtils.makeRegions(rec, markup, Array(1,3,5))
        val allregsseq = RegionAnnotatorUtils.makeRegions(rec, markup, 0 to 6)

        val group = if (add_group) {
          references.headOption match {
            case Some((r : String, _ : Double)) =>
              "-%s".format(r.split("-|_").head)
            case None =>
              ""
          }
        }
        else ""

        val refInfo = new BufferedWriter(new FileWriter(new File(refdir, "%s.txt".format(rec.name))))

        fastaLike.write("> %s\n%s\n%s\n\n".format(rec.name, rec.sequence, anno))
        fastaLike.flush()

        igblastLike.write("%s%s\n".format(rec.name, markup))
        igblastLike.flush()

        cdr123seq match {
          case Some(s) =>
            cdr123.write(">%s\n%s\n\n".format(rec.name, s))
            cdr123.flush()
          case None =>
        }
        cdr3seq match {
          case Some(s) =>
            cdr3.write(">%s\n%s\n\n".format(rec.name, s))
            cdr3.flush()
          case None =>
        }
        allregsseq match {
          case Some(s) =>
            allregs.write(">%s%s\n%s\n\n".format(rec.name, group, s.replaceAll("\\$", " ")))
            allregs.flush()
          case None =>
        }

        refInfo.write(RegionAnnotatorUtils.makeRefsString(rec, references))
        refInfo.close()
      }
    }

    val dataAdapter = if (par) data.toSeq.par else data
    dataAdapter.foreach(rec => {
      logger.info(s"Processing ${rec.name}")
      process(r, rec, count, filter, callback)
    })
  }

  private def process(r : RegionAnnotator, rec : Sequence,
                      count : Int, filter : Boolean, callback : (Sequence, String, Iterable[(String, Double)]) => Unit) : Unit = {
    val full_anno = r.annotate(rec.sequence, count)
    val raw_anno = RegionAnnotatorUtils.annotations2regions(full_anno.annotations).mkString
    val anno = if (filter) RegionAnnotatorUtils.savingSimpleFilter(raw_anno) else raw_anno

    callback(rec, anno, full_anno.references.toArray.sortBy(-_._2))
  }
}
