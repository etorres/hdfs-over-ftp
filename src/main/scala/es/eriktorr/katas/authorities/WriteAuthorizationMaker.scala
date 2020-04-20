package es.eriktorr.katas.authorities

import es.eriktorr.katas.{AuthorizationMaker, FtpUser}
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.usermanager.impl.WritePermission

object WriteAuthorizationMaker extends AuthorizationMaker {
  val WritePermissionDefault: Boolean = false

  override def appliesTo(user: FtpUser): Boolean =
    user.writePermission.getOrElse(WritePermissionDefault)

  override def authorityFrom(user: FtpUser): Authority = new WritePermission
}
