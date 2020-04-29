package es.eriktorr.ftp.usermanagement.authorization

import es.eriktorr.ftp.unitspec.UnitSpec

class IntHashCodeSpec extends UnitSpec {
  "Hash code" should "compute its value from a sequence of integer numbers" in {
    IntHashCode.hashCode(Seq(11, 13, 6)) shouldBe 40771
  }
}
