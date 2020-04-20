package es.eriktorr.katas

import es.eriktorr.katas.unitspec.{DataProvider, UnitSpec}

class ApplicationContextSpec extends UnitSpec with DataProvider {
  "Application context" should "be loaded from default location" in {
    ApplicationContextLoader.loadApplicationContext shouldBe ApplicationContext(
      Seq(OperatorFtpUser, AnonymousFtpUser)
    )
  }
}
