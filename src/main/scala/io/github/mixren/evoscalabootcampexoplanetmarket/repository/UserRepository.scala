package io.github.mixren.evoscalabootcampexoplanetmarket.repository

import cats.effect.Async
import cats.implicits.catsSyntaxApplicativeError
import doobie.hikari.HikariTransactor
import io.github.mixren.evoscalabootcampexoplanetmarket.UserDbQueries
import io.github.mixren.evoscalabootcampexoplanetmarket.domain.{User, UserName}
import doobie.implicits._

class UserRepository[F[_]: Async](xa: HikariTransactor[F]) {
  // TODO make
  def userByName(userName: UserName): F[Either[Throwable, User]] = {
    UserDbQueries.fetchByName(userName).transact(xa).attempt
  }
  //
  //def addUser(value: User): Unit = ()
  //def deleteUser(value: User): Unit = ()

}
