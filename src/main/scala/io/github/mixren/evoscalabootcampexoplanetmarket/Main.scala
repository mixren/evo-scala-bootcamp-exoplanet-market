package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.{ExitCode, IO, IOApp}
import io.github.mixren.evoscalabootcampexoplanetmarket.dbMigrator.FlywayDatabaseMigrator


object Main extends IOApp {

  val dbMigrator = new FlywayDatabaseMigrator[IO]

  def run(args: List[String]): IO[ExitCode] = {
    DbTransactor.pooled[IO].use { implicit xa =>
      for {
        _ <- dbMigrator.migrate()
        _ <- ExoplanetmarketServer.stream[IO].compile.drain.as(ExitCode.Success)
      } yield ExitCode.Success
      //ExoplanetmarketServer.stream[IO].compile.drain.as(ExitCode.Success)
    }
  }
}
