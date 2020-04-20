package es.eriktorr.katas

import com.typesafe.config.{Config, ConfigFactory}
import es.eriktorr.katas.config.{FtpServerConfig, HdfsConfig}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

final case class ApplicationContext(
  ftpServerConfig: FtpServerConfig,
  ftpUsers: Seq[FtpUser],
  hdfsConfig: HdfsConfig
)

object ApplicationContextLoader {
  def loadApplicationContext: ApplicationContext = {
    val config: Config = ConfigFactory.load()

    val ftpServerConfig: FtpServerConfig = config.as[FtpServerConfig]("ftp-server")
    val ftpUsers: Seq[FtpUser] = config.as[Seq[FtpUser]]("ftp-users")
    val hdfsConfig: HdfsConfig = config.as[HdfsConfig]("hdfs-backend")

    ApplicationContext(ftpServerConfig, ftpUsers, hdfsConfig)
  }
}
