package pamflet.sbtpamflet

import org.specs2._
import com.tristanhunt.knockoff._

class CompilerSpec extends Specification {
  def is = args(sequential = true) ^ s2"""

  This is a specification to check the REPL block parsing.

  srepl block should
    ${evalAs("srepl", "scala> 1 + 1", "res0: Int = 2")}
    ${evalAs("srepl", "scala> List(1, 2)", "res1: List[Int] = List(1, 2)")}
    ${evalAs("srepl", "scala> List(1,\n2)", "res2: List[Int] = List(1, 2)")}
    ${evalAs("srepl", "scala> import pamflet._", "import pamflet._")}

  srepl:new block should
    ${evalAs("srepl:new", "scala> 1 + 1", "res0: Int = 2")}
    ${evalAs("srepl:new", "scala> List(1, 2)", "res0: List[Int] = List(1, 2)")}
    ${evalAs("srepl:new", "scala> List(1,\n2)", "res0: List[Int] = List(1, 2)")}
    ${evalAs("srepl:new", "scala> import pamflet._", "import pamflet._")}
                                                                """

  def evalAs(tag: String, input: String, expected: String) =eval {
    s"""```$tag
       |$input
       |```
       |""".stripMargin
  } must_== (input, expected) 
  def eval(s: String): (String, String) =
    discount.knockoff(s).toList.headOption match {
      case Some(ReplCodeBlock(_, List((Text(req), Text(res))), _)) => req.trim -> res.trim
      case x => sys.error(s"unexpected value $x")
    }
  lazy val discount = {
    val x = new Discounter with MutableFencedDiscounter
    x.registerFencedChunkProcessor(new ScalaCompilerProcessor {})
    x
  } 
}
