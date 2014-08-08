import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.alicont.conts.affine
import ru.biocad.ig.common.resources.{MatrixLoader, MatrixTypes}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 15:56
 */
class AffineSemiglobalTest extends FlatSpec with Matchers {
  def blosum = MatrixLoader.byName(MatrixTypes.BLOSUM62)
  def simple = MatrixLoader.byName(MatrixTypes.NUC11)

  "Alignment" should "pass affine semiglobal" in {
    val a = new affine.AlicontSemiglobal(11, "CAGCACTTGGATTCTCGG", -1, -1, Scoring.loadMatrix(simple))
    a.push("CAGCGTGG")
    val (score, (q, t)) = a.alignment()
    score should be (3)
    q should be ("CAGCA-CTTGGATTCTCGG")
    t should be ("---CAGCGTGG--------")

    val b = new affine.AlicontSemiglobal(11, "EASPTMEALYLY", -4, -1, Scoring.loadMatrix(blosum))
    b.push("EAS")
    b.push("LY")
    val (score2, (q2, t2)) = b.alignment()
    score2 should be (15)
    q2 should be ("EASPTMEA-LYLY")
    t2 should be ("------EASLY--")

    val c = new affine.AlicontSemiglobal(11, "EASPTMEALYLY", 0, 0, Scoring.loadMatrix(blosum))
    c.push("EAS")
    c.push("LY")
    val (score3, (q3, t3)) = c.alignment()
    score3 should be (24)
    q3 should be ("EASPTMEALYLY")
    t3 should be ("EAS-----LY--")
  }
}
