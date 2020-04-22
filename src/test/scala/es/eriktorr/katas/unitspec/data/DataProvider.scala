package es.eriktorr.katas.unitspec.data

import es.eriktorr.katas.usermanagement.FtpUser
import es.eriktorr.katas.usermanagement.FtpUser.{EnabledDefault, MaxIdleTimeDefault}
import es.eriktorr.katas.usermanagement.authorization.LoginAuthorizationMaker.{
  MaxLoginNumberDefault,
  MaxLoginPerIpDefault
}
import es.eriktorr.katas.usermanagement.authorization.WriteAuthorizationMaker.WritePermissionDefault

trait DataProvider {
  def aFtpUser(
    name: String,
    password: String,
    groups: Seq[String],
    maxLoginPerIp: Int,
    writePermission: Boolean,
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
      writePermission = Some(writePermission),
      enabled = Some(enabled)
    )

  val RootFtpUser: FtpUser = aFtpUser(
    name = "root",
    password = "62370436:28142C8301BFAC1959EAEA30991958AD",
    groups = Seq("supergroup"),
    maxLoginPerIp = MaxLoginPerIpDefault,
    writePermission = true,
    enabled = EnabledDefault
  )

  val AnonymousFtpUser: FtpUser = aFtpUser(
    name = "anonymous",
    password = "",
    groups = Seq.empty,
    maxLoginPerIp = 1,
    writePermission = WritePermissionDefault,
    enabled = false
  )
}
