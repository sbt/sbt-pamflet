package xsbtpamflet

object Message {
  def apply[T](s: => T) = new xsbtpamfleti.F0[T] { def apply() = s }
}
