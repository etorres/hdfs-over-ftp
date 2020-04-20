package es.eriktorr.katas.config

sealed case class HdfsConfig(uri: String, superUser: String, superGroup: String)
