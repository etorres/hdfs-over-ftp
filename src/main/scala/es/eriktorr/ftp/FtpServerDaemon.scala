package es.eriktorr.ftp

import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.ftp.ftpserver.FtpServer
import org.apache.commons.daemon.{Daemon, DaemonContext}

class FtpServerDaemon extends Daemon with OptionParser {
  private[this] val ftpServerRef = new AtomicReference[Option[FtpServer]](None)
  private[this] val options = new AtomicReference[OptionMap](Map())

  override def init(daemonContext: DaemonContext): Unit =
    options.set(optionsFrom(daemonContext.getArguments))

  override def start(): Unit = {
    val applicationContext = ApplicationContextLoader.defaultApplicationContext

    val ftpServer = FtpServer(applicationContext)
    ftpServer.start()

    ftpServerRef.set(Some(FtpServer(applicationContext)))
  }

  override def stop(): Unit = ftpServerRef.get() match {
    case Some(ftpServer) => ftpServer.stop()
    case None =>
  }

  override def destroy(): Unit = {}
}
