package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.data.EitherT
import cats.effect.Async
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.{Purchase, PurchasePrice, PurchaseSuccess, TrioExosCard}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import java.time.Instant
import scala.concurrent.duration.DurationInt


trait PurchaseServiceT[F[_]] {
  def makePurchase(trio: TrioExosCard, userName: UserName): F[Either[String, PurchaseSuccess]]
}

class PurchaseService[F[_]: Async](reservationService: ReservationServiceT[F],
                                   bankingService: BankingServiceT[F],
                                   repo: PurchaseRepositoryT[F]) extends PurchaseServiceT[F] {

  // TODO P.S. payment can be made, but db write by repo.addPurchase still can fail.
  //  Although, very not likely, but why not to consider some fallback
  override def makePurchase(trio: TrioExosCard, username: UserName): F[Either[String, PurchaseSuccess]] = {
    (for {
      _      <- EitherT(reservationService.verifyAndExtendReservation(trio.exoplanetName, username, 5.minute))
      _      <- EitherT(bankingService.makePayment(trio.card, BigDecimal(4.99), SomeId("XXX555PPP")))
      _      <- EitherT(repo.addPurchase(Purchase(trio.exoplanetName,
        trio.exoplanetNewName,
        username,
        PurchasePrice(BigDecimal(4.99)),
        Instant.now().toEpochMilli)) )
      _      <- EitherT.right[String](reservationService.releaseReservation(trio.exoplanetName, username))   // TODO release it in any case! mb in Main
    } yield PurchaseSuccess.of(username, trio.exoplanetName, trio.exoplanetNewName))
      .value
  }


}
