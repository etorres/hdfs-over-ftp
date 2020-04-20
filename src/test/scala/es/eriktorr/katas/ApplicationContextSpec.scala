package es.eriktorr.katas

import es.eriktorr.katas.config.{FtpServerConfig, HdfsConfig}
import es.eriktorr.katas.unitspec.UnitSpec
import es.eriktorr.katas.unitspec.data.DataProvider

class ApplicationContextSpec extends UnitSpec with DataProvider {
  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.loadApplicationContext shouldBe ApplicationContext(
      FtpServerConfig(
        hostname = Some("localhost"),
        port = 2221,
        dataPorts = "2222-2224",
        enableAnonymous = true
      ),
      Seq(OperatorFtpUser, AnonymousFtpUser),
      HdfsConfig(uri = "hdfs://localhost:9000", superUser = "root", superGroup = "supergroup")
    )
  }
}
