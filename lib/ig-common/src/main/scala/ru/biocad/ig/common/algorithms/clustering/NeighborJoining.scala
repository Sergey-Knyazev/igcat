package ru.biocad.ig.common.algorithms.clustering

import ru.biocad.ig.common.structures.tree.BinaryTree


/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 21.04.14
 * Time: 10:49
 */
class NeighborJoining[T](dmap : collection.Map[T, collection.Map[T, Double]])
  extends HierarchicalClustering[T](dmap) {

  def this() = {
    this(collection.Map.empty[T, collection.Map[T, Double]])
  }

  override def construct() : BinaryTree[T] = {
    while (mapSize != 2) {
      val (f, g) = getPair
      val u = merge(f, g)
      val uw = getWeightAllToNew(u)
      updateMap(u, uw)
    }

    val w = weight(nodes.head, nodes.last)
    merge(nodes.head, nodes.last, (w / 2, w / 2))
  }

  override protected def getWeightLeafsToNew(f : BinaryTree[T], g : BinaryTree[T]) : (Double, Double) = {
    val fu = 0.5 * weight(f, g) + 0.5 / (mapSize - 2) * (dikSum(f) - dikSum(g))
    val gu = weight(f, g) - fu
    (fu, gu)
  }

  override protected def getWeightAllToNew(u : BinaryTree[T]) : collection.Map[BinaryTree[T], Double] =
    nodes.filter(node => node != u.left && node != u.right).map(k =>
      k -> 0.5 * (weight(u.left, k) + weight(u.right, k) - weight(u.left, u.right))).toMap

  override protected def getPair : (BinaryTree[T], BinaryTree[T]) =
    nodes.toSeq.combinations(2).map(arr => {
      val (f, g) = (arr.head, arr.last)
      (f, g) -> ((mapSize - 2) * weight(f, g) - dikSum(f) - dikSum(g))
    }).minBy(_._2)._1
}