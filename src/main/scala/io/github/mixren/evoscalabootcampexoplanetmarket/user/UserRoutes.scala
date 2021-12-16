package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits._
import doobie.hikari.HikariTransactor
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.User
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, HttpRoutes}


object UserRoutes {
  def routes[F[_]: Async](implicit xa: HikariTransactor[F]): HttpRoutes[F] = {

    val repo = new UserRepository[F]
    val routesService = new UserRoutesService[F](repo)
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {

      // Call: curl http://localhost:8080/user/login -d '{"userName": "John", "password": "123456"}' -H "Content-Type: application/json"
      // Login user. Return JWT if user is registered, else error
      case req @ POST -> Root / "user" / "login" =>
        val res = routesService.userLogin(req.as[AuthRequest])
        res.value.flatMap{
          case Right(token)  => Ok(token)
          case Left(err)     => Conflict(s"$err")
        }


      // Call: curl http://localhost:8080/user/register -d '{"userName": "John", "password": "123456"}' -H "Content-Type: application/json"
      // Register user. Return JWT if user is registered.
      case req @ POST -> Root / "user" / "register" =>
        val res = routesService.userRegister(req.as[AuthRequest])
        res.value.flatMap {
          case Right(token)  => Ok(token)
          case Left(err)    => Conflict(s"$err")
        }


    }
  }

  def authRoutes[F[_]: Async]: AuthedRoutes[User, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    AuthedRoutes.of[User, F] {
      // curl http://localhost:8080/auth/loggedin --header "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2Mzk2OTIxODYsImlhdCI6MTYzOTYwNTc4NiwidXNlck5hbWUiOiJKb2huIiwicGFzc3dvcmRIYXNoIjoiOGQ5NjllZWY2ZWNhZDNjMjlhM2E2MjkyODBlNjg2Y2YwYzNmNWQ1YTg2YWZmM2NhMTIwMjBjOTIzYWRjNmM5MiJ9.kz_4dKv9TixNcuk1_qz_X8qYZ4ZtKPDk5Zpg5DfSPZk"
      // Check if user is logged in by passing JWT token.
      case GET -> Root / "auth" / "loggedin" as user =>
        Ok(s"${user.userName.value} is logged in")

    }
  }
}
