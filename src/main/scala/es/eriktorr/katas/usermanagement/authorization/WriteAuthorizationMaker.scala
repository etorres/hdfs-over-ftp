package es.eriktorr.katas.usermanagement.authorization

import es.eriktorr.katas.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.usermanager.impl.WritePermission

object WriteAuthorizationMaker extends AuthorizationMaker {
  val WritePermissionDefault: Boolean = false

  override def appliesTo(user: FtpUser): Boolean =
    user.writePermission.getOrElse(WritePermissionDefault)

  override def authorityFrom(user: FtpUser): Authority = new WritePermission
}
