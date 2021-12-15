package io.github.mixren.evoscalabootcampexoplanetmarket.dbMigrator

import cats.effect.{Async, Sync}
import io.github.mixren.evoscalabootcampexoplanetmarket.DbConfig._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

class FlywayMigrator[F[_]: Async] {
  val flyway: Flyway = Flyway
    .configure()
    .dataSource(dbUrl, dbUser, dbPwd)
    .locations(dbMigrationLocation)
    .load()

  def migrate(): F[MigrateResult] = Sync[F].delay(flyway.migrate())

}