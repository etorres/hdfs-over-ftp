package es.eriktorr.ftp.filesystem

import org.apache.commons.io.FilenameUtils.{directoryContains, equals => pathEquals}
import org.apache.commons.io.IOCase

trait ChrootJail extends FileNameProcessing {
  def isAllowed(rootDir: String, path: String): Boolean = {
    val canonicalPath = normalized(path)
    pathEquals(normalized(rootDir), canonicalPath, false, IOCase.SENSITIVE) || directoryContains(
      normalizedWithEndSeparator(rootDir),
      canonicalPath
    )
  }
}
