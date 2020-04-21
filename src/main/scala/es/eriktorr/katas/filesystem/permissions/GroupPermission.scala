package es.eriktorr.katas.filesystem.permissions

import es.eriktorr.katas.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.User

object GroupPermission extends PermissionValidator {
  override protected def matches(
    fileAttributes: FileAttributes,
    user: User,
    permission: Char
  ): Boolean = {
    val groups = user match {
      case ftpUser: FtpUser => ftpUser.groups
      case _ => Seq.empty
    }
    groups.find(_ == fileAttributes.group) match {
      case Some(_) => fileAttributes.permissions(3) == permission
      case None => false
    }
  }
}
