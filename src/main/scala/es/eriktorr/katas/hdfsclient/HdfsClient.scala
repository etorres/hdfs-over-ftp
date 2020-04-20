package es.eriktorr.katas.hdfsclient

import java.net.URI

import es.eriktorr.katas.ApplicationContext
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hdfs.DistributedFileSystem

trait HdfsClient {
  def isDirectory(path: String): Boolean
}

object HdfsClient {
  private class InnerHdfsClient(dfs: DistributedFileSystem) extends HdfsClient {
    def isDirectory(path: String): Boolean = {
      val fileStatus = dfs.getFileStatus(new Path(path))
      fileStatus.isDirectory
    }
  }

  def apply(context: ApplicationContext): HdfsClient = {
    val hdfsClientConfig = context.hdfsClientConfig

    val configuration = new Configuration()
    configuration.set("fs.defaultFS", hdfsClientConfig.uri)
    configuration.set(
      "hadoop.job.ugi",
      s"${hdfsClientConfig.superUser},${hdfsClientConfig.superGroup}"
    )

    val dfs = new DistributedFileSystem
    dfs.initialize(new URI(hdfsClientConfig.uri), configuration)

    new InnerHdfsClient(dfs)
  }
}
