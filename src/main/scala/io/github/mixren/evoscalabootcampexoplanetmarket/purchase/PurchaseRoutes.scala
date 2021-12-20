package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.Async
import cats.effect.kernel.Ref
import cats.implicits._
import doobie.hikari.HikariTransactor
import io.github.mixren.evoscalabootcampexoplanetmarket.MapReservations.MapReservations
import io.github.mixren.evoscalabootcampexoplanetmarket._
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.{Purchase, PurchasePrice}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, InvalidMessageBodyFailure}

import java.time.Instant
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
    val reservationService = new ReservationService[F](exoRepo, reservedExoplanets)
    val purchaseService = new PurchaseService[F](purRepo)
    val bankingService = new BankingServiceForTesting[F]


    HttpRoutes.of[F] {

      // Reserve an exoplanet before purchasing one
      // Fail:    curl http://localhost:8080/purchase/reserve/exoplanet -d '{"exoplanetName" : "Hal Oh 5G", "username" : "Allah"}' -H "Content-Type: application/json"
      // Success: curl http://localhost:8080/purchase/reserve/exoplanet -d '{"exoplanetName" : "2I/Borisov", "username" : "Allah"}' -H "Content-Type: application/json"
      case req@POST -> Root / "purchase" / "reserve" / "exoplanet" =>
        for {
          pair <- req.as[PairExonameUsername]
          reserved <- reservationService.reserveExoplanet(pair.exoplanetName, pair.username, 5.minutes)
          res <- Ok(reserved.toString)
        } yield res


      // TODO Check if planet is reserved by this user and carry on with banking service
      // Purchase Exoplanet
      // curl http://localhost:8080/purchase/exoplanet -d '{"exoplanetName" : "2I/Borisov", "exoplanetNewName" : "new super name", "card" : {"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}}' -H "Content-Type: application/json"
      case req@POST -> Root / "purchase" / "exoplanet" =>
        /*req.as[PairExonameCard].flatMap(Ok(_)).handleErrorWith{
                                           case f: InvalidMessageBodyFailure => BadRequest(f.getCause.getMessage)
                                           case o => BadRequest(o.getMessage)
                                         }*/
        (for {
          quatro <- req.as[QuatroExosUsrCard]
          _      <- reservationService.verifyReservation(quatro.exoplanetName, quatro.username, 5.minute)
          _      <- bankingService.makePayment(quatro.card, BigDecimal(4.99), SomeId("XXX555PPP"))
          _      <- purchaseService.savePurchase(Purchase(quatro.exoplanetName,
                 quatro.exoplanetNewName,
                 quatro.username,
                 PurchasePrice(BigDecimal(4.99)),
                 Instant.now().toEpochMilli))
          _      <- reservationService.releaseReservation(quatro.exoplanetName, quatro.username)      // TODO release it in any case!
          resp   <- Ok(s"Exoplanet ${quatro.exoplanetName} is renamed to ${quatro.exoplanetNewName}")
        } yield resp)
          .handleErrorWith {
            case f: InvalidMessageBodyFailure => BadRequest(f.getCause.getMessage)
            case t: ReservationError => BadRequest(t.getMessage)
            case o => BadRequest(o.getMessage)
          }

    }
  }
}
