package es.eriktorr.katas.filesystem

import org.apache.commons.io.FilenameUtils.{concat, normalizeNoEndSeparator, separatorsToUnix}
import org.apache.ftpserver.ftplet.{FileSystemView, FtpFile, User}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFileSystemView(distributedFileSystem: DistributedFileSystem, user: User)
    extends FileSystemView {
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private[this] var workingDirectory = "/"

  override def getHomeDirectory: FtpFile = HdfsFtpFile(distributedFileSystem, "/", user)

  override def getWorkingDirectory: FtpFile = ???

  override def changeWorkingDirectory(dir: String): Boolean = ???

  override def getFile(file: String): FtpFile =
    HdfsFtpFile(
      distributedFileSystem,
      normalizeNoEndSeparator(concat(workingDirectory, separatorsToUnix(file))),
      user
    )

  override def isRandomAccessible: Boolean = true

  override def dispose(): Unit = {}
}
