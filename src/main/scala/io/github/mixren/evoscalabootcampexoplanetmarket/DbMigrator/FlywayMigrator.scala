package io.github.mixren.evoscalabootcampexoplanetmarket.DbMigrator

import cats.effect.{Async, Sync}
import io.github.mixren.evoscalabootcampexoplanetmarket.DbConfig._
import org.flywaydb.core.Flyway

class FlywayMigrator[F[_]: Async] {
  val flyway: Flyway = Flyway
    .configure()
    .dataSource(dbUrl, dbUser, dbPwd)
    .locations(dbMigrationLocation)
    .load()

  def migrate(): F[Int] = Sync[F].delay(flyway.migrate())

}