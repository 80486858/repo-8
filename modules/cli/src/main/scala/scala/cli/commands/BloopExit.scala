package scala.cli.commands

import caseapp.*

import scala.build.Os
import scala.build.blooprifle.{BloopRifle, BloopRifleConfig}
import scala.cli.CurrentParams
import scala.cli.commands.util.CommonOps.*
import scala.cli.commands.util.SharedCompilationServerOptionsUtil.*

object BloopExit extends ScalaCommand[BloopExitOptions] {
  override def hidden       = true
  override def isRestricted = true
  override def names: List[List[String]] = List(
    List("bloop", "exit")
  )

  private def mkBloopRifleConfig(opts: BloopExitOptions): BloopRifleConfig = {
    import opts.*
    compilationServer.bloopRifleConfig(
      logging.logger,
      coursier.coursierCache(logging.logger.coursierLogger("Downloading Bloop")),
      logging.verbosity,
      "java", // shouldn't be used…
      directories.directories
    )
  }

  override def loggingOptions(options: BloopExitOptions): Option[LoggingOptions] =
    Some(options.logging)

  override def runCommand(options: BloopExitOptions, args: RemainingArgs): Unit = {
    val bloopRifleConfig = mkBloopRifleConfig(options)
    val logger           = options.logging.logger

    val isRunning = BloopRifle.check(bloopRifleConfig, logger.bloopRifleLogger)

    if (isRunning) {
      val ret = BloopRifle.exit(bloopRifleConfig, Os.pwd.toNIO, logger.bloopRifleLogger)
      logger.debug(s"Bloop exit returned code $ret")
      if (ret == 0)
        logger.message("Stopped Bloop server.")
      else {
        if (options.logging.verbosity >= 0)
          System.err.println(s"Error running bloop exit command (return code $ret)")
        sys.exit(1)
      }
    }
    else
      logger.message("No running Bloop server found.")
  }
}
