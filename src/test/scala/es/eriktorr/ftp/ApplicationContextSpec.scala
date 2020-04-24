package es.eriktorr.ftp

import es.eriktorr.ftp.filesystem.HdfsClientConfig
import es.eriktorr.ftp.ftpserver.FtpServerConfig
import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider

class ApplicationContextSpec extends UnitSpec with DataProvider {
  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.loadApplicationContext shouldBe ApplicationContext(
      FtpServerConfig(
        hostname = Some("localhost"),
        port = 2221,
        dataPorts = "2222-2224",
        enableAnonymous = true,
        Seq(RootFtpUser, AnonymousFtpUser)
      ),
      HdfsClientConfig(uri = "hdfs://localhost:9000", superUser = "root", superGroup = "supergroup")
    )
  }
}