package ru.biocad.ig.common.io.common

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 08.02.14
 * Time: 23:56
 */
case class Sequence(name : String, sequence : String, quality : Array[Byte] = Array.empty[Byte]) {
  def qual : Array[Byte] =
    if (sequence.size == quality.size) quality else Array.ofDim[Byte](sequence.size)
}