import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.alicont.conts.simple
import ru.biocad.ig.common.resources.{MatrixTypes, MatrixLoader}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 15:53
 */
class SimpleLocalTest extends FlatSpec with Matchers {
  def blosum = MatrixLoader.byName(MatrixTypes.BLOSUM62)

  "Alignment" should "pass simple local" in {
    val a = new simple.AlicontLocal(11, "MEANLYLY", -5, Scoring.loadMatrix(blosum))
    a.push("PLE")
    a.push("ASANT")
    a.push("LY")
    val (score1, (_, _)) = a.alignment()
    score1 should be (16)


    val b = new simple.AlicontLocal(11, "MEANLY", -5, Scoring.loadMatrix(blosum))
    b.push("ME")
    b.push("A")
    b.push("L")
    b.push("Y")
    val (score3, (_, _)) = b.alignment()
    score3 should be (20)


    val c = new simple.AlicontLocal(11, "MEANLY", -5, Scoring.loadMatrix(blosum))
    c.push("PLE")
    c.push("ASANT")
    c.push("LY")
    val (score2, (_, _)) = c.alignment()
    score2 should be (16)
  }
}
