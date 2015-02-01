package xsbtpamflet

object Message {
  def apply[T](s: => T) = new xsbti.F0[T] { def apply() = s }
}
