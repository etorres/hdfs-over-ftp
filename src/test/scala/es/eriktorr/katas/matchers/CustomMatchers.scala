package es.eriktorr.katas.matchers

import es.eriktorr.katas.FtpUser
import org.apache.ftpserver.ftplet.User

import scala.util.{Failure, Success, Try}
//import org.apache.ftpserver.usermanager.impl._
import org.scalatest.matchers.{MatchResult, Matcher}

sealed case class UseCase[T](name: String, left: T, expected: T)

trait CustomMatchers {
  class FtpUserIsJavaBeanMatcher(expected: User) extends Matcher[FtpUser] {
    override def apply(left: FtpUser): MatchResult = {
//      val loginRequest = new ConcurrentLoginRequest(1, 1)
//      val transferRateRequest = new TransferRateRequest()
//      val writeRequest = new WriteRequest()

      def asString[T](x: T): String = Try(x.toString) match {
        case Success(value) => value
        case Failure(_) => "<<unknown>>"
      }

      val accumulatedErrors = Seq(
        UseCase("name", left.getName, expected.getName),
        UseCase("password", left.getPassword, expected.getPassword),
        UseCase("authorities", left.getAuthorities, expected.getAuthorities),
        UseCase("maxIdleTime", left.getMaxIdleTime, expected.getMaxIdleTime),
        UseCase("enabled", left.getEnabled, expected.getEnabled),
        UseCase("homeDirectory", left.getHomeDirectory, expected.getHomeDirectory)
      ).flatMap(useCase =>
        if (left == expected) None
        else Some((useCase.name, asString(useCase.left), asString(useCase.expected)))
      )

      /*
      left.getAuthorities(classOf[ConcurrentLoginPermission]) == expected.getAuthorities(
          classOf[ConcurrentLoginPermission]
        )
          && left.getAuthorities(classOf[TransferRatePermission]) == expected.getAuthorities(
            classOf[TransferRatePermission]
          )
          && left.getAuthorities(classOf[WritePermission]) == expected.getAuthorities(
            classOf[WritePermission]
          )
          && left.authorize(loginRequest) == expected.authorize(loginRequest)
          && left.authorize(transferRateRequest) == expected.authorize(transferRateRequest)
          && left.authorize(writeRequest) == expected.authorize(writeRequest)
       */

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
