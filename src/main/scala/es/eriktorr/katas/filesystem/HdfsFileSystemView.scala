package es.eriktorr.katas.filesystem

import org.apache.ftpserver.ftplet.{FileSystemView, FtpFile, User}

class HdfsFileSystemView(user: User) extends FileSystemView {
  override def getHomeDirectory: FtpFile = ???

  override def getWorkingDirectory: FtpFile = ???

  override def changeWorkingDirectory(dir: String): Boolean = ???

  override def getFile(file: String): FtpFile = ???

  override def isRandomAccessible: Boolean = ???

  override def dispose(): Unit = ???
}
