package es.eriktorr.ftp.usermanagement

import es.eriktorr.ftp.ApplicationContextLoader.defaultApplicationContext
import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.ftpserver.ftplet.AuthenticationFailedException
import org.apache.ftpserver.usermanager.{AnonymousAuthentication, UsernamePasswordAuthentication}

class FtpUsersSpec extends UnitSpec with DataProvider {
  private[this] val applicationContext = defaultApplicationContext
  private[this] val ftpUsers = new FtpUsers(applicationContext.ftpServerConfig.ftpUsers)

  "Ftp users" should "find a user by her name" in {
    ftpUsers.getUserByName("root") shouldBe RootFtpUser
  }

  it should "list all usernames" in {
    ftpUsers.getAllUserNames shouldBe Array("root", "anonymous")
  }

  it should "identify a nonexistent user" in {
    ftpUsers.doesExist("admin") shouldBe false
  }

  it should "identify a existent user" in {
    ftpUsers.doesExist("root") shouldBe true
  }

  it should "authenticate a user by its username and password" in {
    ftpUsers.authenticate(new UsernamePasswordAuthentication("root", "s3C4e7")) shouldBe
      RootFtpUser
  }

  it should "authenticate an anonymous access" in {
    ftpUsers.authenticate(new AnonymousAuthentication()) shouldBe AnonymousFtpUser
  }

  it should "reject invalid username and password" in {
    the[AuthenticationFailedException] thrownBy ftpUsers.authenticate(
      new UsernamePasswordAuthentication("root", "fake")
    ) should have message "Authentication failed"
  }
}
