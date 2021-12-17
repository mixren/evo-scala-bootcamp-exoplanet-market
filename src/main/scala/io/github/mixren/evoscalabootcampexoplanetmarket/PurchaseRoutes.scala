package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.Async
//import doobie.hikari.HikariTransactor
//import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.User
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import org.http4s.circe.CirceEntityCodec._

object PurchaseRoutes {
  // TODO AuthedRoute with with Bankcard, official exoplanet name and new exoplanet name.
  /*def authRoutes[F[_]: Async]: AuthedRoutes[User, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    AuthedRoutes.of[User, F] {
      case req @ GET -> Root / "purchase" / "exoplanet" as user =>
        Ok(s"${user.userName.value} is logged in")

    }
  }*/

  // Test purchase in basic route, then move to Authed
  def routes[F[_]: Async]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    // TODO Display errors of malformed json parameters (e.g. if the card number is too short, show this in the error message)
    // Check Bankcard Codecs
    // curl http://localhost:8080/user/login -d '{"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : 123}' -H "Content-Type: application/json"
    HttpRoutes.of[F] {
      case req @ GET -> Root / "purchase" / "exoplanet" =>
        req.as[Bankcard].flatMap(Ok(_))
    }
  }
}
