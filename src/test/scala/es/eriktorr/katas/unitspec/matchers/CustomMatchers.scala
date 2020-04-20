package es.eriktorr.katas.unitspec.matchers

import es.eriktorr.katas.usermanagement.FtpUser
import org.apache.ftpserver.ftplet.User
import org.apache.ftpserver.usermanager.impl.{
  ConcurrentLoginPermission,
  ConcurrentLoginRequest,
  TransferRatePermission,
  TransferRateRequest,
  WritePermission,
  WriteRequest
}

import scala.util.{Failure, Success, Try}
//import org.apache.ftpserver.usermanager.impl._
import org.scalatest.matchers.{MatchResult, Matcher}

sealed case class UseCase[T](name: String, a: T, b: T)

trait CustomMatchers {
  class FtpUserIsJavaBeanMatcher(expected: User) extends Matcher[FtpUser] {
    override def apply(left: FtpUser): MatchResult = {
      val loginRequest = new ConcurrentLoginRequest(1, 1)
      val transferRateRequest = new TransferRateRequest()
      val writeRequest = new WriteRequest()

      def asString[T](x: T): String = Try(x.toString) match {
        case Success(value) => value
        case Failure(_) => "<<unknown>>"
      }

      val accumulatedErrors = Seq(
        UseCase("name", left.getName, expected.getName),
        UseCase("password", left.getPassword, expected.getPassword),
        UseCase("authorities", left.getAuthorities, expected.getAuthorities),
        UseCase(
          "filter_login_permission",
          left.getAuthorities(classOf[ConcurrentLoginPermission]),
          expected.getAuthorities(
            classOf[ConcurrentLoginPermission]
          )
        ),
        UseCase(
          "filter_transfer_rate_permissions",
          left.getAuthorities(classOf[TransferRatePermission]),
          expected.getAuthorities(
            classOf[TransferRatePermission]
          )
        ),
        UseCase(
          "filter_write_permission",
          left.getAuthorities(classOf[WritePermission]),
          expected.getAuthorities(
            classOf[WritePermission]
          )
        ),
        UseCase("maxIdleTime", left.getMaxIdleTime, expected.getMaxIdleTime),
        UseCase("enabled", left.getEnabled, expected.getEnabled),
        UseCase("homeDirectory", left.getHomeDirectory, expected.getHomeDirectory),
        UseCase(
          "authorize_login_request",
          left.authorize(loginRequest),
          expected.authorize(loginRequest)
        ),
        UseCase(
          "authorize_transfer_request",
          left.authorize(transferRateRequest),
          expected.authorize(transferRateRequest)
        ),
        UseCase(
          "authorize_write_request",
          left.authorize(writeRequest),
          expected.authorize(writeRequest)
        )
      ).flatMap { useCase =>
        if (useCase.a == useCase.b) None
        else Some((useCase.name, asString(useCase.a), asString(useCase.b)))
      }
      MatchResult(
        accumulatedErrors.isEmpty,
        s"""FtpUser ${left.toString} is not "${classOf[User].toString}" with errors "${accumulatedErrors.toString}"""",
        s"""FtpUser ${left.toString} is "${classOf[User].toString}""""
      )
    }
  }
  def haveMatchingAttributesWith(expected: User) = new FtpUserIsJavaBeanMatcher(expected)
}

object CustomMatchers extends CustomMatchers
