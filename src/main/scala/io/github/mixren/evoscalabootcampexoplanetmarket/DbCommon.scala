package io.github.mixren.evoscalabootcampexoplanetmarket

import doobie.implicits._
import doobie.util.update.Update

object DbCommon {

  val createTableExoplanetsSql: doobie.ConnectionIO[Int] =
    sql"""
        CREATE TABLE IF NOT EXISTS exoplanets (
          id INTEGER NOT NULL,
          official_name STRING PRIMARY KEY,
          mass_jupiter FLOAT NULL,
          radius_jupiter FLOAT NULL,
          distance_pc FLOAT NULL,
          ra FLOAT NULL,
          dec FLOAT NULL,
          discovery_year INTEGER NULL
        )
    """.update.run

  /*private val dropTableExoplanets =
      sql"""
        DROP TABLE IF EXISTS exoplanets
      """.update.run*/

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

  def fetchAllExoplanets(): doobie.ConnectionIO[List[Exoplanet]] = {
    sql"""SELECT id, official_name, mass_jupiter, radius_jupiter,
          distance_pc, ra, dec, discovery_year FROM exoplanets
    """.query[Exoplanet].to[List]
  }

}
