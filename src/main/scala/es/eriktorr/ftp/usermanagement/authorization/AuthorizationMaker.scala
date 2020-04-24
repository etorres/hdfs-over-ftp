package es.eriktorr.ftp.usermanagement.authorization

import es.eriktorr.ftp.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.Authority

trait AuthorizationMaker {
  def appliesTo(user: FtpUser): Boolean
  def authorityFrom(user: FtpUser): Authority
}
