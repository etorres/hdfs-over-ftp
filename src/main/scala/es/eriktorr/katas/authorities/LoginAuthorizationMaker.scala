package es.eriktorr.katas.authorities

import es.eriktorr.katas.{AuthorizationMaker, FtpUser}
import org.apache.ftpserver.ftplet.Authority

object LoginAuthorizationMaker extends AuthorizationMaker {
  override def appliesTo(user: FtpUser): Boolean = true

  override def authorityFrom(user: FtpUser): Authority =
    new ConcurrentLoginAuthority(user.maxLoginNumber, user.maxLoginPerIp)
}
