package es.eriktorr.ftp

import es.eriktorr.ftp.ftpserver.FtpServer

object FtpServerApplication extends App with OptionParser {
  val options = optionsFrom(args)

  val applicationContext = options.get(Symbol("config")) match {
    case Some(resourceBasename) =>
      ApplicationContextLoader.loadApplicationContextFrom(resourceBasename)
    case None => ApplicationContextLoader.defaultApplicationContext
  }

  val ftpServer = FtpServer(applicationContext)

  ftpServer.start()
  stopOnVmExit()

  val ftpServerConfig = applicationContext.ftpServerConfig
  val hdfsClientConfig = applicationContext.hdfsClientConfig

  logger.info(
    s"FTP server listening in ${ftpServerConfig.hostname
      .getOrElse("localhost")}/${ftpServerConfig.port} and connected to ${hdfsClientConfig.uri}"
  )

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  private[this] def stopOnVmExit(): Unit =
    sys.addShutdownHook {
      ftpServer.stop()
      logger.info("Goodbye.")
    }
}
