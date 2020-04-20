package es.eriktorr.katas.hdfsclient.permissions

import es.eriktorr.katas.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.User

object GroupRead extends PermissionValidator {
  override def matches(fileAttributes: FileAttributes, user: User): Boolean = {
    val groups = user match {
      case ftpUser: FtpUser => ftpUser.groups
      case _ => Seq.empty
    }
    groups.find(_ == fileAttributes.group) match {
      case Some(_) => fileAttributes.permissions(3) == 'r'
      case None => false
    }
  }
}
