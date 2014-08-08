import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.alicont.conts.affine
import ru.biocad.ig.common.resources.{MatrixTypes, MatrixLoader}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 15:55
 */
class AffineGlobalTest extends FlatSpec with Matchers {
  def blosum = MatrixLoader.byName(MatrixTypes.BLOSUM62)

  "Alignment" should "pass affine global" in {
    val a = new affine.AlicontGlobal(10, "PRTEINS", -10, -1, Scoring.loadMatrix(blosum))
    a.push("PRTWPSEIN")
    val (score, (s, t)) = a.alignment()
    score should be (8)
    s should be ("PRT---EINS")
    t should be ("PRTWPSEIN-")

    val b = new affine.AlicontGlobal(11, "PLEASANTLY", -5, 0, Scoring.loadMatrix(blosum))
    b.push("MEANLY")
    val (score2, (s2, t2)) = b.alignment()
    score2 should be (13)
  }
}
