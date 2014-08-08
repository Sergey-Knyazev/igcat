import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.alicont.ConsensusAlignment

class ConsensusAlignmentTest extends FlatSpec with Matchers {
  "ConsensusAlignment" should "work on empty sequences" in {
    intercept[IllegalArgumentException] { ConsensusAlignment.merge(Seq.empty[String]) }
    ConsensusAlignment.merge(Seq("")) should be ("")
    ConsensusAlignment.merge(Seq("", "")) should be ("")
  }

  it should "reject sequences with gaps" in {
    intercept[IllegalArgumentException] { ConsensusAlignment.merge(Seq("aa", "a-", "-b")) }
  }

  it should "align sequence on itself" in {
    ConsensusAlignment.merge(Seq("ABC")) should be ("ABC")
    ConsensusAlignment.merge(Seq("ABC", "ABC")) should be ("ABC")
    ConsensusAlignment.merge(Seq("ABC", "ABC", "ABC")) should be ("ABC")
  }


  it should "align sequence on its subsequence" in {
    ConsensusAlignment.merge(Seq("SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCTYSPALNKMFCQ", "YGFRLGFLHSGTAKS")) should be ("SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCTYSPALNKMFCQ")
    ConsensusAlignment.merge(Seq("LGFLH", "YGFRLGFLHSGTAKS", "SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCTYSPALNKMFCQ")) should be ("SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCTYSPALNKMFCQ")
  }

  it should "merge toy strings" in {
    ConsensusAlignment.merge(Seq("AB", "BC", "CD")) should be ("ABCD")
  }

  it should "merge realistic reads" in {
    ConsensusAlignment.merge(Seq(
      "SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCTYSPALNKMFCQLAKTCPVQLWVDSTPPPGTRVRAMAIYKQSQHMTEVVRRCPHHERCS",
      "AIYKQSQHMTEVVRRCPHHERCSDSDGLAPPQHLIRVEGNLRVEYLDDRNTFRHSVVVPYEPPEV",
      "DRNTFRHSVVVPYEPPEVGSDCTTIHYNYMCNSSCMGGMNRRPILTIITLEDSSGNLLGRNSFEVRVCACPGRDRRTEEENL")) should
      be ("SSSVPSQKTYQGSYGFRLGFLHSGTAKSVTCTYSPALNKMFCQLAKTCPVQLWVDSTPPPGTRVRAMAIYKQSQHMTEVVRRCPHHERCSDSDGLAPPQHLIRVEGNLRVEYLDDRNTFRHSVVVPYEPPEVGSDCTTIHYNYMCNSSCMGGMNRRPILTIITLEDSSGNLLGRNSFEVRVCACPGRDRRTEEENL")
  }
}
