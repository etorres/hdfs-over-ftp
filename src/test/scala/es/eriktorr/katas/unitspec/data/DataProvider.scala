package es.eriktorr.katas.unitspec.data

import es.eriktorr.katas.FtpUser
import es.eriktorr.katas.FtpUser.{EnabledDefault, MaxIdleTimeDefault}
import es.eriktorr.katas.authorities.LoginAuthorizationMaker.{
  MaxLoginNumberDefault,
  MaxLoginPerIpDefault
}
import es.eriktorr.katas.authorities.WriteAuthorizationMaker.WritePermissionDefault

trait DataProvider {
  def aFtpUser(name: String, password: String, maxLoginPerIp: Int, enabled: Boolean): FtpUser =
    FtpUser(
      name = name,
      password = password,
      maxLoginPerIp = Some(maxLoginPerIp),
      maxLoginNumber = Some(MaxLoginNumberDefault),
      maxIdleTime = Some(MaxIdleTimeDefault),
      maxDownloadRate = None,
      maxUploadRate = None,
      writePermission = Some(WritePermissionDefault),
      enabled = Some(enabled)
    )

  val OperatorFtpUser: FtpUser = aFtpUser(
    name = "operator",
    password = "62370436:28142C8301BFAC1959EAEA30991958AD",
    maxLoginPerIp = MaxLoginPerIpDefault,
    enabled = EnabledDefault
  )

  val AnonymousFtpUser: FtpUser = aFtpUser(
    name = "anonymous",
    password = "",
    maxLoginPerIp = 1,
    enabled = false
  )
}
