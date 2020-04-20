package es.eriktorr.katas.hdfsclient.permissions

import org.apache.ftpserver.ftplet.User

object UserPermission extends PermissionValidator {
  override protected def matches(
    fileAttributes: FileAttributes,
    user: User,
    permission: Char
  ): Boolean = fileAttributes.owner == user.getName && fileAttributes.permissions(0) == permission
}
