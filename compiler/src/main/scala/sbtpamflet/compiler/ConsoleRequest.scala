package sbtpamflet
package compiler

case class ConsoleRequest(input: List[String], isPaste: Boolean) {
  override def toString: String =
    {
      import ConsoleRequest._
      val startBlurb = s"""// Entering paste mode (ctrl-D to finish)"""
      val endBlurb = s"""// Exiting paste mode, now interpreting."""
      val inputStr = input.mkString("\n")
      if (isPaste) s"$promptPaste\n$startBlurb\n$inputStr\n\n$endBlurb\n"
      else s"$prompt $inputStr"
    }
}

object ConsoleRequest {
  private[compiler] val prompt = "scala>"
  private[compiler] val promptPaste = s"""$prompt :paste"""

  def parse(value: String): List[ConsoleRequest] =
    {
      val lines = value.lines.toList
      val linesWithPrompt = lines map { line =>
        val isPrompt = line startsWith prompt
        val isPaste = (line == promptPaste)
        val trimmed = if (isPaste) ""
                      else if (isPrompt) (line drop prompt.length).trim
                      else line
        (trimmed, isPrompt, isPaste)
      }
      if (!linesWithPrompt.isEmpty && !linesWithPrompt.head._2) {
        sys.error(s"""Invalid line '${linesWithPrompt.head._1}'. A repl block must start with "scala>".""")
      }
      def splitByPrompt(rest: List[(String, Boolean, Boolean)]): List[ConsoleRequest] =
        rest match {
          case x :: xs =>
            val ys = xs takeWhile { !_._2 } map { _._1 }
            ConsoleRequest((if (x._1 == "") Nil
             else List(x._1)) ::: ys, x._3) :: splitByPrompt(xs dropWhile { !_._2 })
          case Nil => Nil
        }
      splitByPrompt(linesWithPrompt)
    }

}
