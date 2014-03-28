package ru.biocad.ig.igcont.common

import scala.collection.immutable.HashMap

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 18.02.14
 * Time: 10:41
 */
case class AnnotationResult(annotations : Array[(Char, HashMap[String, String])],
                            references : Array[(String, Double)]) {
  def apply(i : Int) : (Char, HashMap[String, String]) = annotations(i)
}
