package sbtpamflet
package compiler

import java.net.{ URL, URLClassLoader }
import sbt.classpath.DualLoader
import xsbtpamfleti.{ Logger, ConsoleInterface, ConsoleFactory }

class Console(val scalaInstance: xsbti.compile.ScalaInstance, val compilerBridge: CompilerBridgeInstance) {
  val factoryClassName = "xsbtpamflet.ConsoleFactory"
  lazy val factory: AnyRef = newInstance(factoryClassName)

  // ConsoleInterface createConsole(String[] args, String bootClasspathString,
  //   String classpathString, String initialCommands, String cleanupCommands,
  //   ClassLoader loader, String[] bindNames, Object[] bindValues, Logger log);
  def console(scalacOptions: Seq[String], bootClasspathString: String, classpathString: String,
    initialCommands: String, cleanupCommands: String, bindNames: Array[String], bindValues: Array[AnyRef],
    log: Logger): ConsoleInterface =
    {
      val l: ClassLoader = null
      (call(factory, factoryClassName)("createConsole")
        (classOf[Array[String]], classOf[String], classOf[String], classOf[String], classOf[String],
          classOf[ClassLoader], classOf[Array[String]], classOf[Array[AnyRef]], classOf[Logger])
        (scalacOptions.toArray, bootClasspathString, classpathString, initialCommands, cleanupCommands,
          l, bindNames, bindValues, log)).asInstanceOf[ConsoleInterface]
    }

  private[this] def newInstance(interfaceClassName: String): AnyRef =
    {
      val interfaceClass = getInterfaceClass(interfaceClassName)
      interfaceClass.newInstance.asInstanceOf[AnyRef]      
    }
  private[this] def call(instance: AnyRef, interfaceClassName: String)(methodName: String)(argTypes: Class[_]*)(args: AnyRef*): AnyRef =
    {
      val interfaceClass = getInterfaceClass(interfaceClassName)
      val method = interfaceClass.getMethod(methodName, argTypes: _*)
      try { method.invoke(instance, args: _*) }
      catch {
        case e: java.lang.reflect.InvocationTargetException =>
          throw e
      }
    }
  private[this] def getInterfaceClass(name: String) = Class.forName(name, true, loader)
  private[this] lazy val loader: ClassLoader =
    {
      val interfaceJar = compilerBridge.compilerBridgeFile
      // this goes to scalaInstance.loader for scala classes and the loader of this class for xsbti classes
      val dual = createDualLoader(scalaInstance.loader, getClass.getClassLoader)
      new URLClassLoader(Array(interfaceJar.toURI.toURL), dual)
    }
  private[this] def createDualLoader(scalaLoader: ClassLoader, sbtLoader: ClassLoader): ClassLoader =
    {
      val xsbtiFilter = (name: String) => name.startsWith("xsbti.") || name.startsWith("xsbtpamfleti.")
      val notXsbtiFilter = (name: String) => !xsbtiFilter(name)
      new DualLoader(scalaLoader, notXsbtiFilter, x => true, sbtLoader, xsbtiFilter, x => false)
    }
}