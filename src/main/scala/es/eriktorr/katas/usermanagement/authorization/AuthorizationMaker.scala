package es.eriktorr.katas.usermanagement.authorization

import es.eriktorr.katas.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.Authority

trait AuthorizationMaker {
  def appliesTo(user: FtpUser): Boolean
  def authorityFrom(user: FtpUser): Authority
}
