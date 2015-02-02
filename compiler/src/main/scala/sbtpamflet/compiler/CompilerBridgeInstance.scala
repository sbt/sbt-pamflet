package sbtpamflet
package compiler

import java.io.File

final class CompilerBridgeInstance(val version: String, val compilerBridgeFile: File) {
}

object CompilerBridgeInstance {
  def apply(version: String, compilerBridgeFile: File): CompilerBridgeInstance =
    new CompilerBridgeInstance(version, compilerBridgeFile)
}
