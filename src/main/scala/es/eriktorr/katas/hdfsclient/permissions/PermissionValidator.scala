package es.eriktorr.katas.hdfsclient.permissions

import org.apache.ftpserver.ftplet.User

sealed case class FileAttributes(owner: String, group: String, permissions: String)

trait PermissionValidator {
  def canRead(fileAttributes: FileAttributes, user: User): Boolean =
    matches(fileAttributes, user, 'r')

  def canWrite(fileAttributes: FileAttributes, user: User): Boolean =
    matches(fileAttributes, user, 'w')

  protected def matches(fileAttributes: FileAttributes, user: User, permission: Char): Boolean
}
