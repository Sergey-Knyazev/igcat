package ru.biocad.ig.common.algorithm

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

  def makeMaybe[T](cond : T => Boolean, to_check : T) : Option[T] =
    if (cond(to_check)) Some(to_check) else None

  def fix[A, B](f : (A => B) => A => B) : (A => B) =
    f(fix(f))(_)
}
