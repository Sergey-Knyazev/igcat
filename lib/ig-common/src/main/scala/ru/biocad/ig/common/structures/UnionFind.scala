package ru.biocad.ig.common.structures

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/** Created by smirnovvs on 25.02.14. */
class UnionFind[T] extends AnyRef with IndexedSeq[T]{
  private class Node(var parent : Int, val data : T)

  private val arr = new ArrayBuffer[Node]

  private def merge(id1: Int, id2: Int) : Unit = {
    def adopt(parentId : Int, childId : Int) = arr(childId).parent = parentId
    if (Random.nextBoolean()) adopt(id1, id2) else adopt(id2, id1)
  }

  def add(elem : T) : Int = {
    arr.append(new Node(arr.length, elem))
    arr.length - 1
  }

  def union(id1 : Int, id2 : Int) : Unit = {
    merge(find(id1), find(id2))
  }

  def find(id : Int) : Int = {
    def is_root(id : Int) = arr(id).parent == id

    val node = arr(id)
    if (!is_root(node.parent)) node.parent = find(node.parent)
    node.parent
  }

  def related(id1 : Int, id2 : Int) : Boolean = find(id1) == find(id2)

  def sets : Iterable[Iterable[Int]] = (0 until arr.length).groupBy(id => find(arr(id).parent)).map(_._2)

  override def apply(id: Int): T = arr(id).data

  override def length: Int = arr.length
}