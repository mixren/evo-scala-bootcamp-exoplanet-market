package io.github.mixren.evoscalabootcampexoplanetmarket.repository

import cats.effect.Async
import cats.implicits.catsSyntaxApplicativeError
import doobie.hikari.HikariTransactor
import io.github.mixren.evoscalabootcampexoplanetmarket.DbQueries
import doobie.implicits._


class ExoplanetsRepository[F[_]: Async](xa: HikariTransactor[F]) {
  //def insertExoplanets(exps: List[Exoplanet]): Unit = {}

  def fetchAllExoplanets = {
    DbQueries.fetchAllExoplanets.transact(xa).attempt
  }

}
