package es.eriktorr.katas

import es.eriktorr.katas.matchers.CustomMatchers._
import org.apache.ftpserver.usermanager.impl.{
  BaseUser,
  ConcurrentLoginPermission,
  TransferRatePermission,
  WritePermission
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.jdk.CollectionConverters._

class FtpUserSpec extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {
  "users" should "have least authority" in {
    val users = Table(
      ("user", "authorities"),
      (
        FtpUser("admin", "123", writePermission = true),
        Seq(
          classOf[WritePermission],
          classOf[ConcurrentLoginPermission],
          classOf[TransferRatePermission]
        )
      ),
      (
        FtpUser("read-only user", "123"),
        Seq(
          classOf[ConcurrentLoginPermission],
          classOf[TransferRatePermission]
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
    user.setPassword("123")
    FtpUser("user", "123") should haveMatchingAttributesWith(user)
  }
}
