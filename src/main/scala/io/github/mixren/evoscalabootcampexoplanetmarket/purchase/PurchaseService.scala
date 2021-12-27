package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.data.EitherT
import cats.effect.Async
import cats.implicits.catsSyntaxApply
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.{ExoOldNewCardRequest, Purchase, PurchasePrice, PurchaseSuccess}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import java.time.Instant
import scala.concurrent.duration.DurationInt


trait PurchaseServiceT[F[_]] {
  def makePurchase(trio: ExoOldNewCardRequest, userName: UserName): F[Either[String, PurchaseSuccess]]
}

class PurchaseService[F[_]: Async](reservationService: ReservationServiceT[F],
                                   bankingService: BankingServiceT[F],
                                   repo: PurchaseRepositoryT[F]) extends PurchaseServiceT[F] {

  override def makePurchase(trio: ExoOldNewCardRequest, username: UserName): F[Either[String, PurchaseSuccess]] = {
    val purchaseT = for {
      _      <- EitherT(reservationService.verifyAndExtendReservation(trio.exoplanetName, username, 5.minute))
      _      <- EitherT(bankingService.makePayment(trio.card, BigDecimal(4.99), SomeId("XXX555PPP")))
      _      <- EitherT(repo.addPurchase(Purchase(trio.exoplanetName,
        trio.exoplanetNewName,
        username,
        PurchasePrice(BigDecimal(4.99)),
        Instant.now().toEpochMilli)) )      // TODO db write by repo.addPurchase still can fail. Although, very not likely, but why not to consider some fallback
      _      <- EitherT.right[String](reservationService.releaseReservation(trio.exoplanetName, username))
    } yield PurchaseSuccess.of(username, trio.exoplanetName, trio.exoplanetNewName)

    // No matter what's the result of the purchase, reservation is released at the end.
    // Discards the second result so it's gonna return "purchaseT.value
    purchaseT.value <* reservationService.releaseReservation(trio.exoplanetName, username)
  }

}
