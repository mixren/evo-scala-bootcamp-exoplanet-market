package io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet

import doobie.implicits._
import doobie.util.update.Update

object ExoplanetsDbQueries {
  // private val table = "exoplanets"  <- TODO how to insert value into sql strings??


  val createTableExoplanetsSql: doobie.ConnectionIO[Int] =
    sql"""
        CREATE TABLE IF NOT EXISTS exoplanets (
          id INTEGER NOT NULL,
          official_name TEXT PRIMARY KEY,
          mass_jupiter DOUBLE NULL,
          radius_jupiter DOUBLE NULL,
          distance_pc DOUBLE NULL,
          ra DOUBLE NULL,
          dec DOUBLE NULL,
          discovery_year INTEGER NULL
        )
    """.update.run

  val dropTableExoplanets: doobie.ConnectionIO[Int] =
      sql"""
        DROP TABLE IF EXISTS exoplanets
      """.update.run

  /*def insertExoplanet(exp: Exoplanet): Update0 =
    sql"""
          INSERT INTO exoplanets(
            id, official_name, mass_jupiter, radius_jupiter,
            distance_pc, ra, dec, discoveryYear)
            values (${exp.id}, ${exp.officialName}, ${exp.mass}, ${exp.radius},
            ${exp.distance}, ${exp.ra}, ${exp.dec}, ${exp.discoveryYear}
          )
    """.update*/

  def insertExoplanets(exps: List[Exoplanet]): doobie.ConnectionIO[Int] = {
    val sql = """
                |INSERT INTO exoplanets(
                |  id, official_name, mass_jupiter, radius_jupiter,
                |  distance_pc, ra, dec, discovery_year)
                |  values (?, ?, ?, ?, ?, ?, ?, ?)
                |  """.stripMargin
    Update[Exoplanet](sql).updateMany(exps)
  }

  def fetchAllExoplanets: doobie.ConnectionIO[List[Exoplanet]] = {
    sql"""SELECT id, official_name, mass_jupiter, radius_jupiter,
          distance_pc, ra, dec, discovery_year FROM exoplanets
    """.query[Exoplanet].to[List]
  }

  def fetch5Exoplanets(): doobie.ConnectionIO[List[Exoplanet]] = {
    sql"""SELECT * FROM exoplanets
    """.query[Exoplanet].stream.take(5).compile.toList
  }

}
