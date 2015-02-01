package xsbtpamflet

import scala.tools.nsc.interpreter.{ Results => IR }
import xsbtpamfleti.ConsoleResult

object ConsoleHelper {
  implicit def toConsoleResult(ir: IR.Result): ConsoleResult =
    ir match {
      case IR.Success    => ConsoleResult.Success
      case IR.Incomplete => ConsoleResult.Incomplete
      case IR.Error      => ConsoleResult.Error
    }
}
