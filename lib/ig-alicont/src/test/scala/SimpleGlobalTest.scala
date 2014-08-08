import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.alicont.conts.simple
import ru.biocad.ig.common.resources.{MatrixTypes, MatrixLoader}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 15:46
 */
class SimpleGlobalTest extends FlatSpec with Matchers {
  def blosum = MatrixLoader.byName(MatrixTypes.BLOSUM62)

  "Alignment" should "pass simple global" in {
    val a = new simple.AlicontGlobal(11, "MEANLY", -5, Scoring.loadMatrix(blosum))
    a.push("PLE")
    a.push("ASANT")
    a.push("LY")

    val (score, (t, q)) = a.alignment()

    score should be (8)
    q.replaceAll("-", "") should be ("PLEASANTLY")
    t.replaceAll("-", "") should be ("MEANLY")
  }
}
