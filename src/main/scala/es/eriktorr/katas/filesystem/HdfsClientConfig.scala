package es.eriktorr.katas.filesystem

sealed case class HdfsClientConfig(uri: String, superUser: String, superGroup: String)
