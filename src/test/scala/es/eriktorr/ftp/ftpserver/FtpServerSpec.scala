package es.eriktorr.ftp.ftpserver

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import better.files._
import es.eriktorr.ftp.ApplicationContextLoader.defaultApplicationContext
import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.clients.FtpClient
import org.apache.commons.lang3.RandomStringUtils

import scala.util.{Failure, Success, Try}

class FtpServerSpec extends UnitSpec {
  private[this] val applicationContext = defaultApplicationContext

  "Ftp server" should "list files in home directory" in {
    val files = ftp[Seq[String]](_.listFiles("input"))
    files.success.value shouldBe FilesInUserRootInput
  }

  it should "change the working directory of the user" in {
    val isChanged = ftp[Boolean](_.changeWorkingDirectory("/user/root"))
    isChanged.success.value shouldBe true
  }

  it should "create a new directory" in {
    val directory = RandomStringUtils.randomAlphanumeric(32)
    val isCreated = ftp[Boolean](_.makeDirectory(s"/user/root/$directory"))
    isCreated.success.value shouldBe true
  }

  it should "download files" in {
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

  it should "upload files" in {
    val isStored = new AtomicBoolean(false)
    val fileName = s"${RandomStringUtils.randomAlphanumeric(32)}.txt"
    File.usingTemporaryFile() { tempFile =>
      tempFile.overwrite("Hello World!\n")
      ftp[Boolean](_.storeFile(tempFile.pathAsString, s"/user/root/$fileName")) match {
        case Success(result) => isStored.set(result)
        case Failure(_) =>
      }
    }
    isStored.get() shouldBe true
  }

  it should "rename an existing file" in {
    val fileName = createHdfsFile()
    val isMoved = ftp[Boolean](_.rename(fileName, s"${fileName}_renamed"))
    isMoved.success.value shouldBe true
  }

  it should "rename an existing directory" in {
    val directory = createHdfsDirectory()
    val isMoved = ftp[Boolean](_.rename(directory, s"${directory}_renamed"))
    isMoved.success.value shouldBe true
  }

  it should "delete an existing file" in {
    val fileName = createHdfsFile()
    val isDeleted = ftp[Boolean](_.deleteFile(fileName))
    isDeleted.success.value shouldBe true
  }

  it should "delete a non-empty directory" in {
    val directory = createHdfsDirectory()
    val isDeleted = ftp[Boolean](_.removeDirectory(directory))
    isDeleted.success.value shouldBe true
  }

  private[this] def createHdfsDirectory(): String = {
    val directory = s"/user/root/${RandomStringUtils.randomAlphanumeric(32)}"
    val created: Try[Boolean] = ftp[Boolean](_.makeDirectory(directory))
    require(created.getOrElse(false), s"The directory was not created: $directory")

    File.usingTemporaryFile() { tempFile =>
      tempFile.overwrite("Hello World!\n")
      ftp[Boolean](_.storeFile(tempFile.pathAsString, s"$directory/${tempFile.name}.txt"))
    }

    directory
  }

  private[this] def createHdfsFile(): String = {
    val fileName = s"/user/root/${RandomStringUtils.randomAlphanumeric(32)}.txt"
    File.usingTemporaryFile() { tempFile =>
      tempFile.overwrite("Hello World!\n")
      ftp[Boolean](_.storeFile(tempFile.pathAsString, fileName))
    }
    fileName
  }

  private[this] def ftp[A](fx: FtpClient => Try[A]): Try[A] = {
    val ftpServer = FtpServer(applicationContext)
    ftpServer.start()

    val result = for {
      ftpClient <- FtpClient(
        username = "root",
        password = "s3C4e7",
        hostname = "localhost",
        port = applicationContext.ftpServerConfig.port
      )
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

  private[this] lazy val FilesInUserRootInput = Seq(
    "capacity-scheduler.xml",
    "configuration.xsl",
    "container-executor.cfg",
    "core-site.xml",
    "core-site.xml.template",
    "hadoop-env.cmd",
    "hadoop-env.sh",
    "hadoop-metrics.properties",
    "hadoop-metrics2.properties",
    "hadoop-policy.xml",
    "hdfs-site.xml",
    "httpfs-env.sh",
    "httpfs-log4j.properties",
    "httpfs-signature.secret",
    "httpfs-site.xml",
    "kms-acls.xml",
    "kms-env.sh",
    "kms-log4j.properties",
    "kms-site.xml",
    "log4j.properties",
    "mapred-env.cmd",
    "mapred-env.sh",
    "mapred-queues.xml.template",
    "mapred-site.xml",
    "mapred-site.xml.template",
    "slaves",
    "ssl-client.xml.example",
    "ssl-server.xml.example",
    "yarn-env.cmd",
    "yarn-env.sh",
    "yarn-site.xml"
  )
}
