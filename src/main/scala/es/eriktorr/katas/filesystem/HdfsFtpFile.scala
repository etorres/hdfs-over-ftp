package es.eriktorr.katas.filesystem

import java.io.{ByteArrayInputStream, InputStream, OutputStream}
import java.util

import com.typesafe.scalalogging.LazyLogging
import es.eriktorr.katas.filesystem.permissions.{
  FileAttributes,
  GroupPermission,
  OtherPermission,
  UserPermission
}
import org.apache.commons.io.FilenameUtils.{
  normalizeNoEndSeparator,
  separatorsToUnix,
  getName => nameMinusPath
}
import org.apache.ftpserver.ftplet.{FtpFile, User}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hdfs.DistributedFileSystem

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

case class HdfsFtpFile(distributedFileSystem: DistributedFileSystem, fileName: String, user: User)
    extends FtpFile
    with LazyLogging {
  override def getAbsolutePath: String = normalizeNoEndSeparator(separatorsToUnix(fileName))

  override def getName: String = nameMinusPath(getAbsolutePath)

  override def isHidden: Boolean = false

  override def isDirectory: Boolean = fileStatus(fileName) match {
    case Success(fileStatus) => fileStatus.isDirectory
    case Failure(exception) =>
      warn(message = "isDirectory failed", exception = exception, response = false)
  }

  override def isFile: Boolean = fileStatus(fileName) match {
    case Success(fileStatus) => fileStatus.isFile
    case Failure(exception) =>
      warn(message = "isFile failed", exception = exception, response = false)
  }

  override def doesExist(): Boolean = fileStatus(fileName) match {
    case Success(_) => true
    case Failure(exception) =>
      warn(message = "doesExist failed", exception = exception, response = false)
  }

  override def isReadable: Boolean = fileStatus(fileName) match {
    case Success(fileStatus) =>
      val fileAttributes = FileAttributes(
        owner = fileStatus.getOwner,
        group = fileStatus.getGroup,
        permissions = fileStatus.getPermission.toString
      )
      readValidators.find(_.canRead(fileAttributes, user)) match {
        case Some(_) => true
        case None => false
      }
    case Failure(exception) =>
      warn(message = "isReadable failed", exception = exception, response = false)
  }

  override def isWritable: Boolean = fileStatus(fileName) match {
    case Success(fileStatus) =>
      val fileAttributes = FileAttributes(
        owner = fileStatus.getOwner,
        group = fileStatus.getGroup,
        permissions = fileStatus.getPermission.toString
      )
      readValidators.find(_.canWrite(fileAttributes, user)) match {
        case Some(_) => true
        case None => false
      }
    case Failure(exception) =>
      warn(message = "isWritable failed", exception = exception, response = false)
  }

  override def isRemovable: Boolean = ???

  override def getOwnerName: String = fileStatus(fileName) match {
    case Success(fileStatus) => fileStatus.getOwner
    case Failure(exception) =>
      warn(message = "getOwnerName failed", exception = exception, response = "")
  }

  override def getGroupName: String = fileStatus(fileName) match {
    case Success(fileStatus) => fileStatus.getGroup
    case Failure(exception) =>
      warn(message = "getGroupName failed", exception = exception, response = "")
  }

  override def getLinkCount: Int = if (isDirectory()) 3 else 1

  override def getLastModified: Long = fileStatus(fileName) match {
    case Success(fileStatus) => fileStatus.getModificationTime
    case Failure(exception) =>
      warn(message = "getLastModified failed", exception = exception, response = 0L)
  }

  override def setLastModified(time: Long): Boolean = ???

  override def getSize: Long = ???

  override def getPhysicalFile: AnyRef = ???

  override def mkdir(): Boolean = ???

  override def delete(): Boolean = ???

  override def move(destination: FtpFile): Boolean = ???

  override def listFiles(): util.List[_ <: FtpFile] = {
    val fileNames = Try {
      distributedFileSystem.listStatus(new Path(fileName))
    } match {
      case Success(listStatus) => listStatus.toList.map(_.getPath.toString)
      case Failure(exception) =>
        warn(message = "listFiles failed", exception = exception, response = Seq.empty)
    }
    fileNames.map(fn => HdfsFtpFile(distributedFileSystem, fn, user)).asJava
  }

  override def createOutputStream(offset: Long): OutputStream = ???

  override def createInputStream(offset: Long): InputStream =
    if (isReadable) {
      Try {
        distributedFileSystem.open(new Path(fileName))
      } match {
        case Success(inputStream) => inputStream
        case Failure(exception) =>
          warn(
            message = "createInputStream failed",
            exception = exception,
            response = new ByteArrayInputStream(exception.getMessage.getBytes())
          )
      }
    } else new ByteArrayInputStream(s"No read permission: $fileName".getBytes)

  private[this] lazy val readValidators = Seq(UserPermission, GroupPermission, OtherPermission)

  private[this] def fileStatus(path: String) =
    Try {
      distributedFileSystem.getFileStatus(new Path(path))
    }

  private[this] def warn[A](message: String, exception: Throwable, response: A): A = {
    logger.warn(message, exception)
    response
  }
}
