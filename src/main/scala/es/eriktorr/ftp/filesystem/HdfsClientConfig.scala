package es.eriktorr.ftp.filesystem

import scala.concurrent.duration.FiniteDuration

sealed case class HdfsClientConfig(
  uri: String,
  superUser: String,
  superGroup: String,
  timeout: FiniteDuration
)
