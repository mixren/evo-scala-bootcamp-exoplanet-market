package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.Async
import cats.implicits._
import doobie.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl


object ExoplanetmarketRoutes {

  def fetchExoplanetsRoutes[F[_]: Async]: HttpRoutes[F] = {
    import org.http4s.circe.CirceEntityCodec._

    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      // curl http://localhost:8080/exoplanets/all
      case GET -> Root / "exoplanets" / "all" =>
        val xa = DbTransactor.makeXa
        for {
          exoplanetsE <- DbQueries.fetchAllExoplanets().transact(xa).attempt
          response   <- exoplanetsE match {
            case Left(throwable: Throwable) => BadRequest(throwable.getMessage)
            case Right(exoplanets) => Ok(exoplanets)
          }
        } yield response

    }
  }

  def authRoutes[F[_]: Async]: HttpRoutes[F] = {
    import org.http4s.circe.CirceEntityCodec._
    import io.circe.generic.auto._


    def jwtEncode(user: User): String = {
      import java.time.Instant
      import pdi.jwt.{JwtCirce, JwtAlgorithm, JwtClaim}
      import io.circe.syntax.EncoderOps

      val userJson = user.asJson.noSpaces
      println(userJson)
      val claim = JwtClaim(
        content = userJson,
        expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
        issuedAt = Some(Instant.now.getEpochSecond)
      )
      // claim: JwtClaim = JwtClaim({}, None, None, None, Some(1791123256), None, Some(1633338496), None)
      val key = "secretKey"
      val algo = JwtAlgorithm.HS256
      JwtCirce.encode(claim, key, algo)
      // token: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3OTExMjMyNTYsImlhdCI6MTYzMzMzODQ5Nn0.mvDSTVzGgZvhBf6Iw7zdijJ3bFozj9UeJkelFyr-pws"
    }

    case class User(name: String, password: String)

    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      // curl http://localhost:8080/auth/login -d '{"name": "John", "password": "123456"}' -H "Content-Type: application/json"
      case req @ POST -> Root / "auth" / "login" =>
        req.as[User].handleError(_ => User("a", "1")).flatMap { user =>
          // TODO first, check user in db
          // if exists - authenticate
          val token: String = jwtEncode(user)
          Ok(token)
        }
    }
  }
}