package ru.biocad.ig.common.algorithms.clustering

import ru.biocad.ig.common.structures.tree.BinaryTree

import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 21.04.14
 * Time: 10:51
 */
abstract class HierarchicalClustering[T](dmap : collection.Map[T, collection.Map[T, Double]]) {
  private var _dmap = createDMap(dmap)

  def this() = {
    this(collection.Map.empty[T, collection.Map[T, Double]])
  }

  def init(dmap : collection.Map[T, collection.Map[T, Double]]) : HierarchicalClustering[T] = {
    _dmap = createDMap(dmap)
    this
  }

  def construct() : BinaryTree[T]
  protected def getWeightLeafsToNew(f : BinaryTree[T], g : BinaryTree[T]) : (Double, Double)
  protected def getWeightAllToNew(u : BinaryTree[T]) : collection.Map[BinaryTree[T], Double]
  protected def getPair : (BinaryTree[T], BinaryTree[T])

  protected def weight(f : BinaryTree[T], g : BinaryTree[T]) : Double = _dmap(f)(g)

  protected def merge(f : BinaryTree[T], g : BinaryTree[T]) : BinaryTree[T] =
    merge(f, g, getWeightLeafsToNew(f, g))

  protected def merge(f : BinaryTree[T], g : BinaryTree[T], w : (Double, Double)) : BinaryTree[T] = {
    val (fu, gu) = w
    f.updateWeight(fu)
    g.updateWeight(gu)

    val u = new BinaryTree[T]()
    u.left = f
    u.right = g
    u
  }

  protected def dikSum : Map[BinaryTree[T], Double] = _dmap.toMap.map(kv => kv._1 -> kv._2.values.sum)

  protected def mapSize : Int = _dmap.size

  protected def nodes : Iterable[BinaryTree[T]] = _dmap.keys

  protected def updateMap(u : BinaryTree[T], ud : collection.Map[BinaryTree[T], Double]) : Unit =
    _dmap = _dmap.filter(kv => kv._1 != u.left && kv._1 != u.right).map(kv =>
      kv._1 -> (kv._2.filter(kv2 => kv2._1 != u.left && kv2._1 != u.right) += (u -> ud(kv._1)))) += (u -> mutable.LinkedHashMap(ud.toSeq : _*))

  private def createDMap(dmap : collection.Map[T, collection.Map[T, Double]]) : mutable.LinkedHashMap[BinaryTree[T], mutable.LinkedHashMap[BinaryTree[T], Double]] = {
    val keysMapping = dmap.keys.map(item => item -> new BinaryTree(item)).toMap
    mutable.LinkedHashMap(dmap.toSeq.map(kv => keysMapping(kv._1) -> mutable.LinkedHashMap(kv._2.toSeq.map(kv2 => keysMapping(kv2._1) -> kv2._2) : _*)) : _*)
  }
}

object HierarchicalClustering {
  def constructDistMap[T](items : Iterable[T],
                          distances : Array[Array[Double]]) : collection.Map[T, collection.Map[T, Double]] = {
    mutable.LinkedHashMap(
      (distances zip items).map(tpl => {
        val (d, i) = tpl
        i -> mutable.LinkedHashMap((d zip items).map(tpl => tpl._2 -> tpl._1) : _*)
      }) : _*)
  }
}