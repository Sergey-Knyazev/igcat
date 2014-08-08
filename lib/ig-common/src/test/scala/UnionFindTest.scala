import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.common.structures.UnionFind

/** Created by smirnovvs on 25.02.14. */
class UnionFindTest extends FlatSpec with Matchers {
  "UnionFind" should "return singleton sets before any union operations" in {
    val uf = new UnionFind[String]()
    val idFoo = uf.add("foo")
    uf.sets.map(_.toSet).toSet shouldEqual Set(Set(idFoo))
    val idBar = uf.add("bar")
    uf.sets.map(_.toSet).toSet shouldEqual Set(Set(idFoo), Set(idBar))
  }

  it should "correctly merge sets" in {
    val uf = new UnionFind[String]
    var reprs = ('a' to 'p').map(char => uf.add(char.toString))
    var sets = reprs.map(r => Set(r))

    while (reprs.length > 1) {
      reprs.grouped(2).foreach { case IndexedSeq(id1, id2) => uf.union(id1, id2) }
      sets = sets.grouped(2).map{ case IndexedSeq(set1, set2) => set1 union set2 }.toIndexedSeq
      uf.sets.map(_.toSet).toSet shouldEqual sets.toSet
      reprs = reprs.zipWithIndex.filter{ case (_, index) => index % 2 == 0 }.map(_._1)
    }
  }
}
