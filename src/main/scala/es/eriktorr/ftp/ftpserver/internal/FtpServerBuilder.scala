package es.eriktorr.ftp.ftpserver.internal

import es.eriktorr.ftp.filesystem.{HdfsClientConfig, HdfsFileSystemManager}
import es.eriktorr.ftp.usermanagement.{FtpUser, FtpUsers}
import org.apache.ftpserver.ftplet.{FileSystemFactory, UserManager}
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.{
  ConnectionConfigFactory,
  DataConnectionConfigurationFactory,
  FtpServer,
  FtpServerFactory
}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

trait FtpServerBuilder {
  def newFtpServer(): FtpServer
}

object FtpServerBuilder {
  final private class InnerFtpServerBuilder(
    userManager: UserManager,
    fileSystemFactory: FileSystemFactory,
    listenerFactory: ListenerFactory,
    connectionConfigFactory: ConnectionConfigFactory
  ) extends FtpServerBuilder {
    override def newFtpServer(): FtpServer = {
      val ftpServerFactory = new FtpServerFactory

      ftpServerFactory.setUserManager(userManager)
      ftpServerFactory.setFileSystem(fileSystemFactory)
      ftpServerFactory.setListeners(
        mutable.Map("default" -> listenerFactory.createListener()).asJava
      )
      ftpServerFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig())

      ftpServerFactory.createServer()
    }
  }

  def apply(
    hostname: Option[String],
    port: Int,
    dataPorts: String,
    users: Seq[FtpUser],
    enableAnonymous: Boolean,
    hdfsClientConfig: HdfsClientConfig
  ): FtpServerBuilder = {
    val dataConnectionConfigurationFactory = new DataConnectionConfigurationFactory
    dataConnectionConfigurationFactory.setPassivePorts(dataPorts)

    val listenerFactory = new ListenerFactory
    listenerFactory.setDataConnectionConfiguration(
      dataConnectionConfigurationFactory.createDataConnectionConfiguration()
    )
    hostname match {
      case Some(h) => listenerFactory.setServerAddress(h)
      case _ =>
    }
    listenerFactory.setPort(port)

    val connectionConfigFactory = new ConnectionConfigFactory
    connectionConfigFactory.setAnonymousLoginEnabled(enableAnonymous)

    new InnerFtpServerBuilder(
      new FtpUsers(users),
      HdfsFileSystemManager(hdfsClientConfig),
      listenerFactory,
      connectionConfigFactory
    )
  }
}
