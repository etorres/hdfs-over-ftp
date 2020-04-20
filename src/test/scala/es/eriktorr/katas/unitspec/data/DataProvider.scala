package es.eriktorr.katas.unitspec.data

import es.eriktorr.katas.usermanagement.FtpUser.{EnabledDefault, MaxIdleTimeDefault}
import es.eriktorr.katas.usermanagement.authorization.LoginAuthorizationMaker.{
  MaxLoginNumberDefault,
  MaxLoginPerIpDefault
}
import es.eriktorr.katas.usermanagement.authorization.WriteAuthorizationMaker.WritePermissionDefault
import es.eriktorr.katas.usermanagement.FtpUser

trait DataProvider {
  def aFtpUser(
    name: String,
    password: String,
    groups: Seq[String],
    maxLoginPerIp: Int,
    enabled: Boolean
  ): FtpUser =
    FtpUser(
      name = name,
      password = password,
      groups = groups,
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
    groups = Seq("supergroup"),
    maxLoginPerIp = MaxLoginPerIpDefault,
    enabled = EnabledDefault
  )

  val AnonymousFtpUser: FtpUser = aFtpUser(
    name = "anonymous",
    password = "",
    groups = Seq.empty,
    maxLoginPerIp = 1,
    enabled = false
  )
}
