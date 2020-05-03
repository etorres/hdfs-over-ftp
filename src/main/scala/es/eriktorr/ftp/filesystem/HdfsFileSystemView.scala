package es.eriktorr.ftp.filesystem

import com.typesafe.scalalogging.LazyLogging
import org.apache.ftpserver.ftplet.{FileSystemView, FtpException, FtpFile, User}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFileSystemView(
  distributedFileSystem: DistributedFileSystem,
  user: User,
  hdfsClientConfig: HdfsClientConfig,
  // Needed to update the user's working directory
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private[this] var workingDirectory: String
) extends FileSystemView
    with ChrootJail
    with LazyLogging {
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

  // Needed to restrict access to home directory
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  override def changeWorkingDirectory(dir: String): Boolean = {
    val candidateWorkingDirectory = concatenateIfRelative(workingDirectory, dir)

    // TODO
    logger.info(s"""
                   |home_dir=${user.getHomeDirectory}\n
                   |new_working_dir=$candidateWorkingDirectory\n 
                   |is_contained=${isAllowed(
                     user.getHomeDirectory,
                     candidateWorkingDirectory
                   ).toString}
                   |""".stripMargin)
    // TODO

    if (hdfsClientConfig.makeHomeRoot && !isAllowed(
        user.getHomeDirectory,
        candidateWorkingDirectory
      )) throw new FtpException("Access is restricted to home directory")
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

  override def getFile(file: String): FtpFile =
    HdfsFtpFile(
      distributedFileSystem,
      concatenate(workingDirectory, file),
      user,
      hdfsClientConfig.hdfsLimits
    )

  override def isRandomAccessible: Boolean = true

  override def dispose(): Unit = {}
}
