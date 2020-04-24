package es.eriktorr.ftp.filesystem.permissions

import es.eriktorr.ftp.usermanagement.FtpUser
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
      case Some(_) =>
        permission match {
          case 'r' => fileAttributes.permissions(3) == permission
          case 'w' => fileAttributes.permissions(4) == permission
          case _ => false
        }
      case None => false
    }
  }
}
