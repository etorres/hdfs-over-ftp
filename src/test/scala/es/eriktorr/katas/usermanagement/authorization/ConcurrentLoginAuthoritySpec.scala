package es.eriktorr.katas.usermanagement.authorization

import es.eriktorr.katas.unitspec.UnitSpec
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission

class ConcurrentLoginAuthoritySpec extends UnitSpec {
  "Instances with same attributes" should "be equal" in {
    new ConcurrentLoginAuthority(4, 2) shouldBe
      new ConcurrentLoginAuthority(4, 2)
  }

  "Instances with different attributes" should "not be equal" in {
    new ConcurrentLoginAuthority(9, 3) should not be
      new ConcurrentLoginAuthority(9, 2)
  }

  "Different classes" should "not be equal" in {
    new ConcurrentLoginPermission(4, 2) should not be
      new ConcurrentLoginAuthority(4, 2)
  }
}
