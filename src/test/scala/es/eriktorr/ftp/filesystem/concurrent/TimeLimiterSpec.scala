package es.eriktorr.ftp.filesystem.concurrent

import es.eriktorr.ftp.unitspec.UnitSpec

import scala.concurrent.duration._
import scala.util.Try

class TimeLimiterSpec extends UnitSpec {
  "Task" should "compute the expected result" in {
    val task: Int => Try[String] =
      TimeLimiterFake.runWithTimeout[Int, String](_.toString, 10.second)
    task.apply(100).success.value shouldBe "100"
  }

  it should "fail when is not completed in the expected time" in {
    val task: Int => Try[String] = TimeLimiterFake.runWithTimeout[Int, String](input => {
      Thread.sleep(10000)
      input.toString
    }, 1.millisecond)
    task.apply(100).failure.exception should have message "Future timed out after [1 millisecond]"
  }

  object TimeLimiterFake extends TimeLimiter
}
