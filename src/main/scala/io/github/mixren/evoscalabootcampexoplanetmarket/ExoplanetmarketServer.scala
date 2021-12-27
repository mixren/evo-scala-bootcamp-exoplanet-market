package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.kernel.Ref
import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import doobie.hikari.HikariTransactor
import fs2.Stream
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRoutes
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.PurchaseRoutes
import io.github.mixren.evoscalabootcampexoplanetmarket.user.UserRoutes
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object ExoplanetmarketServer {

  def stream[F[_]: Async](reservedExoplanets: Ref[F, MapReservations])(implicit xa: HikariTransactor[F]): Stream[F, Nothing] = {

    val middleware = new MyAuthMiddleware

    val httpApp = (
      ExoplanetRoutes.routes[F] <+>
      UserRoutes.routes[F] <+>
      middleware(PurchaseRoutes.authRoutes[F](reservedExoplanets)) <+>
      middleware(UserRoutes.authRoutes[F])
      ).orNotFound

    // With Middlewares in place
    val finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

    Stream.resource(
      EmberServerBuilder.default[F]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(finalHttpApp)
        .build >>
        Resource.eval(Async[F].never[Unit])
    ).drain
  }

}
