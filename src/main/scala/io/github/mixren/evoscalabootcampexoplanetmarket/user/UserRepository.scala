package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits.{catsSyntaxEitherId, toFlatMapOps}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{User, UserName}
import java.time.Instant


trait UserRepositoryT[F[_]]{
  def userByName(userName: UserName): F[Option[User]]
  def createUser(user: User, instant: Instant): F[Either[String, Int]]
}

class UserRepository[F[_]: Async](implicit xa: HikariTransactor[F]) extends UserRepositoryT[F] {

  /*def userByName(userName: UserName): EitherT[F, String, Option[User]] = {
    sql"""SELECT username, password FROM users WHERE username = $userName
       """
      .query[User]
      .option
      .transact(xa)
      .attemptT
      .leftMap {t => s"Something is wrong with fetching from db. $t"}
    }*/

  def userByName(userName: UserName): F[Option[User]] = {
    sql"""SELECT username, password FROM users WHERE username = $userName
       """
      .query[User]
      .option
      .transact(xa)
  }

  def createUser(user: User, instant: Instant): F[Either[String, Int]] = {
    val insertSql = sql"""
                      INSERT INTO users (username, password, registration_timestamp)
                      values (${user.username}, ${user.passwordHash}, ${instant.toEpochMilli})
                      """
    userByName(user.username).flatMap{
      case Some(usr) => Async[F].pure(s"Error. User ${usr.username.value} already exists".asLeft)
      case None      => Async[F].fmap(insertSql.update.run.transact(xa))(_.asRight[String])
    }
  }
}
