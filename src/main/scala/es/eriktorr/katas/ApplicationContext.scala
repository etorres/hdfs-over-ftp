package es.eriktorr.katas

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

final case class ApplicationContext(ftpUsers: Seq[FtpUser])

object ApplicationContextLoader {
  def loadApplicationContext: ApplicationContext = {
    val config: Config = ConfigFactory.load()

    val ftpUsers: Seq[FtpUser] = config.as[Seq[FtpUser]]("ftp-users")

    ApplicationContext(ftpUsers)
  }
}
