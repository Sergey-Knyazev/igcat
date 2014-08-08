package ru.biocad.ig.common.structures.tree

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
      s"(${tree.children.map(dump(_, converter)).mkString(", ")}):${tree.weight}"
    }
  }
}
