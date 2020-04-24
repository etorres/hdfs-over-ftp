package es.eriktorr.ftp.filesystem

import org.apache.ftpserver.ftplet.{FileSystemView, FtpFile, User}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFileSystemView(distributedFileSystem: DistributedFileSystem, user: User)
    extends FileSystemView
    with FileNameProcessing {
  // Needed to update the working directory of the user
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private[this] var workingDirectory = "/"

  override def getHomeDirectory: FtpFile = HdfsFtpFile(distributedFileSystem, "/", user)

  override def getWorkingDirectory: FtpFile =
    HdfsFtpFile(distributedFileSystem, workingDirectory, user)

  override def changeWorkingDirectory(dir: String): Boolean = {
    val candidateWorkingDirectory = concatenate("/", dir)
    val file = HdfsFtpFile(distributedFileSystem, candidateWorkingDirectory, user)
    if (file.isDirectory && file.isReadable) {
      workingDirectory = candidateWorkingDirectory
      true
    } else false
  }

  override def getFile(file: String): FtpFile =
    HdfsFtpFile(
      distributedFileSystem,
      concatenate(workingDirectory, file),
      user
    )

  override def isRandomAccessible: Boolean = true

  override def dispose(): Unit = {}
}
