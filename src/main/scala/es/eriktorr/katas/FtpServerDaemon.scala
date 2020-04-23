package es.eriktorr.katas

import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.katas.ftpserver.FtpServer
import org.apache.commons.daemon.{Daemon, DaemonContext}

class FtpServerDaemon extends Daemon {
  private[this] val ftpServerRef = new AtomicReference[Option[FtpServer]](None)

  override def init(context: DaemonContext): Unit = {}

  override def start(): Unit = {
    val applicationContext = ApplicationContextLoader.loadApplicationContext

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
