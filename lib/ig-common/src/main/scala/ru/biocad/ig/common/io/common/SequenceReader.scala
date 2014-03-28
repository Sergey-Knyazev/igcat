package ru.biocad.ig.common.io.common

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 08.02.14
 * Time: 23:47
 */
trait SequenceReader extends Iterator[Sequence]{
  def hasNext : Boolean
  def next() : Sequence
  def close() : Unit
}
