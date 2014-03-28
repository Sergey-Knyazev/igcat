import ru.biocad.ig.alicont.algorithms.AlgorithmType
import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.igcont.Container
import org.scalatest.{Matchers, FlatSpec}
import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 30.10.13
 * Time: 15:25
 */
class ContainerTest extends FlatSpec with Matchers {
  val path : String = "data/matrix/NUC4.4.txt"

  "Container" should "add sequences easy" in {
    val cont = new Container("ACGT", 'N')

    cont.push("ACGTAGCTACGATGCGACGACGACGAGGATGTTGGTTT", "Seq1")
    cont.push("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "SeqA")
    cont.push("AAAAAAAAAAAAAAAAAAAAAATCTGTCGTGTTGGTTT", "Seq2")

    cont.size should be (3)
  }


  it should "have both name and handle addresation" in {
    val cont = new Container("ACGT", 'N')

    cont.push("ACGTAGCTACGATGCGACGACGACGAGGATGTTGGTTT", "Seq1")
    cont.push("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "SeqA")
    cont.push("AAAAAAAAAAAAAAAAAAAAAATCTGTCGTGTTGGTTT", "Seq2")

    cont.seq("Seq2") should be(cont.seq(2))
  }

  it should "has correct annotations" in {
    val cont = new Container("ACGT", 'N', Array("A1", "A2", "A3"), 7)

    cont.push("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGT", "Seq1")
    cont.push("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "SeqA")

    val rec = cont.record("SeqA")
    rec.setAnnotation(2, "A1", "42")
    rec.setAnnotation(3, "A1", "42")
    rec.setAnnotation(5, "A1", "38")
    rec.setAnnotation(3, "A2", ":)")

    val d = cont.data("SeqA").toArray
    d(2)._2("A1") should be ("42")
    d(3)._2("A1") should be ("42")
    d(5)._2("A1") should be ("38")
    d(3)._2("A2") should be (":)")
  }

  it should "search for alignments right" in {
    val cont = new Container("ACGT", 'N', Array("A1", "A2", "A3"), 7)

    cont.push("ACGTAGCTACGATGCGACGACGACGAGGATGTTGGTTT", "Seq1")
    cont.push("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "SeqA")
    cont.push("AAAAAAAAAAAAAAAAAAAAAATCTGTCGTGTTGGTTT", "Seq2")

    val set = new mutable.TreeSet[String]()
    cont.alignment("AAAAAAGAAAAAAAATGCCAAAAAAATTGG", -5, Scoring.loadMatrix(path),
      AlgorithmType.GLOBAL, 2).foreach(align => set += align.name)

    set should be (mutable.TreeSet[String]("SeqA", "Seq2"))

    cont.alignment("AAAAAAGAAAAAAAATGCCAAAAAAATTGG", -5, Scoring.loadMatrix(path),
      AlgorithmType.GLOBAL, 0.57).head.name should be ("Seq2")
  }

  it should "search with specials" in {
    val cont = new Container("ACGT", 'N', Array(), 3)

    cont.push("GCTGGT", "Seq1")
    cont.push("AAAAAA", "SeqA")
    cont.push("ACTTGT", "Seq2")

    val set = new mutable.TreeSet[String]()
    cont.find("CTNGT").foreach(tpl => set += tpl._1)

    set should be (mutable.TreeSet[String]("Seq1", "Seq2"))
  }

  it should "annotate new sequences" in {
    val cont = new Container("ACGT", 'N', Array("A"), 3)
    cont.push("GCTGGT", "Seq1")
    cont.push("GCCGGT", "Seq2")
    cont.push("GCTGT", "Seq3")

    Array("Seq1", "Seq2", "Seq3").foreach(seq => {
      val rec  = cont.record(seq)
      rec.setAnnotation(0, "A", "1")
      rec.setAnnotation(1, "A", "1")
      rec.setAnnotation(2, "A", "1")
      rec.setAnnotation(4, "A", "1")
      if (rec.handle != 2) {
        rec.setAnnotation(5, "A", "1")
      }
    })

    cont.record(0).setAnnotation(3, "A", "1")
    cont.record(1).setAnnotation(2, "A", "2")

    val res = cont.annotate("CTGGC", -5, Scoring.loadMatrix(path), AlgorithmType.GLOBAL, 3).annotations
    res.forall(tpl => tpl._2("A") == "1") shouldEqual true
  }

  it should "annotate new sequences (local)" in {
    val cont = new Container("ACGT", 'N', Array("A"), 3)
    cont.push("AAATTT", "1")
    cont.push("GGGCCC", "2")

    Array("1", "2").foreach(seq => {
      val rec = cont.record(seq)
      (0 until 6).foreach(rec.setAnnotation(_, "A", seq))
    })

    val res = cont.annotate("AAACCC", -5, Scoring.loadMatrix(path), AlgorithmType.LOCAL, 10).annotations
    println(res)
    res.map(tpl => tpl._2("A")).mkString should be ("111222")
  }

  it should "take variants" in {
    val cont = new Container("ACGT", 'N', Array("A"), 3)
    cont.push("AAATTT", "1")
    cont.push("GGGCCC", "2")

    val res = cont.variants("AAACCC", -5, Scoring.loadMatrix(path), AlgorithmType.LOCAL, 10)._1
    res.flatMap(vr => vr.variants).mkString should be ("AAACCC")

    val res2 = cont.variants("AAACCC", -5, Scoring.loadMatrix(path), AlgorithmType.GLOBAL, 10)._1
    res2.flatMap(vr => vr.variants).mkString should be ("AGAGAGTCTCTC")
  }
}
