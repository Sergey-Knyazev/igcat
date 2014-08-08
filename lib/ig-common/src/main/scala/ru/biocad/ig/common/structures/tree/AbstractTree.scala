package ru.biocad.ig.common.structures.tree

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 03.06.14
 * Time: 9:59
 */
abstract class AbstractTree[T](v : Option[T], w : Double) {
  private val _value  = v
  private var _weight = w

  def this(v : T, w : Double = 0.0) =
    this(Some(v), w)

  def this() =
    this(None, 0.0)

  def isLeaf: Boolean =
    children.isEmpty

  def children : Iterable[AbstractTree[T]]

  def value : Option[T] = _value

  def weight : Double = _weight

  def updateWeight(w : Double) : Unit = _weight = w

  def leafCount: Int =
    if (isLeaf) 1 else children.flatMap(c => if (c == null) None else Some(c.leafCount)).sum
}
