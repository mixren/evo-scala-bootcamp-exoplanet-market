package io.github.mixren.evoscalabootcampexoplanetmarket.db

object DbConfig {
  val dbDriverName = "org.sqlite.JDBC"
  val dbUrl = "jdbc:sqlite:src/sql/exoplanetsmarket.db"
  val dbUser = ""
  val dbPwd = ""

  val dbThreadSize = 10

  val dbMigrationLocation = "classpath:db/migration"
}
