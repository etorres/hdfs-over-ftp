package es.eriktorr.katas.filesystem

import es.eriktorr.katas.hdfsclient.HdfsClient
import org.apache.ftpserver.ftplet.{FileSystemFactory, FileSystemView, User}

final class HdfsFileSystemManager(hdfsClient: HdfsClient) extends FileSystemFactory {
  override def createFileSystemView(user: User): FileSystemView =
    new HdfsFileSystemView(hdfsClient, user)
}
