package es.eriktorr.ftp

import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.ftp.ftpserver.FtpServer
import org.apache.commons.daemon.{Daemon, DaemonContext}

class FtpServerDaemon extends Daemon with OptionParser {
  private[this] val ftpServerRef = new AtomicReference[Option[FtpServer]](None)
  private[this] val optionsRef = new AtomicReference[OptionMap](Map())

  override def init(daemonContext: DaemonContext): Unit =
    optionsRef.set(optionsFrom(daemonContext.getArguments))

  override def start(): Unit = {
    val options = optionsRef.get()

    val applicationContext = options.get(Symbol("config")) match {
      case Some(resourceBasename) =>
        ApplicationContextLoader.loadApplicationContextFrom(resourceBasename)
      case None => ApplicationContextLoader.defaultApplicationContext
    }

    val ftpServer = FtpServer(applicationContext)
    ftpServer.start()

    ftpServerRef.set(Some(FtpServer(applicationContext)))

    val ftpServerConfig = applicationContext.ftpServerConfig
    val hdfsClientConfig = applicationContext.hdfsClientConfig
    logger.info(
      s"FTP server listening in ${ftpServerConfig.hostname
        .getOrElse("localhost")}/${ftpServerConfig.port} and connected to ${hdfsClientConfig.uri}"
    )
  }

  override def stop(): Unit = ftpServerRef.get() match {
    case Some(ftpServer) => ftpServer.stop()
    case None =>
  }

  override def destroy(): Unit = {}
}
