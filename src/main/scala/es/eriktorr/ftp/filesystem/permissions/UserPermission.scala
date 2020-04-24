package es.eriktorr.ftp.filesystem.permissions

import org.apache.ftpserver.ftplet.User

object UserPermission extends PermissionValidator {
  override protected def matches(
    fileAttributes: FileAttributes,
    user: User,
    permission: Char
  ): Boolean =
    (fileAttributes.owner == user.getName) && (permission match {
      case 'r' => fileAttributes.permissions(0) == permission
      case 'w' => fileAttributes.permissions(1) == permission
      case _ => false
    })
}
