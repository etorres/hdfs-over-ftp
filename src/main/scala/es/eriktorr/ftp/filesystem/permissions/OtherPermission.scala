package es.eriktorr.ftp.filesystem.permissions

import org.apache.ftpserver.ftplet.User

object OtherPermission extends PermissionValidator {
  override protected def matches(
    fileAttributes: FileAttributes,
    user: User,
    permission: Char
  ): Boolean = permission match {
    case 'r' => fileAttributes.permissions(6) == permission
    case 'w' => fileAttributes.permissions(7) == permission
    case _ => false
  }
}
