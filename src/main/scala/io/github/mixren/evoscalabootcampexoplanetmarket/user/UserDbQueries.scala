package io.github.mixren.evoscalabootcampexoplanetmarket.user

import doobie.implicits._
import java.time.Instant

object UserDbQueries {

  val createTableUsersSql: doobie.ConnectionIO[Int] =
    sql"""
        CREATE TABLE IF NOT EXISTS users (
          id INTEGER AUTO_INCREMENT,
          username TEXT PRIMARY KEY,
          password TEXT NOT NULL,
          registration_timestamp LONG NOT NULL
        )
    """.update.run

  val dropTableUsers: doobie.ConnectionIO[Int] =
    sql"""
        DROP TABLE IF EXISTS users
      """.update.run

  def insertUser(user: User, instant: Instant): doobie.ConnectionIO[Int] =
    sql"""
          INSERT INTO users (username, password, registration_timestamp)
            values (${user.userName}, ${user.passwordHash}, ${instant.toEpochMilli})
    """.update.run

  def fetchByName(username: UserName): doobie.ConnectionIO[Option[User]] = {
    sql"""SELECT username, password FROM users WHERE username = '$username'
    """.query[User].option
  }
}
