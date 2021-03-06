package ru.biocad.ig.regions.common

import ru.biocad.ig.alicont.common.Scoring
import ru.biocad.ig.common.resources.{MatrixTypes, MatrixLoader}
import ru.biocad.ig.common.io.common.SequenceType

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 31.10.13
 * Time: 12:11
 */
class SequenceTrait(t : SequenceType.SequenceType) {
  private val _type = t
  private val _matrix = t match {
    case SequenceType.NUCLEO => Scoring.loadMatrix(MatrixLoader.byName(MatrixTypes.NUC44))
    case SequenceType.AMINO => Scoring.loadMatrix(MatrixLoader.byName(MatrixTypes.BLOSUM62))
  }

  def alphabet : String = {
    _type match {
      case SequenceType.NUCLEO => "ACGTN"
      case SequenceType.AMINO  => "ARNDCEQGHILKMFPSTWYVX*"
    }
  }

  def special : Char = {
    _type match {
      case SequenceType.NUCLEO => 'N'
      case SequenceType.AMINO  => 'X'
    }
  }

  def k : Int = {
    _type match {
      case SequenceType.NUCLEO => 7
      case SequenceType.AMINO  => 3
    }
  }

  def score : Array[Array[Double]] = _matrix
}
