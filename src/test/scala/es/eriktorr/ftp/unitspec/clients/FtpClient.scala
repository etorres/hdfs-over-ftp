package es.eriktorr.ftp.unitspec.clients

import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

import better.files._
import org.apache.commons.net.ftp.{FTPClient, FTPReply}

import scala.util.{Failure, Success, Try}

trait FtpClient {
  def close(): Try[Unit]
  def listFiles(path: String): Try[Seq[String]]
  def fetchFile(source: String, destination: String): Try[Boolean]
  def changeWorkingDirectory(path: String): Try[Boolean]
  def makeDirectory(path: String): Try[Boolean]
  def storeFile(source: String, destination: String): Try[Boolean]
  def rename(from: String, to: String): Try[Boolean]
  def deleteFile(fileName: String): Try[Boolean]
  def removeDirectory(directory: String): Try[Boolean]
}

object FtpClient {
  private class UnsafeFtpClient(private val ftpClient: FTPClient) extends FtpClient {
    override def close(): Try[Unit] = Try(ftpClient.disconnect())

    override def listFiles(path: String): Try[Seq[String]] = Try {
      ftpClient.listFiles(path).map(_.getName).toList
    }

    override def fetchFile(source: String, destination: String): Try[Boolean] = Try {
      val done = new AtomicBoolean(false)
      for {
        outputStream <- File(destination).newOutputStream.autoClosed
      } done.set(ftpClient.retrieveFile(source, outputStream))
      done.get()
    }

    override def changeWorkingDirectory(path: String): Try[Boolean] = Try {
      ftpClient.changeWorkingDirectory(path)
    }

    override def makeDirectory(path: String): Try[Boolean] = Try {
      ftpClient.makeDirectory(path)
    }

    override def storeFile(source: String, destination: String): Try[Boolean] = Try {
      ftpClient.storeFile(destination, File(source).newFileInputStream)
    }

    override def rename(from: String, to: String): Try[Boolean] = Try {
      ftpClient.rename(from, to)
    }

    override def deleteFile(fileName: String): Try[Boolean] = Try {
      ftpClient.deleteFile(fileName)
    }

    override def removeDirectory(directory: String): Try[Boolean] = Try {
      ftpClient.removeDirectory(directory)
    }
  }

  def apply(username: String, password: String, hostname: String, port: Int): Try[FtpClient] = {
    val ftpClient: FTPClient = new FTPClient()
    ftpClient.connect(hostname, port)
    val replyCode = ftpClient.getReplyCode
    if (FTPReply.isPositiveCompletion(replyCode) && ftpClient.login(
        username,
        password
      ))
      Success(new UnsafeFtpClient(ftpClient))
    else Failure(new IOException("Exception in connecting to FTP Server"))
  }
}
