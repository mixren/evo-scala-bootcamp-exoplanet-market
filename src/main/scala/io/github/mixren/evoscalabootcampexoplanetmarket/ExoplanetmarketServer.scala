package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import doobie.hikari.HikariTransactor
import fs2.Stream
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRoutes
import io.github.mixren.evoscalabootcampexoplanetmarket.user.UserRoutes
import io.github.mixren.evoscalabootcampexoplanetmarket.MyAuthMiddleware
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object ExoplanetmarketServer {

  def stream[F[_]: Async](implicit xa: HikariTransactor[F]): Stream[F, Nothing] = {

    val middleware = new MyAuthMiddleware

    val httpApp = (
      ExoplanetRoutes.routes[F] <+>
      UserRoutes.routes[F] <+>
      PurchaseRoutes.routes[F] <+>
      middleware(UserRoutes.authRoutes)
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

  /*def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        DefRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        DefRoutes.jokeRoutes[F](jokeAlg) <+>
        ExoplanetmarketRoutes.fetchExoplanetsRoutes[F]
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- Stream.resource(
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
        Resource.eval(Async[F].never)
      )
    } yield exitCode

  }.drain*/
}
