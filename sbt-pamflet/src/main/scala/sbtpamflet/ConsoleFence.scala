package sbtpamflet

import pamflet.FencePlugin

import java.io.File
import com.tristanhunt.knockoff._
import scala.util.parsing.input.Position
import scala.collection.mutable.ListBuffer
import sbt.{ IO, Logger, ClasspathOptions => SbtClasspathOptions }
import xsbti.compile.{ ScalaInstance, ClasspathOptions }
import xsbtpamfleti.ConsoleResponse
import xsbtpamfleti.ConsoleResult.{ Success, Incomplete, Error }
import sbtpamflet.compiler.{ CompilerBridgeInstance, ConsoleSession, ConsoleRequest }
 
trait ConsoleFence extends FencePlugin { self =>
  override def toString: String = "ConsoleFence"
  def compilerBridge: CompilerBridgeInstance
  def customClasspath: List[File] = Nil
  def scalaInstance: ScalaInstance
  def classpathOptions: ClasspathOptions = SbtClasspathOptions.auto
  def errorHandling: PfOnError
  def scalacOptions: Seq[String]
  def log: Logger

  override def onBeginLanguage(): Unit = {
    ConsoleSession.clear()
  }

  override def isDefinedAt(language: Option[String]): Boolean =
    language match {
      case Some(x) if x startsWith "console" => true
      case _ => false
    }

  def eval(content: String, tag: String, position: Position): Block =
    {
      val requests = ConsoleRequest.parse(content)
      val session = ConsoleSession(tag, compilerBridge, customClasspath, scalaInstance, classpathOptions,
        scalacOptions, log)
      val responses = requests map { session.interpret }
      val items = (requests zip responses) map {
        case (req, res) =>
          res.result match {
            case Success | Incomplete =>
              Text(req.toString) -> Text(res.output)
            case Error if errorHandling == PfOnError.PrintError =>
              Text(req.toString) -> Text(res.output)
            case Error =>
              sys.error(s"Error in line: scala> $req\n" + res)
          }
      }
      ConsoleCodeBlock(Text(content), items, position)
    }

  override def toBlock(language: Option[String], content: String, position: Position,
      list: ListBuffer[Block]): Block =
    language match {
      case Some(tag) if tag startsWith "console" =>
        eval(content, tag, position)
      case _ => sys.error(s"Unexpected language: $language")
    }

  override def blockToXHTML =
    {
      case ConsoleCodeBlock(content, items, position) => consoleCodeBlockToXTHML(content, items, position)
    }

  def consoleCodeBlockToXTHML(content: Text, items: List[(Text, Text)], position: Position): xml.Node =
    <pre><code class="prettyprint lang-scala">{
      (items map { case (Text(req), Text(res)) => req + "\n" + res }).mkString("\n")
    }</code></pre>
}

case class ConsoleCodeBlock(input: Text, items: List[(Text, Text)], position: Position) extends Block
