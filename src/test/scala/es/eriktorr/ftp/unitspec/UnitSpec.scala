package es.eriktorr.ftp.unitspec

import org.scalatest.TryValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

abstract class UnitSpec extends AnyFlatSpec with Matchers with TryValues {
  val pathTo: String => String = {
    getClass.getClassLoader.getResource(_).getPath
  }
}
