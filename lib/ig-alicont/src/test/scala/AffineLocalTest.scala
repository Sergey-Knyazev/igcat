import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.alicont.conts.affine
import ru.biocad.ig.common.resources.{MatrixTypes, MatrixLoader}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 15:56
 */
class AffineLocalTest extends FlatSpec with Matchers {
  def blosum = MatrixLoader.byName(MatrixTypes.BLOSUM62)

  "Alignment" should "pass affine local" in {
    val a = new affine.AlicontLocal(11, "PLEASANTLY", -10, -1, Scoring.loadMatrix(blosum))
    a.push("MEANLY")

    val (score1, (t1, q1)) = a.alignment()
    score1 should be (12)
    q1 should be ("MEAN")
    t1 should be ("LEAS")

    val b = new affine.AlicontLocal(11, "MEANLY", 0, -5, Scoring.loadMatrix(blosum))
    b.push("ME")
    b.push("A")
    b.push("LY")
    val (score2, (t2, q2)) = b.alignment()
    score2 should be (20)
    q2 should be ("MEA-LY")
    t2 should be ("MEANLY")
  }
}
