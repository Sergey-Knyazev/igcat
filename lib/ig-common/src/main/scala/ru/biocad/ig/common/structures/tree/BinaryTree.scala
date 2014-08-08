package ru.biocad.ig.common.structures.tree

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 03.06.14
 * Time: 10:02
 */
class BinaryTree[T](v : Option[T], w : Double) extends AbstractTree[T](v, w) {
  var left : BinaryTree[T] = null
  var right : BinaryTree[T] = null

  def this(v : T, w : Double = 0.0) =
    this(Some(v), w)

  def this() =
    this(None, 0.0)

  override def children: Iterable[AbstractTree[T]] =
    List(left, right).filter(_ != null)
}
