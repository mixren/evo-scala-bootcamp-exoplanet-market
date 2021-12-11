package io.github.mixren.evoscalabootcampexoplanetmarket

import doobie.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.domain.{User, UserName}


object UserDbQueries {
  private val table = "users"


  val createTableUsersSql: doobie.ConnectionIO[Int] =
    sql"""
        CREATE TABLE IF NOT EXISTS $table (
          id INTEGER NOT NULL AUTO_INCREMENT,
          username TEXT PRIMARY KEY,
          password INTEGER NOT NULL
        )
    """.update.run

  val dropTableUsers: doobie.ConnectionIO[Int] =
    sql"""
        DROP TABLE IF EXISTS $table
      """.update.run

  def insertExoplanet(user: User): doobie.ConnectionIO[Int] =
    sql"""
          INSERT INTO $table(username, password)
            values (${user.userName}, ${user.userPassword})
    """.update.run

  def fetchByName(username: UserName): doobie.ConnectionIO[User] = {
    sql"""SELECT username, password FROM $table WHERE username = '$username'
    """.query[User].unique
  }
}
