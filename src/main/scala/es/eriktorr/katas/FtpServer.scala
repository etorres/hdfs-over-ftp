package es.eriktorr.katas

import java.io.File

import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.{PropertiesUserManagerFactory, SaltedPasswordEncryptor}
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

  def apply(): FtpServer =
    new UnsafeFtpServer(unsafeFtpServer)

  private[this] def unsafeFtpServer: org.apache.ftpserver.FtpServer = {
    val dataConnectionConfigurationFactory = new DataConnectionConfigurationFactory
    dataConnectionConfigurationFactory.setPassivePorts("2222-2224")

    val listenerFactory = new ListenerFactory
    listenerFactory.setDataConnectionConfiguration(
      dataConnectionConfigurationFactory.createDataConnectionConfiguration()
    )
    listenerFactory.setPort(2221)

    val connectionConfigFactory = new ConnectionConfigFactory
    connectionConfigFactory.setAnonymousLoginEnabled(true)

    val userManagerFactory = new PropertiesUserManagerFactory
    userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor)
    userManagerFactory.setFile(new File(pathTo("users.properties")))

    val fileSystemFactory = new NativeFileSystemFactory

    val ftpServerFactory = new FtpServerFactory
    ftpServerFactory.setUserManager(userManagerFactory.createUserManager())
    ftpServerFactory.setFileSystem(fileSystemFactory)
    ftpServerFactory.setListeners(Map("default" -> listenerFactory.createListener()).asJava)
    ftpServerFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig())

    ftpServerFactory.createServer()
  }

  val pathTo: String => String = {
    getClass.getClassLoader.getResource(_).getPath
  }
}
