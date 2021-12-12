package io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet

import cats.effect.Async
import cats.implicits.catsSyntaxApplicativeError
import doobie.hikari.HikariTransactor
import doobie.implicits._


class ExoplanetsRepository[F[_]: Async](implicit xa: HikariTransactor[F]) {
  //def insertExoplanets(exps: List[Exoplanet]): Unit = {}

  def fetchAllExoplanets: F[Either[Throwable, List[Exoplanet]]] = {
    ExoplanetsDbQueries.fetchAllExoplanets.transact(xa).attempt
  }

}
