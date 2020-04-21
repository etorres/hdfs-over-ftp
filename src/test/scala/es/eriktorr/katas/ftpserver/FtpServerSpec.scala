package es.eriktorr.katas.ftpserver

import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec
import es.eriktorr.katas.unitspec.clients.FtpClient

class FtpServerSpec extends UnitSpec {
  private[this] val applicationContext = loadApplicationContext

  "ftp server" should "list files in home directory" in {
    val ftpServer = FtpServer(applicationContext)
    ftpServer.start()

    val files = for {
      ftpClient <- FtpClient("localhost", applicationContext.ftpServerConfig.port)
      files <- ftpClient.listFiles("")
      _ <- ftpClient.close()
    } yield files

    ftpServer.stop()

    files.success.value shouldBe Seq("user")
  }

  "ftp server" should "download files" in {}
}
