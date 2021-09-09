package scala.cli.commands

import caseapp._

import scala.build.blooprifle.BloopRifleConfig

// format: off
final case class BloopExitOptions(
  @Recurse
    logging: LoggingOptions = LoggingOptions(),
  @Recurse
    compilationServer: SharedCompilationServerOptions = SharedCompilationServerOptions(),
  @Recurse
    directories: SharedDirectoriesOptions = SharedDirectoriesOptions()
) {
  // format: on

  def bloopRifleConfig(): BloopRifleConfig =
    compilationServer.bloopRifleConfig(
      logging.logger,
      logging.verbosity,
      "java", // shouldn't be used…
      directories.directories
    )

}
