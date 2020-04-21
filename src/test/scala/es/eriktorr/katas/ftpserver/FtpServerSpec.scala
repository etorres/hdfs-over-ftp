package es.eriktorr.katas.ftpserver

import java.util.concurrent.atomic.AtomicBoolean

import better.files._
import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec
import es.eriktorr.katas.unitspec.clients.FtpClient

import scala.util.{Failure, Success, Try}

class FtpServerSpec extends UnitSpec {
  private[this] val applicationContext = loadApplicationContext

  "ftp server" should "list files in home directory" in {
    val files = ftp[Seq[String]](_.listFiles(""))
    files.success.value shouldBe Seq("user")
  }

  "ftp server" should "download files" in {
    val result = new AtomicBoolean(false)
    File.usingTemporaryFile() { tempFile =>
      // TODO
      println(s"\n\n >> HERE: ${tempFile.pathAsString}\n")
      // TODO

      ftp[Boolean](_.fetchFile(s"/user/root/input/hdfs-site.xml", tempFile.pathAsString)) match {
        case Success(isDone) => result.set(isDone)
        case Failure(_) =>
      }
    }
    result.get() shouldBe true
  }

  private[this] def ftp[A](fx: FtpClient => Try[A]): Try[A] = {
    val ftpServer = FtpServer(applicationContext)
    ftpServer.start()

    val result = for {
      ftpClient <- FtpClient("localhost", applicationContext.ftpServerConfig.port)
      files <- fx(ftpClient)
      _ <- ftpClient.close()
    } yield files

    ftpServer.stop()
    result
  }
}
