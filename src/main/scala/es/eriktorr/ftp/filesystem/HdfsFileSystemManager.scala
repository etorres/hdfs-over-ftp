package es.eriktorr.ftp.filesystem

import java.net.URI

import com.typesafe.scalalogging.LazyLogging
import org.apache.ftpserver.ftplet.{FileSystemFactory, FileSystemView, User}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.CommonConfigurationKeysPublic
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.hadoop.hdfs.protocol.HdfsConstants
import org.apache.hadoop.hdfs.protocol.HdfsConstants.SafeModeAction

trait HdfsFileSystemManager extends FileSystemFactory

object HdfsFileSystemManager extends LazyLogging {
  private class InnerHdfsFileSystemManager(
    distributedFileSystem: DistributedFileSystem,
    hdfsLimits: HdfsLimits
  ) extends HdfsFileSystemManager {
    override def createFileSystemView(user: User): FileSystemView =
      new HdfsFileSystemView(distributedFileSystem, user, hdfsLimits)
  }

  /**
   * A method to create a HDFS filesystem manager.
   *
   * This function uses a few ideas from {{{org.apache.hadoop.hdfs.client.HdfsUtils}}}.
   * An example of use of this class to check the health of a Hadoop cluster is as follows:
   * {{{
   *   HdfsUtils.isHealthy(new URI("hdfs://localhost:9000"))
   * }}}
   *
   * @param hdfsClientConfig The HDFS configuration properties.
   * @return A HDFS filesystem manager.
   */
  def apply(hdfsClientConfig: HdfsClientConfig): HdfsFileSystemManager = {
    configureEnvironment(hdfsClientConfig.superUser)

    val hadoopUri = new URI(hdfsClientConfig.uri)
    require(
      hadoopUri.getScheme == HdfsConstants.HDFS_URI_SCHEME,
      s"The scheme is not ${HdfsConstants.HDFS_URI_SCHEME}, uri=${hadoopUri.toString}"
    )

    val configuration = new Configuration()
    configuration.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, hdfsClientConfig.uri)
    configuration.set(
      "hadoop.job.ugi",
      s"${hdfsClientConfig.superUser},${hdfsClientConfig.superGroup}"
    )

    val dfs = new DistributedFileSystem
    dfs.initialize(hadoopUri, configuration)

    val safeMode = dfs.setSafeMode(SafeModeAction.SAFEMODE_GET)
    require(!safeMode, "The NameNode is in safe mode state")

    new InnerHdfsFileSystemManager(dfs, hdfsClientConfig.hdfsLimits)
  }

  private[this] def configureEnvironment(hadoopUser: String): Unit = {
    val hadoopUserNameKey = "HADOOP_USER_NAME"
    val previousValue = System.setProperty(hadoopUserNameKey, hadoopUser)
    logger.debug(s"Previous $hadoopUserNameKey value $previousValue superseded by $hadoopUser")
  }
}
