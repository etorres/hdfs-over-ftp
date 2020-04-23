package es.eriktorr.katas.ftpserver

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import better.files._
import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec
import es.eriktorr.katas.unitspec.clients.FtpClient
import org.apache.commons.lang.RandomStringUtils

import scala.util.{Failure, Success, Try}

class FtpServerSpec extends UnitSpec {
  private[this] val applicationContext = loadApplicationContext

  "ftp server" should "list files in home directory" in {
    val files = ftp[Seq[String]](_.listFiles(""))
    files.success.value shouldBe Seq("user")
  }

  "ftp server" should "change the working directory of the user" in {
    val isChanged = ftp[Boolean](_.changeWorkingDirectory("/user/root"))
    isChanged.success.value shouldBe true
  }

  "ftp server" should "create a new directory" in {
    val directory = RandomStringUtils.randomAlphanumeric(32)
    val isCreated = ftp[Boolean](_.makeDirectory(s"/user/root/$directory"))
    isCreated.success.value shouldBe true
  }

  "ftp server" should "download files" in {
    val fileContent = new AtomicReference[String]("")
    File.usingTemporaryFile() { tempFile =>
      ftp[Boolean](_.fetchFile(s"/user/root/input/hdfs-site.xml", tempFile.pathAsString)) match {
        case Success(isDone) =>
          if (isDone) fileContent.set(tempFile.contentAsString)
        case Failure(_) =>
      }
    }
    fileContent.get() shouldBe HdfsSiteContent
  }

  "ftp server" should "upload files" in {
    val isStored = new AtomicBoolean(false)
    val fileName = s"${RandomStringUtils.randomAlphanumeric(32)}.txt"
    File.usingTemporaryFile() { tempFile =>
      tempFile.overwrite("Hello World!")
      ftp[Boolean](_.storeFile(tempFile.pathAsString, s"/user/root/$fileName")) match {
        case Success(result) => isStored.set(result)
        case Failure(_) =>
      }
    }
    isStored.get() shouldBe true
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

  private[this] lazy val HdfsSiteContent =
    """<configuration>
      |    <property>
      |        <name>dfs.replication</name>
      |        <value>1</value>
      |    </property>
      |</configuration>
      |""".stripMargin
}
