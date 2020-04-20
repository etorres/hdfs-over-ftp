package es.eriktorr.katas.ftpserver

import es.eriktorr.katas.usermanagement.FtpUser

sealed case class FtpServerConfig(
  hostname: Option[String],
  port: Int,
  dataPorts: String,
  enableAnonymous: Boolean,
  ftpUsers: Seq[FtpUser]
)
