package es.eriktorr.katas.usermanagement

import es.eriktorr.katas.ApplicationContextLoader.loadApplicationContext
import es.eriktorr.katas.unitspec.UnitSpec
import es.eriktorr.katas.unitspec.data.DataProvider
import org.apache.ftpserver.usermanager.{AnonymousAuthentication, UsernamePasswordAuthentication}

class FtpUsersSpec extends UnitSpec with DataProvider {
  private[this] val applicationContext = loadApplicationContext
  private[this] val ftpUsers = new FtpUsers(applicationContext.ftpServerConfig.ftpUsers)

  "ftp users" should "find a user by her name" in {
    ftpUsers.getUserByName("operator") shouldBe OperatorFtpUser
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
    ftpUsers.authenticate(new UsernamePasswordAuthentication("operator", "s3C4e7")) shouldBe
      OperatorFtpUser
  }

  "ftp users" should "authenticate an anonymous access" in {
    ftpUsers.authenticate(new AnonymousAuthentication()) shouldBe AnonymousFtpUser
  }

  "ftp users" should "reject invalid username and password" in {
    ftpUsers.authenticate(new UsernamePasswordAuthentication("operator", "fake")) shouldBe ForbiddenUser
  }
}