package es.eriktorr.katas.filesystem

import java.net.URI

import org.apache.ftpserver.ftplet.{FileSystemFactory, FileSystemView, User}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.DistributedFileSystem

trait HdfsFileSystemManager extends FileSystemFactory

object HdfsFileSystemManager {
  private class InnerHdfsFileSystemManager(distributedFileSystem: DistributedFileSystem)
      extends HdfsFileSystemManager {
    override def createFileSystemView(user: User): FileSystemView =
      new HdfsFileSystemView(distributedFileSystem, user)
  }

  def apply(hdfsClientConfig: HdfsClientConfig): HdfsFileSystemManager = {
    val configuration = new Configuration()
    configuration.set("fs.defaultFS", hdfsClientConfig.uri)
    configuration.set(
      "hadoop.job.ugi",
      s"${hdfsClientConfig.superUser},${hdfsClientConfig.superGroup}"
    )

    val dfs = new DistributedFileSystem
    dfs.initialize(new URI(hdfsClientConfig.uri), configuration)

    new InnerHdfsFileSystemManager(dfs)
  }
}
