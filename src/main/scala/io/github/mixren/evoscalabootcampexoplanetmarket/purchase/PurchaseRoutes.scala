package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.Async
import cats.effect.kernel.Ref
import cats.implicits._
import doobie.hikari.HikariTransactor
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.MapReservations.MapReservations
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, InvalidMessageBodyFailure}

import scala.concurrent.duration.DurationInt

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


  // TODO Test purchase in basic route, then move to Authed
  def routes[F[_] : Async](reservedExoplanets: Ref[F, MapReservations])(implicit xa: HikariTransactor[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    val exoRepo = new ExoplanetRepository[F]
    val purRepo = new PurchaseRepository[F]
    val reservationService = new ReservationService[F](exoRepo, purRepo, reservedExoplanets)
    val bankingService = new BankingServiceForTesting[F]
    val purchaseService = new PurchaseService[F](reservationService, bankingService, purRepo)



    HttpRoutes.of[F] {

      // Reserve an exoplanet before purchasing one
      // Fail:    curl http://localhost:8080/purchase/reserve/exoplanet -d '{"exoplanetName" : "Hal Oh 5G", "username" : "Allah"}' -H "Content-Type: application/json"
      // Success: curl http://localhost:8080/purchase/reserve/exoplanet -d '{"exoplanetName" : "2I/Borisov", "username" : "Allah"}' -H "Content-Type: application/json"
      case req@POST -> Root / "purchase" / "reserve" / "exoplanet" =>
        for {
          pair <- req.as[PairExonameUsername]
          reserved <- reservationService.reserveExoplanet(pair.exoplanetName, pair.username, 5.minutes)
          res <- reserved match {
            case Left(s)  => BadRequest(s)
            case Right(s) => Ok(s)
          }
        } yield res


      // Purchase Exoplanet
      // curl http://localhost:8080/purchase/exoplanet -d '{"exoplanetName" : "2I/Borisov", "exoplanetNewName" : "new super name", "card" : {"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}}' -H "Content-Type: application/json"
      case req@POST -> Root / "purchase" / "exoplanet" =>
        /*req.as[PairExonameCard].flatMap(Ok(_)).handleErrorWith{
                                           case f: InvalidMessageBodyFailure => BadRequest(f.getCause.getMessage)
                                           case o => BadRequest(o.getMessage)
                                         }*/
        (for {
          quatro <- req.as[QuatroExosUsrCard]
          res    <- purchaseService.makePurchase(quatro)
          resp   <- res match {
            case Left(s)   => BadRequest(s)
            case Right(ps) => Ok(ps.msg)
          }
        } yield resp)
          .handleErrorWith {
            case f: InvalidMessageBodyFailure => BadRequest(f.getCause.getMessage)    // For when http Json values fail custom validation
            case o => BadRequest(o.getMessage)
          }

    }
  }
}
