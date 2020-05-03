package es.eriktorr.ftp.filesystem

import org.apache.commons.io.FilenameUtils.{directoryContains, equals => pathEquals}
import org.apache.commons.io.IOCase

trait ChrootJail extends FileNameProcessing {
  def isAllowed(rootDir: String, path: String): Boolean = {
    val canonicalRootDir = normalized(rootDir)
    val canonicalPath = normalized(path)
    pathEquals(canonicalRootDir, canonicalPath, false, IOCase.SENSITIVE) || directoryContains(
      canonicalRootDir,
      canonicalPath
    )
  }
}
