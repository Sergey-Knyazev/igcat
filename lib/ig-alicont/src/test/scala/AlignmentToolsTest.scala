import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.Alignment

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 04.06.14
 * Time: 9:36
 */
class AlignmentToolsTest extends FlatSpec with Matchers {
  "AlignmentTest" should "get aligned pattern" in {
    Alignment.tools.getAlignedPattern("ACGTGCGATGC--CAGTGC", "---TGCGAAGCCCCAG---") should be("TGCGATGCCAG")
  }
}
