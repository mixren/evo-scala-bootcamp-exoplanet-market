package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.Async
import cats.implicits._
import doobie.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.domain.{User, UserName, UserPassword}
import io.github.mixren.evoscalabootcampexoplanetmarket.repository.{ExoplanetsRepository, UserRepository}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl


object ExoplanetmarketRoutes {

  def fetchExoplanetsRoutes[F[_]: Async]: HttpRoutes[F] = {
    import org.http4s.circe.CirceEntityCodec._

    val dsl = new Http4sDsl[F] {}
    import dsl._

    val transactor = DbTransactor.pooled[F]

    HttpRoutes.of[F] {

      // Call: curl http://localhost:8080/exoplanets/all
      // Return exoplanets as Json
      case GET -> Root / "exoplanets" / "all" =>
        for {
          exoplanetsE <- transactor.use(xa => new ExoplanetsRepository[F](xa).fetchAllExoplanets)
          response   <- exoplanetsE match {
            case Left(throwable: Throwable) => BadRequest(throwable.getMessage)
            case Right(exoplanets)          => Ok(exoplanets)
          }
        } yield response

    }
  }


  def authRoutes[F[_]: Async]: HttpRoutes[F] = {
    import org.http4s.circe.CirceEntityCodec._
    import io.github.mixren.evoscalabootcampexoplanetmarket.JwtHelper._

    val dsl = new Http4sDsl[F] {}
    import dsl._

    //val transactor = DbTransactor.pooled[F]

    HttpRoutes.of[F] {
      // curl http://localhost:8080/auth/login -d '{"name": "John", "password": "123456"}' -H "Content-Type: application/json"
      case req @ POST -> Root / "auth" / "login" =>

        req.as[User].handleError(_ => User(UserName("a"), UserPassword("1"))).flatMap { user =>
          // TODO first, check user in db
          //UserRepository
          // if exists - authenticate
          val token: String = jwtEncode(user)
          Ok(token)
        }
    }
  }
}