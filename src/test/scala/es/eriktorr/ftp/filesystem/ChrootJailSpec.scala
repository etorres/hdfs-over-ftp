package es.eriktorr.ftp.filesystem

import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider

class ChrootJailSpec extends UnitSpec with DataProvider {
  "Chroot jail" should "deny access to files or directories under chroot directory" in {
    ChrootJailFake.isAllowed(
      rootDir = "/user/root/input",
      path = "/user/root/input1/file.txt"
    ) shouldBe false
  }

  it should "allow access to files or directories over chroot directory" in {
    ChrootJailFake.isAllowed(
      rootDir = "/user/root",
      path = "/user/root/input"
    ) shouldBe true
  }

  it should "allow access to root directory" in {
    ChrootJailFake.isAllowed(
      rootDir = "/user/root",
      path = "/user/root"
    ) shouldBe true
  }

  object ChrootJailFake extends ChrootJail
}
