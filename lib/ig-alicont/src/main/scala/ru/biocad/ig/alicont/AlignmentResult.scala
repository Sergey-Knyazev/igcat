package ru.biocad.ig.alicont

import scala.collection.immutable.HashMap

import ru.biocad.ig.alicont.common.Record

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 28.10.13
 * Time: 17:48
 */
class AlignmentResult(s : Double, q : String, t : String, source_query : String,
                      source_target : String, record_target : Record) {
  private val _sourceq = source_query
  private val _sourcet = source_target
  private val _recordt = record_target
  private val _query = q
  private val _target = t
  private val _result = Array[(Char, HashMap[String, String])](source_query.map((_, null)):_*)
  private val _score = s
  private val _similarity = 1.0 * q.zip(t).foldRight(0)((c, acc) => acc + (if (c._1 == c._2) 1 else 0)) / q.size
  private val _tname : String = record_target.name

  build()

  def get : Iterable[(Char, HashMap[String, String])] = _result

  def score : Double = _score

  def source : String = _sourceq

  def query : String = _query

  def target : String = _target

  def name : String = _tname

  def similarity : Double = _similarity

  private def build() : Unit = {
    val q_start = _sourceq.indexOf(_query.replaceAll("-", ""))
    val t_start = _sourcet.indexOf(_target.replaceAll("-", ""))

    var i = 0
    var j = 0
    (_query zip _target).takeWhile(_ => i < _result.size).foreach(tpl => {
      val (c, d) = tpl
      if (d == '-') {
        _result(i + q_start) = (c, null)
        i += 1
      }
      else {
        if (c != '-') {
          _result(i + q_start) = (c, _recordt.annotationOf(j + t_start))
          i += 1
        }
        j += 1
      }
    })
  }
}
