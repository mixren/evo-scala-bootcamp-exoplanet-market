package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.Async
import cats.effect.kernel.Ref
import cats.implicits._
import doobie.hikari.HikariTransactor
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.ExoOldNewCardRequest
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.AuthUser
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, InvalidMessageBodyFailure}

import scala.concurrent.duration.DurationInt

object PurchaseRoutes {

  def authRoutes[F[_]: Async](reservedExoplanets: Ref[F, MapReservations])(implicit xa: HikariTransactor[F]): AuthedRoutes[AuthUser, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    val exoRepo = new ExoplanetRepository[F]
    val purRepo = new PurchaseRepository[F]
    val reservationService = new ReservationService[F](exoRepo, purRepo, reservedExoplanets)
    val bankingService = new BankingServiceForTesting[F]
    val purchaseService = new PurchaseService[F](reservationService, bankingService, purRepo)

    AuthedRoutes.of[AuthUser, F] {
      // Reserve an exoplanet before purchasing one
      // Fail:    curl http://localhost:8080/purchase/auth/reserve/exoplanet -d '{"exoplanetName" : "Hal Oh 5G"}' -H "Content-Type: application/json"
      // Success: curl http://localhost:8080/purchase/auth/reserve/exoplanet -d '{"exoplanetName" : "2I/Borisov"}' -H "Content-Type: application/json"
      case req @ POST -> Root / "purchase" / "auth" / "reserve" / "exoplanet" as user =>
        (for {
          exoName   <- req.req.as[ExoplanetOfficialName]
          reserved  <- reservationService.reserveExoplanet(exoName, user.username, 5.minutes)
          resp      <- reserved match {
            case Left(s)  => BadRequest(s)
            case Right(s) => Ok(s)
          }
        } yield resp)
          .handleErrorWith {
            case f: InvalidMessageBodyFailure => BadRequest(f.getMessage + f.getCause.getMessage)    // For when http Json values fail custom validation
            case o => BadRequest(o.getMessage)
          }

      // Purchase Exoplanet
      // curl http://localhost:8080/purchase/auth/exoplanet -d '{"exoplanetName" : "2I/Borisov", "exoplanetNewName" : "new super name", "card" : {"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}}' -H "Content-Type: application/json"
      case req @ POST -> Root / "purchase" / "auth" / "exoplanet" as user =>
        (for {
          trio <- req.req.as[ExoOldNewCardRequest]
          res    <- purchaseService.makePurchase(trio, user.username)
          resp   <- res match {
            case Left(s)   => BadRequest(s)
            case Right(ps) => Ok(ps.msg)
          }
        } yield resp)
          .handleErrorWith {
            case f: InvalidMessageBodyFailure => BadRequest(f.getCause.getMessage)    // For when http Json values fail custom validation
            case o => BadRequest(o.getMessage)
          }

      // Get all purchases by user
      // curl http://localhost:8080/purchase/auth/history/user
      case GET -> Root / "purchase" / "auth"/ "history" / "user" as user =>
        Ok(purRepo.purchasesByUser(user.username))

    }
  }

}
