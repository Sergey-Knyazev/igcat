
import ru.biocad.ig.alicont.algorithms.AlgorithmType._
import ru.biocad.ig.alicont.common.Scoring
import com.typesafe.scalalogging.LazyLogging
import java.io._
import ru.biocad.ig.regions.RegionsRunner

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 31.10.13
 * Time: 9:59
 */
object Main extends LazyLogging {
  private case class Config(amino : Boolean = false, fasta : File = null, kabat : File = null, source : File = null,
                            count : Int = 3, gap_open : Double = -10, gap_ext : Double = -1, gap : Double = -5,
                            scoring : Array[Array[Double]] = null, align : AlgorithmType = SEMIGLOBAL,
                            marking : Boolean = false, filter : Boolean = false, outdir : File = null,
                            add_group : Boolean = false, par : Boolean = false, debug : Boolean = false)

  def main(args : Array[String]) = {
    val parser = getParser

    parser.parse(args, Config()) match {
      case Some(config) =>
        try {
          RegionsRunner.run(config.amino, config.fasta, config.kabat, config.source,
            config.count, config.gap_open, config.gap_ext, config.gap,
            config.scoring, config.align, config.marking, config.filter,
            config.outdir, config.par, config.add_group)
        } catch {
          case e : Exception =>
            logger.error(s"Fatal error: ${e.getMessage}")
            if (config.debug) {
              e.printStackTrace()
            }
        }
      case None =>
        parser.showUsage
    }
  }

  private def getParser : scopt.OptionParser[Config] = new scopt.OptionParser[Config]("ig-regions") {
    head("ig-regions", "1.0-SNAPSHOT")
    note("Required:")
    opt[File]('s', "source") required() action {(s, c) => c.copy(source = s)} validate {x =>
      if (x.canRead) success  else failure("Source file does not exists")
    } text "file to annotate [fasta]"
    opt[File]('r', "reference") required() action {(s, c) => c.copy(fasta = s)} validate {x =>
      if (x.canRead) success  else failure("Reference file does not exists")
    } text "reference file [fasta]"
    opt[File]('m', "marking") required() action {(s, c) => c.copy(kabat = s)} validate {x =>
      if (x.canRead) success  else failure("Marking file does not exists")
    } text "reference marking [igblast marking format]"
    note("Optional:")
    opt[File]("outdir") action {(x, c) => c.copy(outdir = x)} validate {x =>
      if (x.canRead) success else failure("Output directory does not exists")
    } text "output directory"
    opt[Unit]("par") action {(_, c) => c.copy(par = true)} text "Use parallel mode (highly experimental)"
    opt[Unit]("group") action {(_, c) => c.copy(add_group = true)} text "Add germline group to name"
    opt[Unit]('a', "amino") action {(_, c) => c.copy(amino = true)} text "use amino acid data"
    opt[Unit]('l', "igblast-like") action {(_, c) => c.copy(marking = true)} text "output as igblast marking"
    opt[Unit]("filter") action {(s, c) => c.copy(filter = true)} text "enable simple filtration (default: disabled)"
    opt[Int]('n', "alignments") action {(s, c) => c.copy(count = s)} text "number of using alignments for annotation (default: 3)"
    note("\n  alignment parameters\n")
    opt[File]('x', "matrix") action {(x, c) => c.copy(scoring = Scoring.loadMatrix(x))} validate {x =>
      if (x.canRead) success else failure("Scoring matrix file does not exists")
    } text "use external alignment matrix [txt]"
    opt[Double]('g', "gap") action {(s, c) => c.copy(gap = s)} text "simple gap score (default: -5)"
    opt[Double]('o', "gap-open") action {(s, c) => c.copy(gap_open = s)} text "affine open gap score (default: -10)"
    opt[Double]('e', "gap-ext") action {(s, c) => c.copy(gap_ext = s)} text "affine extension gap score (default: -1)"
    note("\n  alignment algorithms\n")
    opt[Unit]("global") action {(_, c) => c.copy(align = GLOBAL)} text "use global alignment"
    opt[Unit]("local") action {(_, c) => c.copy(align = LOCAL)} text "use local alignment"
    opt[Unit]("semiglobal") action {(_, c) => c.copy(align = SEMIGLOBAL)} text "use semiglobal alignment (default)"
    opt[Unit]("affine-global") action {(_, c) => c.copy(align = AFFINE_GLOBAL)} text "use global alignment"
    opt[Unit]("affine-local") action {(_, c) => c.copy(align = AFFINE_LOCAL)} text "use local alignment"
    opt[Unit]("affine-semiglobal") action {(_, c) => c.copy(align = AFFINE_SEMIGLOBAL)} text "use semiglobal alignment"
    note("Debug:")
    opt[Unit]("debug") action {(_, c) => c.copy(debug = true)} text "show exceptions on fail"
    note("Help:")
    help("help") text "this message"
  }
}
