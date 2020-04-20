package es.eriktorr.katas

import es.eriktorr.katas.authorities.LoginAuthorizationMaker.{
  MaxLoginNumberDefault,
  MaxLoginPerIpDefault
}
import es.eriktorr.katas.unitspec.UnitSpec

class ApplicationContextSpec extends UnitSpec {
  private[this] val OperatorFtpUser =
    FtpUser(
      name = "operator",
      password = "62370436:28142C8301BFAC1959EAEA30991958AD",
      maxLoginPerIp = Some(MaxLoginPerIpDefault),
      maxLoginNumber = Some(MaxLoginNumberDefault),
      maxIdleTime = Some(-1),
      maxDownloadRate = None,
      maxUploadRate = None,
      writePermission = None,
      enabled = None
    )

  private[this] val AnonymousFtpUser =
    FtpUser(
      name = "anonymous",
      password = "",
      maxLoginPerIp = Some(1),
      maxLoginNumber = None,
      maxIdleTime = None,
      maxDownloadRate = None,
      maxUploadRate = None,
      writePermission = None,
      enabled = Some(false)
    )

  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.loadApplicationContext shouldBe ApplicationContext(
      Seq(OperatorFtpUser, AnonymousFtpUser)
    )
  }
}
