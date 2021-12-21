package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.data.EitherT
import cats.effect.Async
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.{Purchase, PurchasePrice}

import java.time.Instant
import scala.concurrent.duration.DurationInt


/*trait MyPurchaseService[F[_]] {
  def makePurchase(quatro: QuatroExosUsrCard): F[Int]
}*/

class PurchaseService[F[_]: Async](reservationService: ReservationService[F],
                                   bankingService: BankingService[F],
                                   repo: PurchaseRepository[F]) {

  // TODO finish it!
  def makePurchase(quatro: QuatroExosUsrCard): F[Either[String, String]] = {
    (for {
      _      <- EitherT(reservationService.verifyReservation(quatro.exoplanetName, quatro.username, 5.minute))
      _      <- EitherT(bankingService.makePayment(quatro.card, BigDecimal(4.99), SomeId("XXX555PPP")))
      _      <- EitherT(repo.addPurchase(Purchase(quatro.exoplanetName,
        quatro.exoplanetNewName,
        quatro.username,
        PurchasePrice(BigDecimal(4.99)),
        Instant.now().toEpochMilli)) )
      _      <- EitherT.right[String](reservationService.releaseReservation(quatro.exoplanetName, quatro.username))   // TODO release it in any case!
    } yield s"""${quatro.username} has successfully named the exoplanet \"${quatro.exoplanetName}\" as \"${quatro.exoplanetNewName}\".""")
      .value
  }



}
