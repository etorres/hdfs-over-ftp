package es.eriktorr.katas

import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec

class FtpUsersSpec extends UnitSpec {
  private[this] val applicationContext = loadApplicationContext

  "ftp users" should "list all usernames" in {
    val ftpUsers = new FtpUsers(applicationContext)
    ftpUsers.getAllUserNames shouldBe Array("operator")
  }
}
