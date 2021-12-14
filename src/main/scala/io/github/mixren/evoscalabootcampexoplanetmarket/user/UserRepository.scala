package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.data.EitherT
import cats.effect.Async
import cats.implicits.catsSyntaxApplicativeError
import doobie.hikari.HikariTransactor
import doobie.implicits._

import java.time.Instant

class UserRepository[F[_]: Async](implicit xa: HikariTransactor[F]) {

  def userByName(userName: UserName): EitherT[F, String, Option[User]] = {
    UserDbQueries.fetchByName(userName).transact(xa)
      .attemptT.transform {
      case Left(_: ArrayIndexOutOfBoundsException) => Right(Option.empty[User]): Either[String, Option[User]]
      case Left(t)                                 => Left(s"Something is wrong with fetching from db. $t"): Either[String, Option[User]]
      case Right(value)                            => Right(value): Either[String, Option[User]]
    }
    // Transform because if DB is empty the exception is thrown: ArrayIndexOutOfBoundsException
    // But I want Option[User] to be None and catch other exceptions
  }

  //def deleteUser(value: User): Unit = ()

  def recreateTable(): F[Int] = {
    //(UserDbQueries.dropTableUsers, UserDbQueries.createTableUsersSql).mapN(_ + _).transact(xa).attempt
    UserDbQueries.createTableUsersSql.transact(xa)
  }

  def createUser(user: User, instant: Instant): EitherT[F, String, User] = {
    for {
      userO   <- userByName(user.userName)
      result  <- userO match {
        case Some(usr) => EitherT.leftT[F, User](s"User ${usr.userName.value} already exists")
        case None      => UserDbQueries.insertUser(user, instant).transact(xa)
          .attemptT.leftMap(_.getMessage).map(_ => user)
      }
    } yield result

  }
}
