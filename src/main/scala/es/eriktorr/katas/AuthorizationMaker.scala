package es.eriktorr.katas

import org.apache.ftpserver.ftplet.Authority

trait AuthorizationMaker {
  def appliesTo(user: FtpUser): Boolean
  def authorityFrom(user: FtpUser): Authority
}
