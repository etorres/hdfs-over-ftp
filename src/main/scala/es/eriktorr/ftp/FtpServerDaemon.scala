package es.eriktorr.ftp

import java.util.concurrent.atomic.AtomicReference

import com.typesafe.scalalogging.LazyLogging
import es.eriktorr.ftp.ftpserver.FtpServer
import org.apache.commons.daemon.{Daemon, DaemonContext}

import scala.annotation.tailrec

class FtpServerDaemon extends Daemon with LazyLogging {
  type OptionMap = Map[Symbol, String]

  private[this] val ftpServerRef = new AtomicReference[Option[FtpServer]](None)
  private[this] val options = new AtomicReference[Option[OptionMap]](None)

  override def init(daemonContext: DaemonContext): Unit = {
    @tailrec
    def optionMap(parsedOptions: OptionMap, argsList: List[String]): OptionMap =
      argsList match {
        case "-config" :: value :: tail =>
          optionMap(parsedOptions ++ Map(Symbol("config") -> value), tail)
        case ::(value, next) =>
          logger.warn(s"Unknown option ignored: $value")
          optionMap(parsedOptions, next)
        case Nil => parsedOptions
      }
    options.set(Some(optionMap(Map(), daemonContext.getArguments.toList)))
  }

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
