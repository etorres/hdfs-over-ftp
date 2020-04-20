package es.eriktorr.katas.authorities

import es.eriktorr.katas.{AuthorizationMaker, FtpUser}
import org.apache.ftpserver.ftplet.Authority

object TransferRateAuthorizationMaker extends AuthorizationMaker {
  val MaxDownloadRateDefault: Int = 0
  val MaxUploadRateDefault: Int = 0

  override def appliesTo(user: FtpUser): Boolean = true

  override def authorityFrom(user: FtpUser): Authority =
    new TransferRateAuthority(
      user.maxDownloadRate.getOrElse(MaxDownloadRateDefault),
      user.maxUploadRate.getOrElse(MaxUploadRateDefault)
    )
}
