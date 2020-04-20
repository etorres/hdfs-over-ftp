package es.eriktorr.katas.filesystem

import org.apache.ftpserver.ftplet.{FileSystemFactory, FileSystemView, User}

object HdfsFileSystemManager extends FileSystemFactory {
  override def createFileSystemView(user: User): FileSystemView = new HdfsFileSystemView(user)
}
