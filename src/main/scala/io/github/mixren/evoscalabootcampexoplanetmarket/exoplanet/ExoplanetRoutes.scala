package io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet

import cats.effect.Async
//import cats.implicits._
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._


object ExoplanetRoutes {

  def routes[F[_] : Async](implicit xa: HikariTransactor[F]): HttpRoutes[F] = {
    val repo = new ExoplanetRepository[F]
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {

      // Call: curl http://localhost:8080/exoplanets/all
      // Return exoplanets as Json
      case GET -> Root / "exoplanets" / "all" =>
        Ok(repo.fetchAllExoplanets)

      case GET -> Root / "exoplanets" / "random" / amount =>
        if (amount.matches("^[0-9]+$")) Ok(repo.fetchExoplanetsRandomly(amount.toInt))
        else BadRequest(s"Amount should be an integer, not \"$amount\" ")

    }
  }

}
