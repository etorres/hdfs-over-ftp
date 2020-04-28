package es.eriktorr.ftp.unitspec.data

import es.eriktorr.ftp.usermanagement.FtpUser
import es.eriktorr.ftp.usermanagement.FtpUser.{EnabledDefault, MaxIdleTimeDefault}
import es.eriktorr.ftp.usermanagement.authorization.LoginAuthorizationMaker.{
  MaxLoginNumberDefault,
  MaxLoginPerIpDefault
}
import es.eriktorr.ftp.usermanagement.authorization.WriteAuthorizationMaker.WritePermissionDefault

trait DataProvider {
  def aFtpUser(
    name: String,
    password: String,
    groups: Seq[String],
    homeDirectory: Option[String],
    maxLoginPerIp: Int,
    writePermission: Boolean,
    enabled: Boolean
  ): FtpUser =
    FtpUser(
      name = name,
      password = password,
      groups = groups,
      homeDirectory = homeDirectory,
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
    homeDirectory = None,
    maxLoginPerIp = MaxLoginPerIpDefault,
    writePermission = true,
    enabled = EnabledDefault
  )

  val AnonymousFtpUser: FtpUser = aFtpUser(
    name = "anonymous",
    password = "",
    groups = Seq.empty,
    homeDirectory = Some("/user/ftp/pub"),
    maxLoginPerIp = 1,
    writePermission = WritePermissionDefault,
    enabled = false
  )
}
