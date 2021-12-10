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
}