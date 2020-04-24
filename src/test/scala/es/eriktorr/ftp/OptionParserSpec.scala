package es.eriktorr.ftp

import es.eriktorr.ftp.unitspec.UnitSpec

class OptionParserSpec extends UnitSpec {
  "option parser" should "parse command-line arguments" in {
    val options = OptionParserFake.optionsFrom(Array("-config", "application.conf", "123"))
    options shouldBe Map(Symbol("config") -> "application.conf")
  }

  object OptionParserFake extends OptionParser
}
