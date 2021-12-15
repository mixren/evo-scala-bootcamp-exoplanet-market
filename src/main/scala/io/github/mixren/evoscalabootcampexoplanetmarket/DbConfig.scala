package io.github.mixren.evoscalabootcampexoplanetmarket

object DbConfig {
  val dbDriverName = "org.sqlite.JDBC"
  val dbUrl = "jdbc:sqlite:src/main/resources/db/exoplanetsmarket.db"
  val dbUser = ""
  val dbPwd = ""

  val dbThreadSize = 10

  val dbMigrationLocation = "src/main/resources/db/migration"
}
