package sbtpamflet

sealed trait PfOnError {}
object PfOnError {
  case object Halt extends PfOnError
  case object PrintError extends PfOnError
}
