package es.eriktorr.ftp

import java.util.concurrent.atomic.AtomicReference

import better.files.File
import es.eriktorr.ftp.filesystem.{HdfsClientConfig, HdfsLimits}
import es.eriktorr.ftp.ftpserver.FtpServerConfig
import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider

class ApplicationContextSpec extends UnitSpec with DataProvider {
  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.defaultApplicationContext shouldBe anApplicationContext(
      "localhost",
      2221
    )
  }

  it should "be loaded from file" in {
    val applicationContextRef = new AtomicReference[ApplicationContext]()

    File.usingTemporaryFile() { tempFile =>
      tempFile.overwrite("""ftpServer = {
                           |  hostname = "127.0.0.1"
                           |  port = 21
                           |}
                           |""".stripMargin)
      val applicationContext =
        ApplicationContextLoader.loadApplicationContextFrom(tempFile.pathAsString)
      applicationContextRef.set(applicationContext)
    }

    applicationContextRef.get() shouldBe anApplicationContext(
      "127.0.0.1",
      21
    )
  }

  private[this] def anApplicationContext(hostname: String, port: Int) = ApplicationContext(
    FtpServerConfig(
      hostname = Some(hostname),
      port = port,
      dataPorts = "2222-2224",
      enableAnonymous = true,
      Seq(RootFtpUser, AnonymousFtpUser)
    ),
    HdfsClientConfig(
      uri = "hdfs://localhost:9000",
      superUser = "root",
      superGroup = "supergroup",
      hdfsLimits = HdfsLimits(maxListedFiles = 1000)
    )
  )
}
