package es.eriktorr.ftp.filesystem

sealed case class HdfsClientConfig(uri: String, superUser: String, superGroup: String)
