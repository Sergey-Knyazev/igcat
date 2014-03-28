import ru.biocad.ig.common.algorithm.{PairSeqTools, SeqTools}
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 13.02.14
 * Time: 13:04
 */
class AlgorithmTests extends FlatSpec with Matchers {
  "Seq Tools" should "translate right" in {
    val seq = "CAGGCTGTGGTGACCCAGGAGCCATCCCTGTCAGTGTCTCCAGGAGGGACGGTCACCCTCACCTGCGGCCTCAGCTCTGGGTCTGTCACTTCCAGTAATT"
    val tseq = "QAVVTQEPSLSVSPGGTVTLTCGLSSGSVTSSN"

    SeqTools.translateString(seq) should be (tseq)
  }

  it should "get reverse complement right" in {
    SeqTools.reverseComp("") should be ("")
    SeqTools.reverseComp("ACGT") should be ("ACGT")
    SeqTools.reverseComp("ACNG") should be ("CNGT")
    SeqTools.reverseComp("AC--TTG-C") should be("G-CAA--GT")
  }

  it should "strip endings right" in {
    SeqTools.stripEndings("-!adasd--^$%^!fdafs!#-", "!@#$%^&*()_+-=") should be("adasd--^$%^!fdafs")
  }

  "PairSeq tools" should "find longest ig substring right" in {
    val s1 = "SDFGHJHGFDSTYTNBCDRTUYKJNMB"
    val (b1, _, l1) = PairSeqTools.longestSubstr(s1, "DFGHHGFDSTYTNBERTYUIOK")
    s1.substring(b1, b1 + l1) should be ("HGFDSTYTNB")

    val s2 = "QWERTYUIOP"
    val (b2, _, l2) = PairSeqTools.longestSubstr(s2, s2)
    s2.substring(b2, b2 + l2) should be (s2)

    val s3 = "QWERT"
    val (b3, _, l3) = PairSeqTools.longestSubstr(s2, s3)
    s2.substring(b3, b3 + l3) should be (s3)

    val s4 = "YUIOP"
    val (b4, _, l4) = PairSeqTools.longestSubstr(s4, s2)
    s4.substring(b4, b4 + l4) should be (s4)
  }

  it should "get aligned patterns right" in {
    PairSeqTools.getAlignedPattern("ACGTGCGATGC--CAGTGC", "---TGCGAAGCCCCAG---") should be("TGCGATGCCAG")
  }
}
