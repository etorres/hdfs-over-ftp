package es.eriktorr.katas.config

sealed case class FtpServerConfig(
  hostname: Option[String],
  port: Int,
  dataPorts: String,
  enableAnonymous: Boolean
)
