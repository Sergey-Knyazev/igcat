import ru.biocad.ig.common.algorithms.{SmallTools, PairSeqTools}
import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.common.io.common.Sequence

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

    Sequence(sequence = seq).translated should be (tseq)
  }

  it should "get reverse complement right" in {
    Sequence(sequence = "").reverse_complement should be ("")
    Sequence(sequence = "ACGT").reverse_complement should be ("ACGT")
    Sequence(sequence = "ACNG").reverse_complement should be ("CNGT")
    Sequence(sequence = "AC--TTG-C").reverse_complement should be("G-CAA--GT")
  }

  it should "strip endings right" in {
    SmallTools.stripEndings("-!adasd--^$%^!fdafs!#-", "!@#$%^&*()_+-=") should be("adasd--^$%^!fdafs")
  }

  "PairSeq tools" should "find longest ig substring right" in {
    val s1 = "SDFGHJHGFDSTYTNBCDRTUYKJNMB"
    val (b1, _, l1) = PairSeqTools.longestCommonSubstring(s1, "DFGHHGFDSTYTNBERTYUIOK")
    s1.substring(b1, b1 + l1) should be ("HGFDSTYTNB")

    val s2 = "QWERTYUIOP"
    val (b2, _, l2) = PairSeqTools.longestCommonSubstring(s2, s2)
    s2.substring(b2, b2 + l2) should be (s2)

    val s3 = "QWERT"
    val (b3, _, l3) = PairSeqTools.longestCommonSubstring(s2, s3)
    s2.substring(b3, b3 + l3) should be (s3)

    val s4 = "YUIOP"
    val (b4, _, l4) = PairSeqTools.longestCommonSubstring(s4, s2)
    s4.substring(b4, b4 + l4) should be (s4)
  }
}
