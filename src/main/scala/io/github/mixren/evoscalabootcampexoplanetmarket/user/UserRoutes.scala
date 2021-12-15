package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.data.EitherT
import cats.effect.Async
import cats.implicits._
import doobie.hikari.HikariTransactor
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.HashGenerator
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, HttpRoutes}

import java.time.Instant


object UserRoutes {
  def routes[F[_]: Async](implicit xa: HikariTransactor[F]): HttpRoutes[F] = {
    import io.github.mixren.evoscalabootcampexoplanetmarket.utils.JwtHelper._

    val repo = new UserRepository[F]
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {

      // Call: curl http://localhost:8080/user/login -d '{"userName": "John", "password": "123456"}' -H "Content-Type: application/json"
      // Login user. Return JWT if user is registered, else error
      case req @ POST -> Root / "user" / "login" =>
        val action = for {
          authReq     <- req.as[AuthRequest].attemptT.leftMap(t => t.getMessage)
          userO       <- repo.userByName(authReq.userName)
          user        <- EitherT.fromOption[F](userO, s"User ${authReq.userName.value} is not registered")
          hash        = HashGenerator.run(authReq.password.value)
          isValidUser = user.validate(hash)
          token       = jwtEncode(user)
          result      <- if (isValidUser) {
                           EitherT.rightT[F, String](token)
                         } else {
                           EitherT.leftT[F, String]("Wrong password")
                         }
        } yield result

        action.value.flatMap{
          case Right(token)  => Ok(token)
          case Left(err)     => Conflict(s"$err")
        }


      // Call: curl http://localhost:8080/user/register -d '{"userName": "John", "password": "123456"}' -H "Content-Type: application/json"
      // Register user. Return JWT if user is registered.
      case req @ POST -> Root / "user" / "register" =>
        val action = for {
          authReq     <- req.as[AuthRequest]
          hash        = HashGenerator.run(authReq.password.value)
          user        = authReq.asUser(PasswordHash(hash))
          result      <- repo.createUser(user, Instant.now()).value
        } yield result

        action.flatMap {
          case Right(user)  => Ok(jwtEncode(user))
          case Left(err)    => Conflict(s"$err")
        }


      // Call: curl http://localhost:8080/user/table/recreate
      // Recreate 'users' sql table
      case GET -> Root / "user" / "table" / "recreate" =>
        Ok(repo.recreateTable())

    }
  }

  def authRoutes[F[_]: Async]: AuthedRoutes[User, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    AuthedRoutes.of[User, F] {
      // TODO How to call this route?
      // curl --location --request GET 'http://localhost:8080/auth/loggedin' \ --header 'Authorization: Bearer MTEwNiwidXNlck5hbWUiOiJKb2huIiwicGFzc3dvcmRIYXNoIjoiOGQ5NjllZWY2ZWNhZDNjMjlhM2E2MjkyODBlNjg2Y2YwYzNmNWQ1YTg2YWZmM2NhMTIwMjBjOTIzYWRjNmM5MiJ9.Ogxi77p8uDTNuMumafXb-17A1S2r5SvFN5huxH7hMH0'
      // Check if user is logged in by passing JWT token.
      case GET -> Root / "auth" / "loggedin" as user =>
        Ok(s"${user.userName} is logged in")

    }
  }
}
