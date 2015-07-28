package sbtpamflet
package compiler

sealed trait ConsoleSessionOption
object ConsoleSessionOption {
  val NewOption = "new"
  val ErrorOption = "error"
  def parse(value: String): ConsoleSessionOption =
    value match {
      case NewOption   => Reset
      case ErrorOption => AllowErrors
      case _         => sys.error("Invalid option: " + value)
    }

  case object Reset extends ConsoleSessionOption
  case object AllowErrors extends ConsoleSessionOption
}
case class ConsoleSessionOptions(options: List[ConsoleSessionOption]) {
  import ConsoleSessionOption._
  def reset: Boolean = options contains Reset
  def allowErrors: Boolean = options contains AllowErrors
}

object ConsoleSessionOptions {
  def parse(value: String): ConsoleSessionOptions =
    value.indexOf(":") match {
      case n if n < 0 => ConsoleSessionOptions(Nil)
      case n =>
        val xs = value.substring(n + 1).split(",").toList map {_.trim}
        ConsoleSessionOptions(xs map { ConsoleSessionOption.parse })
    }
}
