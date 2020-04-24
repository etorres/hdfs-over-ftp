package es.eriktorr.ftp.ftpserver

import es.eriktorr.ftp.ApplicationContext
import es.eriktorr.ftp.ftpserver.internal.FtpServerBuilder

trait FtpServer {
  def start(): Unit
  def stop(): Unit
}

object FtpServer {
  final private class UnsafeFtpServer(private val ftpServer: org.apache.ftpserver.FtpServer)
      extends FtpServer {
    override def start(): Unit = ftpServer.start()
    override def stop(): Unit = ftpServer.stop()
  }

  def apply(context: ApplicationContext): FtpServer = new UnsafeFtpServer(unsafeFtpServer(context))

  private[this] def unsafeFtpServer(context: ApplicationContext): org.apache.ftpserver.FtpServer = {
    val ftpServerConfig = context.ftpServerConfig
    val hdfsClientConfig = context.hdfsClientConfig

    FtpServerBuilder(
      ftpServerConfig.hostname,
      ftpServerConfig.port,
      ftpServerConfig.dataPorts,
      ftpServerConfig.ftpUsers,
      ftpServerConfig.enableAnonymous,
      hdfsClientConfig
    ).newFtpServer()
  }
}
