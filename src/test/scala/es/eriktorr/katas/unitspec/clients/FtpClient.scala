package es.eriktorr.katas.unitspec.clients

import java.io.IOException

import org.apache.commons.net.ftp.{FTPClient, FTPReply}

import scala.util.{Failure, Success, Try}

trait FtpClient {
  def close(): Try[Unit]
  def listFiles(path: String): Try[Seq[String]]
}

object FtpClient {
  private class UnsafeFtpClient(private val ftpClient: FTPClient) extends FtpClient {
    override def close(): Try[Unit] = Try(ftpClient.disconnect())

    override def listFiles(path: String): Try[Seq[String]] = Try {
      ftpClient.listFiles(path).map(_.getName).toList
    }
  }

  def apply(hostname: String, port: Int): Try[FtpClient] = {
    val ftpClient: FTPClient = new FTPClient()
    ftpClient.connect(hostname, port)
    val replyCode = ftpClient.getReplyCode
    if (FTPReply.isPositiveCompletion(replyCode) && ftpClient.login(
        "operator",
        "s3C4e7"
      ))
      Success(new UnsafeFtpClient(ftpClient))
    else Failure(new IOException("Exception in connecting to FTP Server"))
  }
}
