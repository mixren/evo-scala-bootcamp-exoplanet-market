package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.data.EitherT
import cats.effect.Async
import cats.implicits.catsSyntaxApplicativeError
import doobie.hikari.HikariTransactor
import doobie.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{User, UserName}

import java.time.Instant

// TODO mb add UserRepoHandler for handling logic
class UserRepository[F[_]: Async](implicit xa: HikariTransactor[F]) {

  def userByName(userName: UserName): EitherT[F, String, Option[User]] = {
    sql"""SELECT username, password FROM users WHERE username = $userName
       """
      .query[User]
      .option
      .transact(xa)
      .attemptT
      .leftMap {t => s"Something is wrong with fetching from db. $t"}
    }

  //def deleteUser(value: User): Unit = ()


  def createUser(user: User, instant: Instant): EitherT[F, String, User] = {
    val insertSql = sql"""
                      INSERT INTO users (username, password, registration_timestamp)
                      values (${user.userName}, ${user.passwordHash}, ${instant.toEpochMilli})
                      """
    for {
      userO   <- userByName(user.userName)
      result  <- userO match {
        case Some(usr) => EitherT.leftT[F, User](s"User ${usr.userName.value} already exists")
        case None      => insertSql.update.run.transact(xa)
          .attemptT.leftMap(_.getMessage).map(_ => user)
      }
    } yield result

  }
}
