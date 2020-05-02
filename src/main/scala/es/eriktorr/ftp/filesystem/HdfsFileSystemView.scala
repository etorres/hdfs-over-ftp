package es.eriktorr.ftp.filesystem

import org.apache.ftpserver.ftplet.{FileSystemView, FtpFile, User}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFileSystemView(
  distributedFileSystem: DistributedFileSystem,
  user: User,
  hdfsLimits: HdfsLimits,
  // Needed to update the user's working directory
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private[this] var workingDirectory: String
) extends FileSystemView
    with FileNameProcessing {
  def this(distributedFileSystem: DistributedFileSystem, user: User, hdfsLimits: HdfsLimits) = {
    this(distributedFileSystem, user, hdfsLimits, user.getHomeDirectory)
  }

  override def getHomeDirectory: FtpFile =
    HdfsFtpFile(distributedFileSystem, user.getHomeDirectory, user, hdfsLimits)

  override def getWorkingDirectory: FtpFile =
    HdfsFtpFile(distributedFileSystem, workingDirectory, user, hdfsLimits)

  override def changeWorkingDirectory(dir: String): Boolean = {
    val candidateWorkingDirectory = concatenateIfRelative(workingDirectory, dir)
    val file = HdfsFtpFile(distributedFileSystem, candidateWorkingDirectory, user, hdfsLimits)
    if (file.isDirectory && file.isReadable) {
      workingDirectory = candidateWorkingDirectory
      true
    } else false
  }

  override def getFile(file: String): FtpFile =
    HdfsFtpFile(
      distributedFileSystem,
      concatenate(workingDirectory, file),
      user,
      hdfsLimits
    )

  override def isRandomAccessible: Boolean = true

  override def dispose(): Unit = {}
}
