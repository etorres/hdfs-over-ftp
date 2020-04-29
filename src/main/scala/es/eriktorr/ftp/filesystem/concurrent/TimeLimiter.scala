package es.eriktorr.ftp.filesystem.concurrent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}
import scala.util.Try

trait TimeLimiter {
  def runWithTimeout[A, B](task: A => B, atMost: FiniteDuration): A => Try[B] =
    (input: A) =>
      Try {
        Await.result(Future {
          task.apply(input)
        }, atMost)
      }
}
