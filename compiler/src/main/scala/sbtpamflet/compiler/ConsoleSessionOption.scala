package sbtpamflet
package compiler

case class ConsoleSessionOption(fresh: Boolean)
object ConsoleSessionOption {
  val NewOption = "new"
  def parse(value: String): ConsoleSessionOption =
    {
      val xs = value.split(":").toList map {_.trim}
      val fresh = xs contains NewOption
      ConsoleSessionOption(fresh)
    }
}
