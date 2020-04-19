package es.eriktorr.katas

import es.eriktorr.katas.authorities.{ConcurrentLoginAuthority, TransferRateAuthority}
import es.eriktorr.katas.matchers.CustomMatchers._
import org.apache.ftpserver.usermanager.impl.{BaseUser, WritePermission}
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
          classOf[ConcurrentLoginAuthority],
          classOf[TransferRateAuthority]
        )
      ),
      (
        FtpUser("read-only user", "123"),
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
    user.setPassword("123")
    user.setAuthorities(
      Seq(
        new ConcurrentLoginAuthority(4, 2),
        new TransferRateAuthority(0, 0)
      ).asJava
    )
    user.setMaxIdleTime(300)
    user.setHomeDirectory("/home/user")
    FtpUser("user", "123") should haveMatchingAttributesWith(user)
  }
}
