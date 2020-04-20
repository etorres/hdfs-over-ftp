package es.eriktorr.katas.hdfsclient.permissions

import org.apache.ftpserver.ftplet.User

object UserRead extends PermissionValidator {
  override def matches(fileAttributes: FileAttributes, user: User): Boolean =
    fileAttributes.owner == user.getName && fileAttributes.permissions(0) == 'r'
}
