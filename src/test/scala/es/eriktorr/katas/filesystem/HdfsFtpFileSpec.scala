package es.eriktorr.katas.filesystem

import java.net.URI

import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec
import es.eriktorr.katas.unitspec.data.DataProvider
import org.apache.hadoop.hdfs.client.HdfsUtils

class HdfsFtpFileSpec extends UnitSpec with DataProvider {
  private[this] val applicationContext = loadApplicationContext

  "HDFS" should "be healthy" in {
    HdfsUtils.isHealthy(
      new URI(s"${applicationContext.hdfsClientConfig.uri}")
    ) shouldBe true
  }
}
