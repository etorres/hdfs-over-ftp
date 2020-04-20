package es.eriktorr.katas.filesystem

import java.io.{InputStream, OutputStream}
import java.util

import es.eriktorr.katas.hdfsclient.HdfsClient
import org.apache.ftpserver.ftplet.{FtpFile, User}

import scala.jdk.CollectionConverters._

case class HdfsFtpFile(hdfsClient: HdfsClient, fileName: String, user: User) extends FtpFile {
  override def getAbsolutePath: String = ???

  override def getName: String = ???

  override def isHidden: Boolean = false

  override def isDirectory: Boolean = hdfsClient.isDirectory(fileName)

  override def isFile: Boolean = hdfsClient.isFile(fileName)

  override def doesExist(): Boolean = hdfsClient.doesExist(fileName)

  override def isReadable: Boolean = hdfsClient.isReadable(fileName, user)

  override def isWritable: Boolean = hdfsClient.isWritable(fileName, user)

  override def isRemovable: Boolean = ???

  override def getOwnerName: String = hdfsClient.getOwnerName(fileName)

  override def getGroupName: String = hdfsClient.getGroupName(fileName)

  override def getLinkCount: Int = ???

  override def getLastModified: Long = ???

  override def setLastModified(time: Long): Boolean = ???

  override def getSize: Long = ???

  override def getPhysicalFile: AnyRef = ???

  override def mkdir(): Boolean = ???

  override def delete(): Boolean = ???

  override def move(destination: FtpFile): Boolean = ???

  override def listFiles(): util.List[_ <: FtpFile] =
    hdfsClient.listFiles(fileName).map(f => HdfsFtpFile(hdfsClient, f, user)).asJava

  override def createOutputStream(offset: Long): OutputStream = ???

  override def createInputStream(offset: Long): InputStream = ???
}
