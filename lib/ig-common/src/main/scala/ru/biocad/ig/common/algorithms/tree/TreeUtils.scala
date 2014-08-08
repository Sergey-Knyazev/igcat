package ru.biocad.ig.common.algorithms.tree

import ru.biocad.ig.common.structures.tree.AbstractTree

import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 18.06.14
 * Time: 11:42
 */
object TreeUtils {
  def dump[T](tree : AbstractTree[T], converter : T => String = (e : T) => e.toString) : String = {
    if (tree.isLeaf) {
      s"${tree.value.fold("")(converter)}:${tree.weight}"
    }
    else {
      s"(${tree.children.flatMap(c => if (c == null) None else Some(dump(c))).mkString(", ")}):${tree.weight}"
    }
  }

  def dfs[T](tree : AbstractTree[T], pre : AbstractTree[T] => Unit, post : AbstractTree[T] => Unit) : Unit = {
    pre(tree)
    tree.children.foreach(dfs(_, pre, post))
    post(tree)
  }

  def distanceHeights[T](tree : AbstractTree[T]) : Map[AbstractTree[T], Double] = {
    val h = mutable.HashMap.empty[AbstractTree[T], Double]
    dfs[T](tree, _ => (), node => h(node) = node.children.map(c => h(c) + c.weight).fold(0.0)(math.max))
    h.toMap
  }

  def heights[T](tree : AbstractTree[T]) : Map[AbstractTree[T], Int] = {
    val h = mutable.HashMap.empty[AbstractTree[T], Int]
    var depth = 0
    dfs[T](tree, node => {h(node) = depth; depth += 1}, _ => depth -= 1)
    h.toMap
  }

  def leafs[T](tree : AbstractTree[T]) : Iterable[T] = {
    val leafs = mutable.ArrayBuffer.empty[T]
    dfs[T](tree, _ => (), node => if (node.isLeaf) node.value.foreach(leafs += _))
    leafs
  }
}
