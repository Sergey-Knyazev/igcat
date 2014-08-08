package ru.biocad.ig.common.structures.tree

import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 03.06.14
 * Time: 10:08
 */
class MultiTree[T](v : Option[T], w : Double) extends AbstractTree[T](v, w) {
  private val _children = mutable.HashSet.empty[MultiTree[T]]

  def this(v : T, w : Double = 0.0) =
    this(Some(v), w)

  def this() =
    this(None, 0.0)

  override def children: Iterable[AbstractTree[T]] =
    _children
}
