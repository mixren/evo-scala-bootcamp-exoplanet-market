package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits._
import doobie.hikari.HikariTransactor
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

//import java.time.Instant


object UserRoutes {
  def routes[F[_]: Async](implicit xa: HikariTransactor[F]): HttpRoutes[F] = {
    import io.github.mixren.evoscalabootcampexoplanetmarket.utils.JwtHelper._

    val repo = new UserRepository[F]
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {

      // Call: curl http://localhost:8080/user/login -d '{"userName": "John", "userPassword": "123456"}' -H "Content-Type: application/json"
      // Login user. Return JWT if user is registered, else error
      case req @ POST -> Root / "user" / "login" =>
        // TODO add custom error message when header is wrong
        for {
          user      <- req.as[User]
          userO     <- repo.userByName(user.userName)
          response  <- userO match {
            case Some(user)           => Ok(jwtEncode(user))
            case _                    => NoContent()
          }
          // or: response  <- userO.fold(NoContent())(u => Ok(jwtEncode(u)))
        } yield response
//        req.as[User].flatMap { user =>
//          for {
//            userE     <- repo.userByName(user.userName)
//            response  <- userE match {
//              case Left(_)                      => BadRequest()
//              case Right(Some(user))            => Ok(jwtEncode(user))
//              case Right(None)                  => NoContent()
//            }
//          } yield response

      // Call: curl http://localhost:8080/user/register -d '{"name": "John", "password": "123456"}' -H "Content-Type: application/json"
      // Register user. Return JWT if user is registered.
      /*case req @ POST -> Root / "user" / "register" =>
        // TODO make it work
        for {
          authReq     <- req.as[AuthRequest]
          hash        <- crypt.hashpw(authReq.userPassword)

        } yield result
        for {
          user      <- req.as[User]
          userO     <- repo.userByName(user.userName)
          success   <- userO.fold(fail)(repo.addUser(user, Instant.now()))
          response  <- if (success) BadRequest("can't add") else Ok(jwtEncode(user))
        } yield response*/

      // Call: curl http://localhost:8080/user/table/recreate
      // Recreate 'users' sql table
      case GET -> Root / "user" / "table" / "recreate" =>
        Ok(repo.recreateTable())

    }
  }

  def authRoutes[F[_]: Async] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    AuthedRoutes.of[User, F] {

      case GET -> Root / "loggedin" as user =>
        Ok(s"${user.userName} is logged in")

    }
  }
}
