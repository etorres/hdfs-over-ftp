package es.eriktorr.ftp

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

  def loadApplicationContextFrom(resourceBasename: String): ApplicationContext = {
    val baseConfig = ConfigFactory.load()
    val config = ConfigFactory.load(resourceBasename).withFallback(baseConfig)

    // TODO ConfigParseOptions.defaults().setAllowMissing(true)

    val ftpServerConfig: FtpServerConfig = config.as[FtpServerConfig](ftpServer)
    val hdfsClientConfig: HdfsClientConfig = config.as[HdfsClientConfig](hdfsClient)

    ApplicationContext(ftpServerConfig, hdfsClientConfig)
  }
}
