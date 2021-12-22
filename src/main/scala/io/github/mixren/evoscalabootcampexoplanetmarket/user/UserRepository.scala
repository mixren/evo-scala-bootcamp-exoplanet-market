package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits.{catsSyntaxEitherId, toFlatMapOps}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{User, UserName}

import java.time.Instant

// TODO mb add UserRepoHandler for handling logic
class UserRepository[F[_]: Async](implicit xa: HikariTransactor[F]) {

  /*def userByName(userName: UserName): EitherT[F, String, Option[User]] = {
    sql"""SELECT username, password FROM users WHERE username = $userName
       """
      .query[User]
      .option
      .transact(xa)
      .attemptT
      .leftMap {t => s"Something is wrong with fetching from db. $t"}
    }*/

  def userByName2(userName: UserName): F[Option[User]] = {
    sql"""SELECT username, password FROM users WHERE username = $userName
       """
      .query[User]
      .option
      .transact(xa)
  }

  //def deleteUser(value: User): Unit = ()


  /*def createUser(user: User, instant: Instant): EitherT[F, String, User] = {
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
  }*/

  def createUser2(user: User, instant: Instant): F[Either[String, Int]] = {
    val insertSql = sql"""
                      INSERT INTO users (username, password, registration_timestamp)
                      values (${user.userName}, ${user.passwordHash}, ${instant.toEpochMilli})
                      """
    userByName2(user.userName).flatMap{
      case Some(usr) => Async[F].pure(s"Error. User ${usr.userName.value} already exists".asLeft)
      case None      => Async[F].fmap(insertSql.update.run.transact(xa))(_.asRight[String])
    }
  }
}
