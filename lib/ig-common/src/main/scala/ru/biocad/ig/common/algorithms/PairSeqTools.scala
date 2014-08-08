package ru.biocad.ig.common.algorithms

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 21.11.13
 * Time: 17:28
 */
object PairSeqTools {
  def longestCommonSubstring(s : String, t : String) : (Int, Int, Int) = {
    if (s.isEmpty || t.isEmpty) {
      return (0, 0, 0)
    }

    val m = s.length
    val n = t.length
    var cost = 0
    var maxLen = 0
    var posi = 0
    var posj = 0
    var p = Array.fill[Int](n)(0)
    var d = Array.fill[Int](n)(0)

    for (i <- 0 until m) {
      for (j <- 0 until n) {
        // calculate cost/score
        if (s.charAt(i) != t.charAt(j)) {
          cost = 0
        } else {
          if ((i == 0) || (j == 0)) {
            cost = 1
          } else {
            cost = p(j - 1) + 1
          }
        }
        d(j) = cost

        if (cost > maxLen) {
          posi = i
          posj = j
          maxLen = cost
        }
      } // for {}

      val swap = p
      p = d
      d = swap
    }

    (posi - maxLen + 1, posj - maxLen + 1, maxLen)
  }

  def longestCommonPrefix(s : String, t : String) : String =
    (s zip t).takeWhile(tpl => tpl._1 == tpl._2).map(_._1).mkString
}
