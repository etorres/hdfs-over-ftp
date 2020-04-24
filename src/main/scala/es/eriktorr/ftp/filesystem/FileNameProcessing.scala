package es.eriktorr.ftp.filesystem

import org.apache.commons.io.FilenameUtils.{
  concat,
  getFullPathNoEndSeparator,
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
  def pathToParent(fileName: String): String = getFullPathNoEndSeparator(fileName)
}
