package io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet

import cats.effect.Async
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.update.Update
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.Exoplanet

// TODO add ExoplanetHandler for the repository logic?
class ExoplanetRepository[F[_]: Async](implicit xa: HikariTransactor[F]) {
  def insertExoplanets(exps: List[Exoplanet]): F[Int] = {
    val sql = """
                |INSERT INTO exoplanets(
                |  id, official_name, mass_jupiter, radius_jupiter,
                |  distance_pc, ra, dec, discovery_year)
                |  values (?, ?, ?, ?, ?, ?, ?, ?)
                |  """.stripMargin
    Update[Exoplanet](sql)
      .updateMany(exps)
      .transact(xa)
  }

  def fetchAllExoplanets: F[List[Exoplanet]] = {
    sql"""SELECT id, official_name, mass_jupiter, radius_jupiter,
          distance_pc, ra, dec, discovery_year FROM exoplanets
       """
      .query[Exoplanet]
      .to[List]
      .transact(xa)
  }

  def deleteAllExoplanets(): F[Int] = {
    sql"""DELETE FROM exoplanets""".update.run.transact(xa)
  }

  /*def fetchExoplanets(number: Int): doobie.ConnectionIO[List[Exoplanet]] = {
    sql"""SELECT * FROM exoplanets"""
      .query[Exoplanet]
      .stream
      .take(number)
      .compile
      .toList
  }*/

}
