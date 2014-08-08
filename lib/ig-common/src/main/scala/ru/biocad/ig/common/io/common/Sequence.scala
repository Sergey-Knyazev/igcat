package ru.biocad.ig.common.io.common

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 08.02.14
 * Time: 23:56
 */
case class Sequence(name : String = "", sequence : String,
                    quality : Array[Byte] = Array.empty[Byte],
                    raw_data : Array[Int] = Array.empty[Int],
                    description : String = "") {
  def unary_+() : String = sequence
  def unary_-() : String = reverse_complement

  def size : Int = sequence.size

  def qual : Array[Byte] =
    if (sequence.size == quality.size) quality else Array.ofDim[Byte](sequence.size)

  def complement : String =
    sequence.map(Sequence.rc)

  def reverse_complement : String =
   sequence.reverseMap(Sequence.rc)

  def translated : String =
    (sequence zip (0 until sequence.size / 3 * 3)).map(_._1).sliding(3, 3).map(arr => Sequence.tm(arr.mkString)).mkString

  def translatedWithStop : String =
    (sequence zip (0 until sequence.size / 3 * 3)).map(_._1).sliding(3, 3).
      map(arr => Sequence.tm(arr.mkString)).mkString.split('*').head

  def isVHH(confidence : Int = 0) : Option[Boolean] = {
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

    pattern.findFirstMatchIn(sequence) match {
      case Some(fr2match) =>
        val fr2 = sequence.substring(fr2match.start, fr2match.end)
        if (fr2.zipWithIndex.count(tpl => posVHH(tpl._2).contains(tpl._1)) >= confidence) Some(true) else Some(false)
      case _ =>
        None
    }
  }
}

object Sequence {
  private val tm = translationMap
  private val rtm = reverseTranslationMap

  def translationMap : Map[String, Char] =
    codonList.toMap

  def reverseTranslationMap : Map[Char, Iterable[String]] =
    codonList.groupBy(_._2).map(tpl => (tpl._1, tpl._2.map(_._1)))

  def reverseTranslationMapSingleton : Map[Char, Iterable[String]] = rtm

  private def codonList : Iterable[(String, Char)] = {
    val tcag = "TCAG"
    val codon_table = "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
    val nucleo = tcag.flatMap(n1 => tcag.flatMap(n2 => tcag.map(n3 => Array(n1, n2, n3).mkString)))
    nucleo zip codon_table
  }

  private def rc(ch : Char) : Char = {
    val rcs = "HVWRMACNGTKYSBD"
    if (rcs.contains(ch)) rcs(rcs.size - rcs.indexOf(ch) - 1) else ch
  }
}