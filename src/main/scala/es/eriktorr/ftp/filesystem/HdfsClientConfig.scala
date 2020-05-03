package es.eriktorr.ftp.filesystem

sealed case class HdfsLimits(maxListedFiles: Int)

sealed case class HdfsClientConfig(
  uri: String,
  superUser: String,
  superGroup: String,
  enableChrootJail: Boolean,
  hdfsLimits: HdfsLimits
)
