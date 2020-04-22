package es.eriktorr.katas.filesystem

import org.apache.commons.io.FilenameUtils.{
  concat,
  getName,
  normalizeNoEndSeparator,
  separatorsToUnix
}

trait FileNameProcessing {
  def normalized(fileName: String): String =
    normalizeNoEndSeparator(separatorsToUnix(fileName)).replaceFirst("//", "/")
  def simpleNameFrom(fileName: String): String = getName(fileName)
  def concatenate(basePath: String, fileNameToAdd: String): String =
    normalized(concat(basePath, separatorsToUnix(fileNameToAdd)))
}
