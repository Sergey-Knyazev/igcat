import org.scalatest.{Matchers, FlatSpec}
import ru.biocad.ig.common.algorithms.clustering.{WPGMA, UPGMA, HierarchicalClustering, NeighborJoining}
import ru.biocad.ig.common.algorithms.tree.TreeUtils

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 9:40
 */
class HierarchicalClusteringTest extends FlatSpec with Matchers {
  "NeighborJoining" should "work" in {
    val items = Array('A', 'B', 'C', 'D')
    val dist = Array(Array(0.0, 7.0, 11.0, 14.0),
                     Array(7.0, 0.0, 6.0, 9.0),
                     Array(11.0, 6.0, 0.0, 7.0),
                     Array(14.0, 9.0, 7.0, 0.0))

    val clustering = new NeighborJoining(HierarchicalClustering.constructDistMap(items, dist))
    val tree = clustering.construct()
    println(TreeUtils.dump(tree))
  }

  "UPGMA" should "work" in {
    val items = Range(1, 6)
    val dist = Array(Array(0.00, 2.06, 4.03, 6.32, 2.08),
                     Array(2.06, 0.00, 3.50, 4.12, 5.43),
                     Array(4.03, 3.50, 0.00, 2.25, 3.65),
                     Array(6.32, 4.12, 2.25, 0.00, 4.81),
                     Array(2.08, 5.43, 3.65, 4.81, 0.00))

    val clustering = new UPGMA(HierarchicalClustering.constructDistMap(items, dist))
    val tree = clustering.construct()
    println(TreeUtils.dump(tree))
  }

  "WPGMA" should "work" in {
    val items = Range(1, 6)
    val dist = Array(Array(0.00, 2.06, 4.03, 6.32, 2.08),
                     Array(2.06, 0.00, 3.50, 4.12, 5.43),
                     Array(4.03, 3.50, 0.00, 2.25, 3.65),
                     Array(6.32, 4.12, 2.25, 0.00, 4.81),
                     Array(2.08, 5.43, 3.65, 4.81, 0.00))

    val clustering = new WPGMA(HierarchicalClustering.constructDistMap(items, dist))
    val tree = clustering.construct()
    println(TreeUtils.dump(tree))
  }
}
