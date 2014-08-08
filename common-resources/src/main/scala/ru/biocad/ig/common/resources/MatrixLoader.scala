package ru.biocad.ig.common.resources

import MatrixTypes._
import scala.io.Source

/** Created by smirnovvs on 23.05.2014. */
object MatrixLoader {
  def byName(matrixType : MatrixTypes) : Source = byName(
    matrixType match {
      case NUC11 => "/matrix/NUC1.1.txt"
      case NUC44 => "/matrix/NUC4.4.txt"
      case BLOSUM62 => "/matrix/BLOSUM62.txt"
    }
  )
  
  def byName(resourceName : String) : Source = Source.fromURL(getClass.getResource(resourceName)) 
}
