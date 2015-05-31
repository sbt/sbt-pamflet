package sbtpamflet
package compiler

case class ConsoleSessionOption(reset: Boolean)
object ConsoleSessionOption {
  val NewOption = "new"
  def parse(value: String): ConsoleSessionOption =
    {
      val xs = value.split(":").toList map {_.trim}
      val reset = xs contains NewOption
      ConsoleSessionOption(reset)
    }
}
