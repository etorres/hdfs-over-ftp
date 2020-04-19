package es.eriktorr.katas

import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec
import org.apache.ftpserver.usermanager.{AnonymousAuthentication, UsernamePasswordAuthentication}

class FtpUsersSpec extends UnitSpec {
  private[this] val applicationContext = loadApplicationContext
  private[this] val ftpUsers = new FtpUsers(applicationContext)

  private[this] val operatorFtpUser: FtpUser = FtpUser(
    name = "operator",
    password = "62370436:28142C8301BFAC1959EAEA30991958AD"
  )

  private[this] val anonymousFtpUser: FtpUser = FtpUser(
    name = "anonymous",
    password = "",
    maxLoginPerIp = 1,
    enabled = false
  )

  "ftp users" should "find a user by her name" in {
    ftpUsers.getUserByName("operator") shouldBe operatorFtpUser
  }

  "ftp users" should "list all usernames" in {
    ftpUsers.getAllUserNames shouldBe Array("operator", "anonymous")
  }

  "ftp users" should "identify a nonexistent user" in {
    ftpUsers.doesExist("admin") shouldBe false
  }

  "ftp users" should "identify a existent user" in {
    ftpUsers.doesExist("operator") shouldBe true
  }

  "ftp users" should "authenticate a user by its username and password" in {
    ftpUsers.authenticate(new UsernamePasswordAuthentication("operator", "s3C4e7")) shouldBe operatorFtpUser
  }

  "ftp users" should "authenticate an anonymous access" in {
    ftpUsers.authenticate(new AnonymousAuthentication()) shouldBe anonymousFtpUser
  }

  "ftp users" should "reject invalid username and password" in {
    ftpUsers.authenticate(new UsernamePasswordAuthentication("operator", "fake")) shouldBe ForbiddenUser
  }
}
