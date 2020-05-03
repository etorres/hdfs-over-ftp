package es.eriktorr.ftp.filesystem

import org.apache.commons.io.FilenameUtils.{
  concat,
  getFullPathNoEndSeparator,
  getName,
  getPrefix,
  normalizeNoEndSeparator,
  separatorsToUnix,
  directoryContains => directoryContainsJava
}

trait FileNameProcessing {
  def normalized(fileName: String): String =
    normalizeNoEndSeparator(separatorsToUnix(fileName)).replaceFirst("//", "/")
  def simpleNameFrom(fileName: String): String = getName(fileName)
  def concatenate(basePath: String, fileNameToAdd: String): String =
    normalized(concat(basePath, separatorsToUnix(fileNameToAdd)))
  def pathToParent(fileName: String): String = getFullPathNoEndSeparator(fileName)
  def concatenateIfRelative(basePath: String, fileNameToAdd: String) = {
    val unixDir = separatorsToUnix(fileNameToAdd)
    getPrefix(unixDir) match {
      case "/" => normalized(fileNameToAdd)
      case _ => concatenate(basePath, fileNameToAdd)
    }
  }
  def directoryContains(parent: String, child: String): Boolean =
    directoryContainsJava(parent, child)
}
