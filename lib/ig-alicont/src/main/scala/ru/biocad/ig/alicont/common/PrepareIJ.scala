package ru.biocad.ig.alicont.common

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 23.04.14
 * Time: 0:10
 */
object PrepareIJ {
  def local(s : Int, q : Int, matrix : Matrix) : (Double, (Int, Int)) = {
    var (i, j) = (s, q)

    var score = Double.MinValue
    for (it <- 0 to s; jt <- 0 to q) {
      if (score < matrix(it)(jt)) {
        score = matrix(it)(jt)
        i = it
        j = jt
      }
    }

    (score, (i, j))
  }

  def semiglobal(s : Int, q : Int, matrix : Matrix) : (Double, (Int, Int)) = {
    var (maxlastrow, maxlastcol) = (Double.MinValue, Double.MinValue)
    var (maxi, maxj) = (0, 0)
    for (jt <- 0 to q) {
      if (maxlastrow < matrix.last(jt)) {
        maxlastrow = matrix.last(jt)
        maxj = jt
      }
    }
    for (it <- 0 to s) {
      if (maxlastcol < matrix(it).last) {
        maxlastcol = matrix(it).last
        maxi = it
      }
    }

    if (maxlastrow >= maxlastcol) (maxlastrow, (s, maxj)) else (maxlastcol, (maxi, q))
  }
}
