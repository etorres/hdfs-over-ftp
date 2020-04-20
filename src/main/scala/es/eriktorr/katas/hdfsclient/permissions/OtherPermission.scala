package es.eriktorr.katas.hdfsclient.permissions

import org.apache.ftpserver.ftplet.User

object OtherPermission extends PermissionValidator {
  override protected def matches(
    fileAttributes: FileAttributes,
    user: User,
    permission: Char
  ): Boolean =
    fileAttributes.permissions(6) == permission
}
