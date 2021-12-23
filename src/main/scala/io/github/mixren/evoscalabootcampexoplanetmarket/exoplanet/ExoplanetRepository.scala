package io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet

import cats.effect.Async
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.update.Update
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{Exoplanet, ExoplanetOfficialName}

trait ExoplanetRepositoryT[F[_]] {
  def insertExoplanets(exps: List[Exoplanet]): F[Int]
  def fetchAllExoplanets: F[List[Exoplanet]]
  def deleteAllExoplanets(): F[Int]
  def exoplanetByName(exoplanetName: ExoplanetOfficialName): F[Option[Exoplanet]]
}

class ExoplanetRepository[F[_]: Async](implicit xa: HikariTransactor[F]) extends ExoplanetRepositoryT[F] {
  override def insertExoplanets(exps: List[Exoplanet]): F[Int] = {
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

  override def fetchAllExoplanets: F[List[Exoplanet]] = {
    sql"""SELECT id, official_name, mass_jupiter, radius_jupiter,
          distance_pc, ra, dec, discovery_year FROM exoplanets
       """
      .query[Exoplanet]
      .to[List]
      .transact(xa)
  }

  override def deleteAllExoplanets(): F[Int] = {
    sql"""DELETE FROM exoplanets""".update.run.transact(xa)
  }

  override def exoplanetByName(exoplanetName: ExoplanetOfficialName): F[Option[Exoplanet]] = {
    sql"""SELECT id, official_name, mass_jupiter, radius_jupiter,
          distance_pc, ra, dec, discovery_year FROM exoplanets
          WHERE official_name = $exoplanetName
       """
      .query[Exoplanet]
      .option
      .transact(xa)
  }

}
