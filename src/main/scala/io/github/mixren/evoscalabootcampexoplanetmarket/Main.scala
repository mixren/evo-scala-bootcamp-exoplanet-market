package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.{ExitCode, IO, IOApp}


object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    DbTransactor.pooled[IO].use { implicit xa =>
      ExoplanetmarketServer.stream[IO].compile.drain.as(ExitCode.Success)
    }
  }
}
