package es.eriktorr.ftp.usermanagement

import es.eriktorr.ftp.usermanagement.authorization.WriteAuthorizationMaker.WritePermissionDefault
import es.eriktorr.ftp.usermanagement.authorization.{
  ConcurrentLoginAuthority,
  TransferRateAuthority
}
import es.eriktorr.ftp.unitspec.matchers.CustomMatchers._
import org.apache.ftpserver.usermanager.impl.{BaseUser, WritePermission}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.jdk.CollectionConverters._

class FtpUserSpec extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {
  private[this] val Password = "123"

  private[this] def aFtpUser(name: String, writePermission: Boolean) =
    FtpUser(
      name = name,
      password = Password,
      groups = Seq.empty,
      maxLoginPerIp = None,
      maxLoginNumber = None,
      maxIdleTime = None,
      maxDownloadRate = None,
      maxUploadRate = None,
      writePermission = Some(writePermission),
      enabled = None
    )

  "users" should "have least authority" in {
    val users = Table(
      ("user", "authorities"),
      (
        aFtpUser(name = "admin", writePermission = true),
        Seq(
          classOf[WritePermission],
          classOf[ConcurrentLoginAuthority],
          classOf[TransferRateAuthority]
        )
      ),
      (
        aFtpUser(name = "read-only user", writePermission = WritePermissionDefault),
        Seq(
          classOf[ConcurrentLoginAuthority],
          classOf[TransferRateAuthority]
        )
      )
    )

    forAll(users)((user: FtpUser, authorities: Seq[_]) =>
      user.getAuthorities.asScala.map(_.getClass) shouldBe authorities
    )
  }

  "user" should "provide access to attributes via getter methods" in {
    val user = new BaseUser()
    user.setName("user")
    user.setPassword(Password)
    user.setAuthorities(
      Seq(
        new ConcurrentLoginAuthority(4, 2),
        new TransferRateAuthority(0, 0)
      ).asJava
    )
    user.setMaxIdleTime(300)
    user.setHomeDirectory("/home/user")
    aFtpUser(name = "user", writePermission = WritePermissionDefault) should
      haveMatchingAttributesWith(user)
  }
}
