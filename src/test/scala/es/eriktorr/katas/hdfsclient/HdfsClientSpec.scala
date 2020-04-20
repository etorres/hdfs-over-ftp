package es.eriktorr.katas.hdfsclient

import java.net.URI

import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec
import org.apache.hadoop.hdfs.client.HdfsUtils

class HdfsClientSpec extends UnitSpec {
  private[this] val applicationContext = loadApplicationContext

  "HDFS" should "be healthy" in {
    HdfsUtils.isHealthy(
      new URI(s"${applicationContext.hdfsClientConfig.uri}/user/root/hdfs-site.xml")
    ) shouldBe true
  }

  "HDFS client" should "distinguish directories from files" in {
    val hdfsClient = HdfsClient(applicationContext)
    hdfsClient.isDirectory("/user/root") shouldBe true
  }
}
