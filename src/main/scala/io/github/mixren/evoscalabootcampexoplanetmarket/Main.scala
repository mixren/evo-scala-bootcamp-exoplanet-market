package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.kernel.Ref
import cats.effect.{ExitCode, IO, IOApp}
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.db.{DbFlywayMigrator, DbTransactor}

import scala.concurrent.duration.DurationInt

/**
 * Exoplanet Market server
 */
object Main extends IOApp {

  val dbMigrator = new DbFlywayMigrator[IO]

  def run(args: List[String]): IO[ExitCode] = {
    DbTransactor.pooled[IO].use { implicit xa =>
      for {
        _                   <- dbMigrator.migrate()
        reservedExoplanets  <- Ref.of[IO, MapReservations](Map.empty)
        cleaner             = new ReservationCleaner[IO](reservedExoplanets)
        _                   <- cleaner.delayedCleaning(10.seconds).foreverM.start
        _                   <- ExoplanetmarketServer.stream[IO](reservedExoplanets).compile.drain.as(ExitCode.Success)
      } yield ExitCode.Success
    }
  }
}

/*
P.S.
In case of this error:
  java.net.BindException: Address already in use: bind
Use this in cmd (for Windows) to find the task holding the port 8080 (last time the task was java.exe):
  netstat -ano | findstr :8080
And kill it by (5160 is the PID found in the previous step):
  taskkill /pid 5160 /f
 */

