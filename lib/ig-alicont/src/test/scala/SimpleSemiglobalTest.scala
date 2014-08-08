import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.alicont.conts.simple
import ru.biocad.ig.common.resources.{MatrixLoader, MatrixTypes}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 15:54
 */
class SimpleSemiglobalTest extends FlatSpec with Matchers {
  def blosum = MatrixLoader.byName(MatrixTypes.BLOSUM62)
  def nuc11 = MatrixLoader.byName(MatrixTypes.NUC11)

  "Alignment" should "pass simple semiglobal" in {
    val a = new simple.AlicontSemiglobal(11, "CAGCACTTGGATTCTCGG", -1, Scoring.loadMatrix(nuc11))
    a.push("CAGCGTGG")
    val (score, (q, t)) = a.alignment()
    score should be (4)
    q.replaceAll("-", "") should be ("CAGCACTTGGATTCTCGG")
    t.replaceAll("-", "") should be ("CAGCGTGG")

    val b = new simple.AlicontSemiglobal(11, "CAGCGAACACTTGGATTCTCGG", -1, Scoring.loadMatrix(nuc11))
    b.push("CAGCGTGG")
    val (score2, (q2, t2)) = b.alignment()
    score2 should be (4)
    q2.replaceAll("-", "") should be ("CAGCGAACACTTGGATTCTCGG")
    t2.replaceAll("-", "") should be ("CAGCGTGG")

    val c = new simple.AlicontSemiglobal(11, "ACGTCAT", -1, Scoring.loadMatrix(nuc11))
    c.push("TCATGCA")
    val (score3, (q3, t3)) = c.alignment()
    score3 should be (4)
    q3.replaceAll("-", "") should be ("ACGTCAT")
    t3.replaceAll("-", "") should be ("TCATGCA")

    val d = new simple.AlicontSemiglobal(11, "ACAGATA", -1, Scoring.loadMatrix(nuc11))
    d.push("AGT")
    val (score4, (q4, t4)) = d.alignment()
    score4 should be (2)
    q4.replaceAll("-", "") should be ("ACAGATA")
    t4.replaceAll("-", "") should be ("AGT")

    val e = new simple.AlicontSemiglobal(11, "EASPTMEALYLY", -5, Scoring.loadMatrix(blosum))
    e.push("EAS")
    e.push("LY")
    val (score6, (t6, q6)) = e.alignment()
    score6 should be (15)
    q6.replaceAll("-", "") should be ("EASLY")
    t6.replaceAll("-", "") should be ("EASPTMEALYLY")
  }
}
