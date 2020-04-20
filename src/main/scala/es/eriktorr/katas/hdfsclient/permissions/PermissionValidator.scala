package es.eriktorr.katas.hdfsclient.permissions

import org.apache.ftpserver.ftplet.User

sealed case class FileAttributes(owner: String, group: String, permissions: String)

trait PermissionValidator {
  def matches(fileAttributes: FileAttributes, user: User): Boolean
}
