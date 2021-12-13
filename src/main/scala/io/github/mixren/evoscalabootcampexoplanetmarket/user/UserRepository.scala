package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits.catsSyntaxApplicativeError
import doobie.hikari.HikariTransactor
import doobie.implicits._

import java.time.Instant

class UserRepository[F[_]: Async](implicit xa: HikariTransactor[F]) {

  def userByName(userName: UserName): F[Option[User]] = {
    UserDbQueries.fetchByName(userName).transact(xa)
  }

  //
  //def addUser(value: User): Unit = ()
  //def deleteUser(value: User): Unit = ()

  def recreateTable(): F[Int] = {
    //(UserDbQueries.dropTableUsers, UserDbQueries.createTableUsersSql).mapN(_ + _).transact(xa).attempt
    UserDbQueries.createTableUsersSql.transact(xa)
  }

  def addUser(user: User, instant: Instant): F[Either[Throwable, Int]] = {
    UserDbQueries.insertUser(user, instant).transact(xa).attempt

  }
}
