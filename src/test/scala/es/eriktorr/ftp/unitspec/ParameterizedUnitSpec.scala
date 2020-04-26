package es.eriktorr.ftp.unitspec

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

abstract class ParameterizedUnitSpec
    extends AnyFlatSpec
    with Matchers
    with TableDrivenPropertyChecks
