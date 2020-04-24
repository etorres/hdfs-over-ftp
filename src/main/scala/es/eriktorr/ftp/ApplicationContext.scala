package es.eriktorr.ftp

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import es.eriktorr.ftp.filesystem.HdfsClientConfig
import es.eriktorr.ftp.ftpserver.FtpServerConfig
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

final case class ApplicationContext(
  ftpServerConfig: FtpServerConfig,
  hdfsClientConfig: HdfsClientConfig
)

object ApplicationContextLoader {
  private[this] val ftpServer = "ftpServer"
  private[this] val hdfsClient = "hdfsClient"

  def defaultApplicationContext: ApplicationContext = {
    val config: Config = ConfigFactory.load()

    val ftpServerConfig: FtpServerConfig = config.as[FtpServerConfig](ftpServer)
    val hdfsClientConfig: HdfsClientConfig = config.as[HdfsClientConfig](hdfsClient)

    ApplicationContext(ftpServerConfig, hdfsClientConfig)
  }

  def loadApplicationContextFrom(fileName: String): ApplicationContext = {
    val baseConfig = ConfigFactory.load()
    val customConfig = ConfigFactory.parseFile(new File(fileName))

    val ftpServerConfig: FtpServerConfig =
      customConfig.withFallback(baseConfig).as[FtpServerConfig](ftpServer)
    val hdfsClientConfig: HdfsClientConfig =
      customConfig.withFallback(baseConfig).as[HdfsClientConfig](hdfsClient)

    ApplicationContext(ftpServerConfig, hdfsClientConfig)
  }
}
