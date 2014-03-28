package ru.biocad.ig.common.sequence


/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 13.03.14
 * Time: 13:16
 */
object VHHChecker {
  def isVHH(s : String, confidence : Int = 0) : Option[Boolean] = {
    val posVH = Array("W",               // 36
                      "FHILYV",          // 37
                      "R",               // 38
                      "Q",               // 39
                      "A",               // 40
                      "PST",             // 41
                      "G",               // 42
                      "K",               // 43
                      "EGADQRSL",        // 44
                      "RLCILPQV",        // 45
                      "EV",              // 46
                      "WLFAGIMRSVY",     // 47
                      "V",               // 48
                      "SAG")             // 49

    val posVHH = Array("",               // 36
                       "",               // 37
                       "",               // 38
                       "HPR",            // 39
                       "FGLPTV",         // 40
                       "AL",             // 41
                       "E",              // 42
                       "DENQRTV",        // 43
                       "",               // 44 ??
                       "",               // 45 ??
                       "DKQ",            // 46
                       "",               // 47 ??
                       "IL",             // 48
                       "ITV")            // 49
    val pattern = (posVH zip posVHH).map(s => "[%s%s]".format(s._1, s._2)).mkString.r

    pattern.findFirstMatchIn(s) match {
      case Some(fr2match) =>
        val fr2 = s.substring(fr2match.start, fr2match.end)
        if (fr2.zipWithIndex.count(tpl => posVHH(tpl._2).contains(tpl._1)) >= confidence) Some(true) else Some(false)
      case _ =>
        None
    }
  }
}
