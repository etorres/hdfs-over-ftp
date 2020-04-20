package es.eriktorr.katas.filesystem

import java.io.{InputStream, OutputStream}
import java.util

import org.apache.ftpserver.ftplet.FtpFile

case class HdfsFtpFile() extends FtpFile {
  override def getAbsolutePath: String = ???

  override def getName: String = ???

  override def isHidden: Boolean = ???

  override def isDirectory: Boolean = ???

  override def isFile: Boolean = ???

  override def doesExist(): Boolean = ???

  override def isReadable: Boolean = ???

  override def isWritable: Boolean = ???

  override def isRemovable: Boolean = ???

  override def getOwnerName: String = ???

  override def getGroupName: String = ???

  override def getLinkCount: Int = ???

  override def getLastModified: Long = ???

  override def setLastModified(time: Long): Boolean = ???

  override def getSize: Long = ???

  override def getPhysicalFile: AnyRef = ???

  override def mkdir(): Boolean = ???

  override def delete(): Boolean = ???

  override def move(destination: FtpFile): Boolean = ???

  override def listFiles(): util.List[_ <: FtpFile] = ???

  override def createOutputStream(offset: Long): OutputStream = ???

  override def createInputStream(offset: Long): InputStream = ???
}
