package es.eriktorr.katas.filesystem

import java.io._
import java.util

import com.typesafe.scalalogging.LazyLogging
import es.eriktorr.katas.filesystem.permissions.{
  FileAttributes,
  GroupPermission,
  OtherPermission,
  UserPermission
}
import es.eriktorr.katas.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.{FtpFile, User}
import org.apache.hadoop.fs.{FSDataOutputStream, FileStatus, Path}
import org.apache.hadoop.hdfs.DistributedFileSystem

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

case class HdfsFtpFile(distributedFileSystem: DistributedFileSystem, fileName: String, user: User)
    extends FtpFile
    with LazyLogging
    with FileNameProcessing {
  override def getAbsolutePath: String = normalized(fileName)

  override def getName: String = simpleNameFrom(getAbsolutePath)

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
    case Failure(_: FileNotFoundException) => false
    case Failure(exception) =>
      warn(message = "doesExist failed", exception = exception, response = false)
  }

  override def isReadable: Boolean = fileStatus(fileName) match {
    case Success(fileStatus) =>
      val fileAttributes = fileAttributesFrom(fileStatus)
      permissionValidators.find(_.canRead(fileAttributes, user)) match {
        case Some(_) => true
        case None => false
      }
    case Failure(exception) =>
      warn(message = "isReadable failed", exception = exception, response = false)
  }

  override def isWritable: Boolean = {
    @tailrec
    def isWritable(path: String): Boolean = fileStatus(path) match {
      case Success(fileStatus) =>
        val fileAttributes = fileAttributesFrom(fileStatus)
        permissionValidators.find(_.canWrite(fileAttributes, user)) match {
          case Some(_) => true
          case None => false
        }
      case Failure(e: FileNotFoundException) =>
        if (path == "/") warn(message = "isWritable failed", exception = e, response = false)
        else isWritable(pathToParent(path))
      case Failure(exception) =>
        warn(message = "isWritable failed", exception = exception, response = false)
    }
    isWritable(fileName)
  }

  private[this] def fileAttributesFrom(fileStatus: FileStatus) =
    FileAttributes(
      owner = fileStatus.getOwner,
      group = fileStatus.getGroup,
      permissions = fileStatus.getPermission.toString
    )

  override def isRemovable: Boolean = isWritable

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

  override def setLastModified(time: Long): Boolean =
    Try {
      distributedFileSystem.setTimes(new Path(fileName), time, time)
    } match {
      case Success(_) => true
      case Failure(exception) =>
        warn(message = "setLastModified failed", exception = exception, response = false)
    }

  override def getSize: Long = fileStatus(fileName) match {
    case Success(fileStatus) => fileStatus.getLen
    case Failure(exception) =>
      warn(message = "getSize failed", exception = exception, response = 0L)
  }

  override def getPhysicalFile: AnyRef = distributedFileSystem.getUri

  override def mkdir(): Boolean =
    Try {
      withOwner[Boolean](new Path(fileName), path => distributedFileSystem.mkdirs(path))
    } match {
      case Success(created) => created
      case Failure(exception) =>
        warn(message = "mkdir failed", exception = exception, response = false)
    }

  override def delete(): Boolean =
    Try {
      distributedFileSystem.delete(new Path(fileName), true)
    } match {
      case Success(deleted) => deleted
      case Failure(exception) =>
        warn(message = "delete failed", exception = exception, response = false)
    }

  override def move(destination: FtpFile): Boolean =
    Try {
      distributedFileSystem.rename(new Path(fileName), new Path(destination.getAbsolutePath))
    } match {
      case Success(moved) => moved
      case Failure(exception) =>
        warn(message = "move failed", exception = exception, response = false)
    }

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

  @SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.Throw"))
  override def createOutputStream(offset: Long): OutputStream =
    if (isWritable) {
      Try {
        withOwner[FSDataOutputStream](
          new Path(fileName),
          path => distributedFileSystem.create(path)
        )
      } match {
        case Success(value) => value
        case Failure(exception) =>
          warn(message = "createOutputStream failed", exception = exception, response = null)
      }
    } else throw new IOException(s"File cannot be written; No write permission on: $fileName")

  @SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.Throw"))
  override def createInputStream(offset: Long): InputStream =
    if (isReadable) {
      Try {
        distributedFileSystem.open(new Path(fileName))
      } match {
        case Success(inputStream) => inputStream
        case Failure(exception) =>
          warn(message = "createInputStream failed", exception = exception, response = null)
      }
    } else throw new IOException(s"File cannot be read; No read permission on: $fileName")

  private[this] def withOwner[A](path: Path, filesystemCommand: Path => A): A = {
    val result = filesystemCommand.apply(path)
    distributedFileSystem.setOwner(path, user.getName, user match {
      case ftpUser: FtpUser => ftpUser.getMainGroup
      case _ => "none"
    })
    result
  }

  private[this] lazy val permissionValidators =
    Seq(UserPermission, GroupPermission, OtherPermission)

  private[this] def fileStatus(path: String) =
    Try {
      distributedFileSystem.getFileStatus(new Path(path))
    }

  private[this] def warn[A](message: String, exception: Throwable, response: A): A = {
    logger.warn(message, exception)
    response
  }
}
