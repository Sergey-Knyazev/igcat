import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.alicont.conts.simple
import ru.biocad.ig.alicont.conts.affine
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 28.11.13
 * Time: 20:39
 */
class AlignmentTest extends FlatSpec with Matchers {
  val pathBlosum = getClass.getResource("/BLOSUM62.txt")
  val pathSimple = getClass.getResource("/NUC1.1.txt")

  "Alignment" should "pass simple global" in {
    val a = new simple.AlicontGlobal(11, "MEANLY", -5, Scoring.loadMatrix(pathBlosum))
    a.push("PLE")
    a.push("ASANT")
    a.push("LY")

    val (score, (t, q)) = a.alignment()

    score should be (8)
    q.replaceAll("-", "") should be ("PLEASANTLY")
    t.replaceAll("-", "") should be ("MEANLY")
  }

  it should "pass simple local" in {
    val a = new simple.AlicontLocal(11, "MEANLYLY", -5, Scoring.loadMatrix(pathBlosum))
    a.push("PLE")
    a.push("ASANT")
    a.push("LY")
    val (score1, (_, _)) = a.alignment()
    score1 should be (16)


    val b = new simple.AlicontLocal(11, "MEANLY", -5, Scoring.loadMatrix(pathBlosum))
    b.push("ME")
    b.push("A")
    b.push("L")
    b.push("Y")
    val (score3, (_, _)) = b.alignment()
    score3 should be (20)


    val c = new simple.AlicontLocal(11, "MEANLY", -5, Scoring.loadMatrix(pathBlosum))
    c.push("PLE")
    c.push("ASANT")
    c.push("LY")
    val (score2, (_, _)) = c.alignment()
    score2 should be (16)
  }

  it should "pass simple semiglobal" in {
    val a = new simple.AlicontSemiglobal(11, "CAGCACTTGGATTCTCGG", -1, Scoring.loadMatrix(pathSimple))
    a.push("CAGCGTGG")
    val (score, (q, t)) = a.alignment()
    score should be (4)
    q.replaceAll("-", "") should be ("CAGCACTTGGATTCTCGG")
    t.replaceAll("-", "") should be ("CAGCGTGG")

    val b = new simple.AlicontSemiglobal(11, "CAGCGAACACTTGGATTCTCGG", -1, Scoring.loadMatrix(pathSimple))
    b.push("CAGCGTGG")
    val (score2, (q2, t2)) = b.alignment()
    score2 should be (4)
    q2.replaceAll("-", "") should be ("CAGCGAACACTTGGATTCTCGG")
    t2.replaceAll("-", "") should be ("CAGCGTGG")

    val c = new simple.AlicontSemiglobal(11, "ACGTCAT", -1, Scoring.loadMatrix(pathSimple))
    c.push("TCATGCA")
    val (score3, (q3, t3)) = c.alignment()
    score3 should be (4)
    q3.replaceAll("-", "") should be ("ACGTCAT")
    t3.replaceAll("-", "") should be ("TCATGCA")

    val d = new simple.AlicontSemiglobal(11, "ACAGATA", -1, Scoring.loadMatrix(pathSimple))
    d.push("AGT")
    val (score4, (q4, t4)) = d.alignment()
    score4 should be (2)
    q4.replaceAll("-", "") should be ("ACAGATA")
    t4.replaceAll("-", "") should be ("AGT")

    val e = new simple.AlicontSemiglobal(11, "EASPTMEALYLY", -5, Scoring.loadMatrix(pathBlosum))
    e.push("EAS")
    e.push("LY")
    val (score6, (t6, q6)) = e.alignment()
    score6 should be (15)
    q6.replaceAll("-", "") should be ("EASLY")
    t6.replaceAll("-", "") should be ("EASPTMEALYLY")
  }

