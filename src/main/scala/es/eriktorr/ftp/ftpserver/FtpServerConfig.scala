package es.eriktorr.ftp.ftpserver

import es.eriktorr.ftp.usermanagement.FtpUser

sealed case class FtpServerConfig(
  hostname: Option[String],
  port: Int,
  dataPorts: String,
  enableAnonymous: Boolean,
  ftpUsers: Seq[FtpUser]
)
