package es.eriktorr.katas

import com.typesafe.config.{Config, ConfigFactory}
import es.eriktorr.katas.filesystem.HdfsClientConfig
import es.eriktorr.katas.ftpserver.FtpServerConfig
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

final case class ApplicationContext(
  ftpServerConfig: FtpServerConfig,
  hdfsClientConfig: HdfsClientConfig
)

object ApplicationContextLoader {
  def loadApplicationContext: ApplicationContext = {
    val config: Config = ConfigFactory.load()

    val ftpServerConfig: FtpServerConfig = config.as[FtpServerConfig]("ftpServer")
    val hdfsClientConfig: HdfsClientConfig = config.as[HdfsClientConfig]("hdfsClient")

    ApplicationContext(ftpServerConfig, hdfsClientConfig)
  }
}
