package es.eriktorr.ftp.usermanagement.authorization

import es.eriktorr.ftp.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.Authority

object LoginAuthorizationMaker extends AuthorizationMaker {
  val MaxLoginNumberDefault: Int = 4
  val MaxLoginPerIpDefault: Int = 2

  override def appliesTo(user: FtpUser): Boolean = true

  override def authorityFrom(user: FtpUser): Authority =
    new ConcurrentLoginAuthority(
      user.maxLoginNumber.getOrElse(MaxLoginNumberDefault),
      user.maxLoginPerIp.getOrElse(MaxLoginPerIpDefault)
    )
}
