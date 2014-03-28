package ru.biocad.ig.regions.annotators

import ru.biocad.ig.igcont.{ContainerUtils, Container}
import ru.biocad.ig.common.sequence.SequenceType.SequenceType
import ru.biocad.ig.alicont.AlignmentResult
import ru.biocad.ig.alicont.algorithms.AlgorithmType.AlgorithmType
import ru.biocad.ig.alicont.algorithms.AlgorithmType
import ru.biocad.ig.regions.common.SequenceTrait
import java.io.File
import ru.biocad.ig.igcont.common.AnnotationResult

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 31.10.13
 * Time: 12:02
 */
class RegionAnnotator(n : String, t : SequenceType, fasta : File, kabat : File,
                      gap : (Double, Double, Double) = (-10, -1, -5),
                      matrix : scala.Array[scala.Array[scala.Double]] = null,
                      algo : AlgorithmType = AlgorithmType.SEMIGLOBAL) {
  private val _name     = n
  private val _type     = new SequenceTrait(t)
  private val _cont     = new Container(_type.alphabet, _type.special, Array("Region"), _type.k)
  private val _algo     = algo
  private val _matrix   = matrix
  private val _gap_open = gap._1
  private val _gap_ext  = gap._2
  private val _gap_smpl = gap._3

  RegionAnnotatorUtils.createRegionsContainer(_cont, fasta, kabat)

  def find(pattern : String) : Iterable[(String, Int)] = _cont.find(pattern)

  def alignment(query : String, n : Int = 10) : Iterable[AlignmentResult] =
    if (!AlgorithmType.affine.contains(_algo))
      _cont.alignment(query, _gap_smpl, if (_matrix == null) _type.score else matrix, _algo, n)
    else
      _cont.affine_alignment(query, _gap_open, _gap_ext, if (_matrix == null) _type.score else matrix, _algo, n)

  def annotate(query : String, n : Int = 3) : AnnotationResult =
    if (!AlgorithmType.affine.contains(_algo))
      _cont.annotate(query, _gap_smpl, if (_matrix == null) _type.score else matrix, _algo, n)
    else
      _cont.affine_annotate(query, _gap_open, _gap_ext, if (_matrix == null) _type.score else matrix, _algo, n)

  def regions(query : String, n : Int = 3) : Iterable[Int] =
    RegionAnnotatorUtils.annotations2regions(annotate(query, n).annotations)

  def name : String = _name

  def stats() : Unit = ContainerUtils.print_stats(_cont)
}