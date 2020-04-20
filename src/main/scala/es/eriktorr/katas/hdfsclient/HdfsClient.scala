package es.eriktorr.katas.hdfsclient

import java.net.URI

import com.typesafe.scalalogging.LazyLogging
import es.eriktorr.katas.ApplicationContext
import es.eriktorr.katas.hdfsclient.permissions.{FileAttributes, GroupRead, OtherRead, UserRead}
import org.apache.ftpserver.ftplet.User
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hdfs.DistributedFileSystem

import scala.util.{Failure, Success, Try}

trait HdfsClient {
  def doesExist(path: String): Boolean
  def isDirectory(path: String): Boolean
  def isFile(path: String): Boolean
  def listFiles(path: String): Seq[String]
  def getOwnerName(path: String): String
  def getGroupName(path: String): String
  def isReadable(path: String, user: User): Boolean
  def isWritable(path: String, user: User): Boolean
}

object HdfsClient extends LazyLogging {
  private class InnerHdfsClient(dfs: DistributedFileSystem) extends HdfsClient {
    override def doesExist(path: String): Boolean =
      fileStatus(path) match {
        case Success(_) => true
        case Failure(exception) =>
          warn(message = "doesExist failed", exception = exception, response = false)
      }

    override def isDirectory(path: String): Boolean =
      fileStatus(path) match {
        case Success(fileStatus) => fileStatus.isDirectory
        case Failure(exception) =>
          warn(message = "isDirectory failed", exception = exception, response = false)
      }

    override def isFile(path: String): Boolean =
      fileStatus(path) match {
        case Success(fileStatus) => fileStatus.isFile
        case Failure(exception) =>
          warn(message = "isFile failed", exception = exception, response = false)
      }

    override def listFiles(path: String): Seq[String] =
      Try {
        dfs.listStatus(new Path(path))
      } match {
        case Success(listStatus) => listStatus.toList.map(_.getPath.toString)
        case Failure(exception) =>
          warn(message = "listStatus failed", exception = exception, response = Seq.empty)
      }

    override def getOwnerName(path: String): String = fileStatus(path) match {
      case Success(fileStatus) => fileStatus.getOwner
      case Failure(exception) =>
        warn(message = "getOwnerName failed", exception = exception, response = "")
    }

    override def getGroupName(path: String): String = fileStatus(path) match {
      case Success(fileStatus) => fileStatus.getGroup
      case Failure(exception) =>
        warn(message = "getGroupName failed", exception = exception, response = "")
    }

    override def isReadable(path: String, user: User): Boolean = fileStatus(path) match {
      case Success(fileStatus) => {
        val fileAttributes = FileAttributes(
          owner = fileStatus.getOwner,
          group = fileStatus.getGroup,
          permissions = fileStatus.getPermission.toString
        )
        readValidators.find(_.matches(fileAttributes, user)) match {
          case Some(_) => true
          case None => false
        }
      }
      case Failure(exception) =>
        warn(message = "isReadable failed", exception = exception, response = false)
    }

    override def isWritable(path: String, user: User): Boolean = ???

    private[this] lazy val readValidators = Seq(UserRead, GroupRead, OtherRead)

    private[this] def fileStatus(path: String) =
      Try {
        dfs.getFileStatus(new Path(path))
      }

    private[this] def warn[A](message: String, exception: Throwable, response: A): A = {
      logger.warn(message, exception)
      response
    }
  }

  def apply(context: ApplicationContext): HdfsClient = {
    val hdfsClientConfig = context.hdfsClientConfig

    val configuration = new Configuration()
    configuration.set("fs.defaultFS", hdfsClientConfig.uri)
    configuration.set(
      "hadoop.job.ugi",
      s"${hdfsClientConfig.superUser},${hdfsClientConfig.superGroup}"
    )

    val dfs = new DistributedFileSystem
    dfs.initialize(new URI(hdfsClientConfig.uri), configuration)

    new InnerHdfsClient(dfs)
  }
}
