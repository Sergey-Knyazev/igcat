package ru.biocad.ig.common.structures

import com.typesafe.scalalogging.LazyLogging

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 15.04.14
 * Time: 23:47
 */
object Combination extends LazyLogging {
  def combinationIterator(ll : List[List[_]]) : Iterator[List[_]] = {
    val num =  ll.map(_.size).product
    logger.debug(s"${if (num <= 0) "Infinity" else num} combinations to process")
    Iterator.from(0).takeWhile(n => n < num || num <= 0).map(combination(ll, _))
  }

  private def combination(xx : List[List[_]], i : Int) : List[_] =
    xx match {
      case Nil => Nil
      case x :: xs => x(i % x.length) :: combination(xs, i / x.length)
    }
}
