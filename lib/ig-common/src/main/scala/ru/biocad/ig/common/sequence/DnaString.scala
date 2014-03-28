package ru.biocad.ig.common.sequence

import ru.biocad.ig.common.algorithm.SeqTools

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 06.02.14
 * Time: 15:51
 */
class DnaString(seq: String) {
  def unary_+() = seq
  def unary_-() = SeqTools.reverseComp(seq)
  def length = seq.length
}