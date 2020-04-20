package es.eriktorr.katas.hdfsclient.permissions

import org.apache.ftpserver.ftplet.User

object OtherRead extends PermissionValidator {
  override def matches(fileAttributes: FileAttributes, user: User): Boolean =
    fileAttributes.permissions(6) == 'r'
}
