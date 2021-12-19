package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect._
import cats.effect.kernel.Ref
import cats.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.MapReservations.MapReservations
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import doobie.hikari.HikariTransactor
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
  def routes[F[_]: Async](reservedExoplanets: Ref[F, MapReservations])(implicit xa: HikariTransactor[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    val repo = new ExoplanetRepository[F]
    val reservationService = new ReservationService[F](repo, reservedExoplanets)

    // TODO Display errors of malformed json parameters (e.g. if the card number is too short, show this in the error message)
    // Check Bankcard Codecs

    HttpRoutes.of[F] {

      // Reserve an exoplanet before purchasing one
      // Fail:    curl http://localhost:8080/purchase/reserve/exoplanet -d '{"exoplanetName" : "Hal Oh 5G", "username" : "Allah"}' -H "Content-Type: application/json"
      // Success: curl http://localhost:8080/purchase/reserve/exoplanet -d '{"exoplanetName" : "2I/Borisov", "username" : "Allah"}' -H "Content-Type: application/json"
      case req @ POST -> Root / "purchase" / "reserve" / "exoplanet" =>
        import cats.effect.Concurrent
        import io.circe.Decoder
        import io.circe.generic.semiauto.deriveDecoder
        import org.http4s.EntityDecoder
        import org.http4s.circe.accumulatingJsonOf

        case class PairExonameUsername (exoplanetName: ExoplanetOfficialName, username: UserName)
        implicit val decoder: Decoder[PairExonameUsername] = deriveDecoder[PairExonameUsername]
        implicit def entityDecoder[G[_]: Concurrent]: EntityDecoder[G, PairExonameUsername] = accumulatingJsonOf[G, PairExonameUsername]

        for {
          pair <- req.as[PairExonameUsername]
          reserved <- reservationService.reserveExoplanet(pair.exoplanetName, pair.username, 5.minutes)
          res <- Ok(reserved.toString)
        } yield res


      // TODO Check if planet is reserved by this user and carry on with banking service
      // curl http://localhost:8080/purchase/exoplanet -d '{"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}' -H "Content-Type: application/json"
      case req @ POST -> Root / "purchase" / "exoplanet" =>
        /*
        import io.circe.parser.decodeAccumulating
        import cats.data.Validated.{Invalid, Valid}
        req.as[String].map(decodeAccumulating[BankCard]).flatMap{
          case Valid(card)  => Ok(card)
          case Invalid(err) => BadRequest(err.show)
        }
        */
        //req.as[BankCard].flatMap(Ok(_)).handleErrorWith(t => BadRequest(t.getCause.getMessage))
        import org.http4s.InvalidMessageBodyFailure
        req.as[BankCard].flatMap(Ok(_)).handleErrorWith{
          case f: InvalidMessageBodyFailure => BadRequest(f.getCause.getMessage)
          case o => BadRequest(o.getMessage)
        }



    }
  }
}
