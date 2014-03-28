package ru.biocad.ig.common.sequence

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 31.10.13
 * Time: 12:06
 */
object SequenceType extends Enumeration {
  type SequenceType = Value
  val NUCLEO, AMINO = Value

  class SequenceTypeInner(seqType : SequenceType) {
    def wildcard : Char = seqType match {
      case SequenceType.NUCLEO => 'N'
      case SequenceType.AMINO  => 'X'
    }
  }

  implicit def seqTypeToWildcard(seqType : SequenceType) : SequenceTypeInner = new SequenceTypeInner(seqType)
}