  it should "pass affine global" in {
    val a = new affine.AlicontGlobal(11, "MEANLY", 0, -5, Scoring.loadMatrix(pathBlosum))
    a.push("PLE")
    a.push("ASANT")
    a.push("LY")
    val (score, (t, q)) = a.alignment()
    score should be (8)
    q.replaceAll("-", "") should be ("PLEASANTLY")
    t.replaceAll("-", "") should be ("MEANLY")

    val a1 = new affine.AlicontGlobal(100, "SAS", -9, -1, Scoring.loadMatrix(pathBlosum))
    a1.push("SS")
    val (score1, (t1, q1)) = a1.alignment()
    score1 should be (-2)
    t1.replaceAll("-", "") should be ("SAS")
    q1.replaceAll("-", "") should be ("SS")

    val b = new affine.AlicontGlobal(100, "SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCT" +
                                          "YSPALNKMFCQLAKTCPVQLWVDSTPPPGTRV" +
                                          "RAMAIYKQSQHMTEVVRRCPHHERCSDSDGLA" +
                                          "PPQHLIRVEGNLRVEYLDDRNTFRHSVVVPYE" +
                                          "PPEVGSDCTTIHYNYMCNSSCMGGMNRRPILT" +
                                          "IITLEDSSGNLLGRNSFEVRVCACPGRDRRTE" +
                                          "EENL", -9, -1, Scoring.loadMatrix(pathBlosum))
    b.push("DDYAGKYGLQLDFQQNGTAKSVVKRCPHHERSVEPG")
    val (score2, (_, _)) = b.alignment()
    score2 should be (-69)

    val g = new affine.AlicontGlobal(100, "SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCT" +
                                          "YSPALNKMFCQLAKTCPVQLWVDSTPPPGTRV" +
                                          "RAMAIYKQSQHMTEVVRRCPHHERCSDSDGLA" +
                                          "PPQHLIRVEGNLRVEYLDDRNTFRHSVVVPYE" +
                                          "PPEVGSDCTTIHYNYMCNSSCMGGMNRRPILT" +
                                          "IITLEDSSGNLLGRNSFEVRVCACPGRDRRTE" +
                                          "EENL", -9, -1, Scoring.loadMatrix(pathBlosum))
    g.push("RHSVCVPYEGPQVGTECTTVLYNYMCNSSCMGGMNRRPILTIITLETPQGLLLGRRCFEVRV")
    g.push("")
    val (score7, (_, _)) = g.alignment()
    score7 should be (110)
  }

  it should "pass affine local" in {
    val a = new affine.AlicontLocal(11, "PLEASANTLY", -10, -1, Scoring.loadMatrix(pathBlosum))
    a.push("MEANLY")

    val (score1, (t1, q1)) = a.alignment()
    score1 should be (12)
    q1 should be ("MEAN")
    t1 should be ("LEAS")


    val b = new affine.AlicontLocal(11, "MEANLY", 0, -5, Scoring.loadMatrix(pathBlosum))
    b.push("ME")
    b.push("A")
    b.push("LY")
    val (score2, (t2, q2)) = b.alignment()
    score2 should be (20)
    q2 should be ("MEA-LY")
    t2 should be ("MEANLY")
  }

  it should "pass affine semiglobal" in {
    val a = new affine.AlicontSemiglobal(11, "CAGCACTTGGATTCTCGG", 0, -1, Scoring.loadMatrix(pathSimple))
    a.push("CAGCGTGG")
    val (score, (q, t)) = a.alignment()
    score should be (4)
    q.replaceAll("-", "") should be ("CAGCACTTGGATTCTCGG")
    t.replaceAll("-", "") should be ("CAGCGTGG")

    val b = new affine.AlicontSemiglobal(11, "CAGCGAACACTTGGATTCTCGG", 0, -1, Scoring.loadMatrix(pathSimple))
    b.push("CAGCGTGG")
    val (score2, (q2, t2)) = b.alignment()
    score2 should be (4)
    q2.replaceAll("-", "") should be ("CAGCGAACACTTGGATTCTCGG")
    t2.replaceAll("-", "") should be ("CAGCGTGG")


    val c = new affine.AlicontSemiglobal(11, "ACGTCAT", 0, -1, Scoring.loadMatrix(pathSimple))
    c.push("TCATGCA")
    val (score3, (q3, t3)) = c.alignment()
    score3 should be (4)
    q3.replaceAll("-", "") should be ("ACGTCAT")
    t3.replaceAll("-", "") should be ("TCATGCA")


    val d = new affine.AlicontSemiglobal(11, "ACAGATA", 0, -1, Scoring.loadMatrix(pathSimple))
    d.push("AGT")
    val (score4, (q4, t4)) = d.alignment()
    score4 should be (2)
    q4.replaceAll("-", "") should be ("ACAGATA")
    t4.replaceAll("-", "") should be ("AGT")


    val e = new affine.AlicontSemiglobal(40, "AAAAAAGAAAAAAAATGCCAAAAAAATTGG", 0, -1, Scoring.loadMatrix(pathSimple))
    e.push("AAAAAAAAAAAAAAAAAAAAAATCTGTCGTGTTGGTTT")
    val (_, (q5, t5)) = e.alignment()
    q5.replaceAll("-", "") should be ("AAAAAAGAAAAAAAATGCCAAAAAAATTGG")
    t5.replaceAll("-", "") should be ("AAAAAAAAAAAAAAAAAAAAAATCTGTCGTGTTGGTTT")


    val f = new affine.AlicontSemiglobal(11, "EASPTMEALYLY", 0, -5, Scoring.loadMatrix(pathBlosum))
    f.push("EAS")
    f.push("LY")
    val (score6, (_, _)) = f.alignment()
    score6 should be (15)
  }
}
