package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits._
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl


object UserRoutes {
  def routes[F[_]: Async](implicit xa: HikariTransactor[F]): HttpRoutes[F] = {
    import io.github.mixren.evoscalabootcampexoplanetmarket.utils.JwtHelper._

    val repo = new UserRepository[F]
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {

      // Call: curl http://localhost:8080/user/login -d '{"name": "John", "password": "123456"}' -H "Content-Type: application/json"
      // Login user. Return JWT if user is registered, else error
      case req @ POST -> Root / "user" / "login" =>
        req.as[User].flatMap { user =>
          for {
            userE     <- repo.userByName(user.userName)
            response  <- userE match {  //TODO recheck the behaviour
              case Left(throwable: Throwable)   => BadRequest(throwable.getMessage)
              case Right(Some(user))            => Ok(jwtEncode(user))
              case Right(None)                  => NoContent()
            }
          } yield response
        }

      // Call: curl http://localhost:8080/user/register -d '{"name": "John", "password": "123456"}' -H "Content-Type: application/json"
      // Register user. Return JWT if user is registered.
      /*case req @ POST -> Root / "user" / "register" =>
        req.as[User].flatMap { user =>
          for {
            userE <- transactor.use(xa => new UserRepository[F].userByName(user.userName))
            response   <- userE match {
              // TODO define the throwable for when user in table was not found
              case Left(throwable: Throwable) => Ok()
              case Right(_)                   => BadRequest("User with this username exists already")
            }

          } yield response
        }*/

      // Call: curl http://localhost:8080/user/table/recreate
      // Recreate 'users' sql table
      case GET -> Root / "user" / "table" / "recreate" =>
        Ok(repo.recreateTable())
    }
  }
}
