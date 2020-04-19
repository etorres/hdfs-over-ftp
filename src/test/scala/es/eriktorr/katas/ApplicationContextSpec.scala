package es.eriktorr.katas

import es.eriktorr.katas.unitspec.UnitSpec

class ApplicationContextSpec extends UnitSpec {
  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.loadApplicationContext shouldBe ApplicationContext(
      Seq(
        FtpUser(name = "operator", password = "62370436:28142C8301BFAC1959EAEA30991958AD"),
        FtpUser(name = "anonymous", password = "", maxLoginPerIp = 1, enabled = false)
      )
    )
  }
}
