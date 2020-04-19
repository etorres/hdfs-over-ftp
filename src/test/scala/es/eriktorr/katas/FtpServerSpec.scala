package es.eriktorr.katas

import es.eriktorr.katas.unitspec.UnitSpec

class FtpServerSpec extends UnitSpec {
  "ftp server" should "list files in home directory" in {
    val ftpServer = FtpServer()
    ftpServer.start()

    val files = for {
      ftpClient <- FtpClient("localhost", 2221)
      files <- ftpClient.listFiles("")
      _ <- ftpClient.close()
    } yield files

    files.success.value shouldBe Seq()
  }
}
