package sbtpamflet

import pamflet.FencePlugin
import com.tristanhunt.knockoff._
import java.io.File
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.parsing.input.Position
import sbt.{ Compiler, IO, Logger, LoggerReporter }
import sbt.Path._
import sbt.Logger.o2m
import xsbti.compile.{ CompileOrder }
import xsbti.{ Position => SbtPosition }
import sbtpamflet.compiler.ConsoleSessionOptions

trait CompileFence extends FencePlugin { self =>
  override def toString: String = "CompileFence"
  def customClasspath: List[File] = Nil
  def scalacOptions: Seq[String]
  def javacOptions: Seq[String]
  def maxErrors: Int
  def sourcePositionMappers: Seq[SbtPosition => Option[SbtPosition]]
  def compileOrder: CompileOrder
  def compilers: Compiler.Compilers
  def compileIncSetup: Compiler.IncSetup
  def log: Logger

  override def onBeginLanguage(): Unit = ()

  override def isDefinedAt(language: Option[String]): Boolean =
    language match {
      case Some(x) if (x.trim == "compile") || (x startsWith "compile:") => true
      case _ => false
    }

  override def toBlock(language: Option[String], content: String, position: Position,
      list: ListBuffer[Block]): Block =
    language match {
      case Some(tag) if tag startsWith "compile" =>
        compileOnGlobal(content, tag, position)
        CompileCodeBlock(Text(content), position)
      case _ => sys.error(s"Unexpected language: $language")
    }

  override def blockToXHTML =
    {
      case CompileCodeBlock(content, position) => scalcCodeBlockToXHTML(content, position)
    }

  def scalcCodeBlockToXHTML(content: Text, position: Position): xml.Node =
    <pre><code class="prettyprint lang-scala">{
      content match { case Text(req) => req }
    }</code></pre>

  def compileOnGlobal(content: String, tag: String, position: Position): Unit =
    {
      IO.withTemporaryDirectory { tempDir =>
        val randomName = "pamflet_" + java.lang.Integer.toHexString(content.##) + ".scala"
        val sourceFile = tempDir / "src" / randomName
        IO.write(sourceFile, content)
        val sources = List(sourceFile)
        val classDirectory = tempDir / "classes"
        val input = Compiler.inputs(customClasspath, sources, classDirectory,
          scalacOptions, javacOptions, maxErrors, sourcePositionMappers,
          compileOrder)(compilers, compileIncSetup, log)
        val options = ConsoleSessionOptions.parse(tag)
        val reporter = new LoggerReporter(maxErrors, log, { p: SbtPosition =>
          new SbtPosition {
            def line = p.line
            def lineContent = p.lineContent
            def offset = p.offset
            def pointer = p.pointer
            def pointerSpace = p.pointerSpace
            // this reporter removes source positions
            def sourcePath = o2m(None)
            def sourceFile = o2m(None)
          }
        })
        try {
          Compiler(input, log, reporter)
        } catch {
          case e: Throwable if options.allowErrors => ()
        }
      }
    }
}

case class CompileCodeBlock(intpu: Text, position: Position) extends Block
