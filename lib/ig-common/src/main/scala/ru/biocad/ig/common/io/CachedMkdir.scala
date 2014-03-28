package ru.biocad.ig.common.io

import java.io.File
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 06.02.14
 * Time: 15:53
 */
object CachedMkdir {
  private var cache = Set[String]()

  def mkdirp(path: String) {
    var prepath = ""

    for(dir <- path.split(Pattern.quote(File.separator))) {
      prepath += (dir + File.separator)
      val f = new java.io.File(prepath)
      if(!cache.contains(prepath) && !f.isDirectory) {
        f.mkdir()
        cache += prepath
      }
    }
  }

}