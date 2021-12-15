package io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet

import cats.effect.Async
import cats.implicits._
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._



object ExoplanetRoutes {

  def routes[F[_] : Async](implicit xa: HikariTransactor[F]): HttpRoutes[F] = {
    val repo = new ExoplanetsRepository[F]
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {

      // Call: curl http://localhost:8080/exoplanets/all
      // Return exoplanets as Json
      case GET -> Root / "exoplanets" / "all" =>
        for {
          exoplanets <- repo.fetchAllExoplanets
          response   <- Ok(exoplanets)
        } yield response

    }
  }

}
