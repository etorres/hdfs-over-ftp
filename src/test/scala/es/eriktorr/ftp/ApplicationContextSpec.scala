package es.eriktorr.ftp

import es.eriktorr.ftp.filesystem.HdfsClientConfig
import es.eriktorr.ftp.ftpserver.FtpServerConfig
import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider

class ApplicationContextSpec extends UnitSpec with DataProvider {
  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.defaultApplicationContext shouldBe anApplicationContext(
      hostname = "localhost",
      port = 2221
    )
  }

  "Application context" should "be loaded from file" in {
    ApplicationContextLoader.loadApplicationContextFrom("application-test.conf") shouldBe
      anApplicationContext(hostname = "127.0.0.1", port = 21)
  }

  private[this] def anApplicationContext(hostname: String, port: Int) = ApplicationContext(
    FtpServerConfig(
      hostname = Some(hostname),
      port = port,
      dataPorts = "2222-2224",
      enableAnonymous = true,
      Seq(RootFtpUser, AnonymousFtpUser)
    ),
    HdfsClientConfig(uri = "hdfs://localhost:9000", superUser = "root", superGroup = "supergroup")
  )
}
