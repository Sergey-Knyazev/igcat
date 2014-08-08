package ru.biocad.ig.common.algorithms.clustering

import ru.biocad.ig.common.structures.tree.BinaryTree

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 22.04.14
 * Time: 12:25
 */
class WPGMA[T](dmap : collection.Map[T, collection.Map[T, Double]])
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

  override protected def getWeightAllToNew(u: BinaryTree[T]): collection.Map[BinaryTree[T], Double] =
    nodes.filter(node => node != u.left && node != u.right).map(k => {
      k -> (weight(u.left, k) + weight(u.right, k)) / 2
    }).toMap


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