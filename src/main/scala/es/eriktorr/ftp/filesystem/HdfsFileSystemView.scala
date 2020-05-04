package es.eriktorr.ftp.filesystem

import org.apache.ftpserver.ftplet.{FileSystemView, FtpFile, User}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFileSystemView(
  distributedFileSystem: DistributedFileSystem,
  user: User,
  hdfsClientConfig: HdfsClientConfig,
  // Needed to update the user's working directory
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private[this] var workingDirectory: String
) extends FileSystemView
    with ChrootJail {
  def this(
    distributedFileSystem: DistributedFileSystem,
    user: User,
    hdfsClientConfig: HdfsClientConfig
  ) = {
    this(distributedFileSystem, user, hdfsClientConfig, user.getHomeDirectory)
  }

  override def getHomeDirectory: FtpFile =
    HdfsFtpFile(distributedFileSystem, user.getHomeDirectory, user, hdfsClientConfig.hdfsLimits)

  override def getWorkingDirectory: FtpFile =
    HdfsFtpFile(distributedFileSystem, workingDirectory, user, hdfsClientConfig.hdfsLimits)

  override def changeWorkingDirectory(dir: String): Boolean = {
    val candidateWorkingDirectory = concatenateIfRelative(workingDirectory, dir)
    requireChrootJailAccess(user.getHomeDirectory, candidateWorkingDirectory)
    val file = HdfsFtpFile(
      distributedFileSystem,
      candidateWorkingDirectory,
      user,
      hdfsClientConfig.hdfsLimits
    )
    if (file.isDirectory && file.isReadable) {
      workingDirectory = candidateWorkingDirectory
      true
    } else false
  }

  override def getFile(file: String): FtpFile = {
    val pathToFile = concatenate(workingDirectory, file)
    requireChrootJailAccess(user.getHomeDirectory, pathToFile)
    HdfsFtpFile(
      distributedFileSystem,
      pathToFile,
      user,
      hdfsClientConfig.hdfsLimits
    )
  }

  override def isRandomAccessible: Boolean = true

  override def dispose(): Unit = {}

  private[this] def requireChrootJailAccess(rootDir: String, path: String): Unit =
    require(
      !hdfsClientConfig.enableChrootJail || isAllowed(rootDir, path),
      "Access is restricted to home directory"
    )
}
