package es.eriktorr.katas.hdfsclient

sealed case class HdfsClientConfig(uri: String, superUser: String, superGroup: String)
