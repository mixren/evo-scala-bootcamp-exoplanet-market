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
    // curl http://localhost:8080/purchase/exoplanet -d '{"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : 123}' -H "Content-Type: application/json"
    HttpRoutes.of[F] {

      // TODO Reserve an exoplanet before purchasing one.
      //  Should include an expiration time for the purchase (~5 min), user who reserved it and exoplanet's official name
      case req @ POST -> Root / "purchase" / "reserve" / "exoplanet" =>
        req.as[BankCard].flatMap(Ok(_))

      // TODO Check if planet is reserved by this user and carry on with banking service
      case req @ POST -> Root / "purchase" / "exoplanet" =>
        req.as[BankCard].flatMap(Ok(_)).handleErrorWith(t => BadRequest(t.getCause.getMessage))


    }
  }
}
