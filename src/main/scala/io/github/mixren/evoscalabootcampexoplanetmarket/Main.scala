package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.kernel.Ref
import cats.effect.{ExitCode, IO, IOApp}
import io.github.mixren.evoscalabootcampexoplanetmarket.db.{DbTransactor, DbFlywayMigrator}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import scala.concurrent.duration.Deadline


object Main extends IOApp {

  val dbMigrator = new DbFlywayMigrator[IO]

  def run(args: List[String]): IO[ExitCode] = {
    DbTransactor.pooled[IO].use { implicit xa =>
      for {
        _                   <- dbMigrator.migrate()
        reservedExoplanets  <- Ref.of[IO, Map[ExoplanetOfficialName, (UserName, Deadline)]](Map.empty)
        _                   <- ExoplanetmarketServer.stream[IO](reservedExoplanets).compile.drain.as(ExitCode.Success)
      } yield ExitCode.Success
      //ExoplanetmarketServer.stream[IO].compile.drain.as(ExitCode.Success)
    }
  }
}
