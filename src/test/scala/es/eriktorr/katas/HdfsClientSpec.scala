package es.eriktorr.katas

import java.net.URI

import es.eriktorr.katas.unitspec.UnitSpec
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.hadoop.hdfs.client.HdfsUtils

class HdfsClientSpec extends UnitSpec {
  "test" should "work" in {

    // TODO
    val kk = HdfsUtils.isHealthy(new URI("hdfs://localhost:9000/user/root/hdfs-site.xml"))
    println(kk)
    // TODO

    val configuration = new Configuration()
    configuration.set("fs.defaultFS", "hdfs://localhost:9000")
    configuration.set("hadoop.job.ugi", s"root,supergroup")

    val dfs = new DistributedFileSystem
    dfs.initialize(new URI("hdfs://localhost:9000"), configuration)

    val fileStatus = dfs.getFileStatus(new Path("/user/root"))
    fileStatus.isDirectory shouldBe true
  }
}
