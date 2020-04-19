package es.eriktorr.katas.authorities

import es.eriktorr.katas.{AuthorizationMaker, FtpUser}
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.usermanager.impl.TransferRatePermission

object TransferRateAuthorizationMaker extends AuthorizationMaker {
  override def appliesTo(user: FtpUser): Boolean = true

  override def authorityFrom(user: FtpUser): Authority =
    new TransferRatePermission(user.maxDownloadRate, user.maxUploadRate)
}
