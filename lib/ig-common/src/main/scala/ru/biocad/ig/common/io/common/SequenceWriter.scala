package ru.biocad.ig.common.io.common

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 08.02.14
 * Time: 23:47
 */
trait SequenceWriter {
  def writeSequence(rec : Sequence) : Unit
  def writeSequences(recs : Iterable[Sequence]) : Unit
  def close() : Unit
}
