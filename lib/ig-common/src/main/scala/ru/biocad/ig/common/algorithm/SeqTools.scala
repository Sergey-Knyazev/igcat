package ru.biocad.ig.common.algorithm

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 06.02.14
 * Time: 15:27
 */
object SeqTools {
  private val NUCLEO = "ACGT"

  private val tm = translationMap

  def translateString(dna : String, stop_on_stop : Boolean = false) : String = {
    val result = (dna zip (0 until dna.size / 3 * 3)).map(_._1).sliding(3, 3).map(arr => tm(arr.mkString)).mkString
    if (stop_on_stop) result.split('*').head else result
  }

  def complement(dna : String) : String = dna.map(rc)

  def reverseComp(dna : String) : String = dna.reverseMap(rc)

  def stripEndings(s : String, chars : String) : String = {
    s.dropWhile(c => chars.contains(c)).reverse.dropWhile(c => chars.contains(c)).reverse
  }

  private def rc(ch : Char) : Char =
    if (!NUCLEO.contains(ch)) ch else NUCLEO(NUCLEO.size - NUCLEO.indexOf(ch) - 1)

  def translationMap : Map[String, Char] =
    codonList.toMap

  def reverseTranslationMap : Map[Char, Iterable[String]] =
    codonList.groupBy(_._2).map(tpl => (tpl._1, tpl._2.map(_._1)))

  private def codonList : Iterable[(String, Char)] = {
    val tcag = "TCAG"
    val codon_table = "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG"
    val nucleo = tcag.flatMap(n1 => tcag.flatMap(n2 => tcag.map(n3 => Array(n1, n2, n3).mkString)))
    nucleo zip codon_table
  }
}
