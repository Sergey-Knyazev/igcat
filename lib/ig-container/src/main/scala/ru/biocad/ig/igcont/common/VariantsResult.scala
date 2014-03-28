package ru.biocad.ig.igcont.common

import scala.collection.immutable.HashMap

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 25.02.14
 * Time: 10:19
 */
case class VariantsResult(symbol : Char, variants : Iterable[Char],
                          annotations : HashMap[String, String])
