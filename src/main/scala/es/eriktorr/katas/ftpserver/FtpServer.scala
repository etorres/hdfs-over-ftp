package es.eriktorr.katas.ftpserver

import es.eriktorr.katas.ApplicationContext
import es.eriktorr.katas.usermanagement.FtpUsers
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.{
  ConnectionConfigFactory,
  DataConnectionConfigurationFactory,
  FtpServerFactory
}

import scala.jdk.CollectionConverters._

trait FtpServer {
  def start(): Unit
  def stop(): Unit
}

object FtpServer {
  private class UnsafeFtpServer(private val ftpServer: org.apache.ftpserver.FtpServer)
      extends FtpServer {
    override def start(): Unit =
      ftpServer.start()
    override def stop(): Unit =
      ftpServer.stop()
  }

  def apply(context: ApplicationContext): FtpServer =
    new UnsafeFtpServer(unsafeFtpServer(context))

  private[this] def unsafeFtpServer(context: ApplicationContext): org.apache.ftpserver.FtpServer = {
    val ftpServerConfig = context.ftpServerConfig

    val dataConnectionConfigurationFactory = new DataConnectionConfigurationFactory
    dataConnectionConfigurationFactory.setPassivePorts(ftpServerConfig.dataPorts)

    val listenerFactory = new ListenerFactory
    listenerFactory.setDataConnectionConfiguration(
      dataConnectionConfigurationFactory.createDataConnectionConfiguration()
    )
    ftpServerConfig.hostname match {
      case Some(hostname) => listenerFactory.setServerAddress(hostname)
      case _ =>
    }
    listenerFactory.setPort(ftpServerConfig.port)

    val connectionConfigFactory = new ConnectionConfigFactory
    connectionConfigFactory.setAnonymousLoginEnabled(ftpServerConfig.enableAnonymous)

    val ftpServerFactory = new FtpServerFactory
    ftpServerFactory.setUserManager(new FtpUsers(ftpServerConfig.ftpUsers))
    ftpServerFactory.setFileSystem(new NativeFileSystemFactory)
    ftpServerFactory.setListeners(Map("default" -> listenerFactory.createListener()).asJava)
    ftpServerFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig())

    ftpServerFactory.createServer()
  }

  val pathTo: String => String = {
    getClass.getClassLoader.getResource(_).getPath
  }
}
