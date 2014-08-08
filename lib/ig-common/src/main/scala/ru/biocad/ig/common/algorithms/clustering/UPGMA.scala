package ru.biocad.ig.common.algorithms.clustering

import ru.biocad.ig.common.structures.tree.BinaryTree

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 11:55
 */
class UPGMA[T](dmap : collection.Map[T, collection.Map[T, Double]])
  extends HierarchicalClustering[T](dmap) {

  def this() = {
    this(collection.Map.empty[T, collection.Map[T, Double]])
  }

  override def construct(): BinaryTree[T] = {
    while (mapSize != 1) {
      val (f, g) = getPair
      val u = merge(f, g)
      val uw = getWeightAllToNew(u)
      updateMap(u, uw)
    }
    nodes.head
  }

  override protected def getWeightAllToNew(u: BinaryTree[T]): collection.Map[BinaryTree[T], Double] = {
    val leftleafs = u.left.leafCount
    val rightleafs = u.right.leafCount
    nodes.filter(node => node != u.left && node != u.right).map(k => {
      k -> (leftleafs * weight(u.left, k) + rightleafs * weight(u.right, k)) / (leftleafs + rightleafs)
    }).toMap
  }

  override protected def getWeightLeafsToNew(f: BinaryTree[T], g: BinaryTree[T]): (Double, Double) = {
    val w = weight(f, g)
    (w / 2, w / 2)
  }

  override protected def getPair : (BinaryTree[T], BinaryTree[T]) =
    nodes.toSeq.combinations(2).map(arr => {
      val (f, g) = (arr.head, arr.last)
      (f, g) -> weight(f, g)
    }).minBy(_._2)._1
}