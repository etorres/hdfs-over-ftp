package es.eriktorr.katas

import es.eriktorr.katas.ftpserver.FtpServerConfig
import es.eriktorr.katas.hdfsclient.HdfsClientConfig
import es.eriktorr.katas.unitspec.UnitSpec
import es.eriktorr.katas.unitspec.data.DataProvider

class ApplicationContextSpec extends UnitSpec with DataProvider {
  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.loadApplicationContext shouldBe ApplicationContext(
      FtpServerConfig(
        hostname = Some("localhost"),
        port = 2221,
        dataPorts = "2222-2224",
        enableAnonymous = true,
        Seq(OperatorFtpUser, AnonymousFtpUser)
      ),
      HdfsClientConfig(uri = "hdfs://localhost:9000", superUser = "root", superGroup = "supergroup")
    )
  }
}
