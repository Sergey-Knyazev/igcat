package ru.biocad.ig.common.algorithms

import scala.collection.GenIterable

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 12.03.14
 * Time: 13:19
 */
object SmallTools {
  def truncateSuffix(text : String, suffix : String) : String = {
    val pattern = s"(.*?)($suffix)?$$".r
    val pattern(name, _) = text
    name
  }

  def truncateSuffixes(text : String, suffixes : Iterable[String]) : String =
    suffixes.foldRight(text)((suff, t) => truncateSuffix(t, suff))

  def stripEndings(s : String, chars : String) : String =
    s.dropWhile(c => chars.contains(c)).reverse.dropWhile(c => chars.contains(c)).reverse

  def makeMaybe[T](cond : T => Boolean, to_check : T) : Option[T] =
    if (cond(to_check)) Some(to_check) else None

  def fix[A, B](f : (A => B) => A => B) : (A => B) =
    f(fix(f))(_)

  def avg[T](c : GenIterable[T])(implicit num : Numeric[T]) : Double =
    c.map(num.toDouble).sum / c.size

  def oneOf[T](a : => T, b : => T) : T = {
    try {
      a
    }
    catch {
      case _ : Exception => b
    }
  }

  implicit class Conditional[T](x : T) {
    def otherIfNot(pred : T => Boolean, other : T) =
      if (pred(x)) x else other
  }
}