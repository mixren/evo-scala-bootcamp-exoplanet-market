package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.data.EitherT
import cats.effect.Async
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.{Purchase, PurchasePrice}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import java.time.Instant
import scala.concurrent.duration.DurationInt

case class PurchaseSuccess(msg: String) extends AnyVal
object PurchaseSuccess{
  def of(username: UserName, exoplanetName: ExoplanetOfficialName, exoplanetNewName: ExoplanetNewName): PurchaseSuccess ={
    PurchaseSuccess(s"""$username has successfully named the exoplanet \"$exoplanetName\" as \"$exoplanetNewName\".""")
  }
}

trait MyPurchaseService[F[_]] {
  def makePurchase(quatro: QuatroExosUsrCard): F[Either[String, PurchaseSuccess]]
}

class PurchaseService[F[_]: Async](reservationService: ReservationService[F],
                                   bankingService: BankingService[F],
                                   repo: PurchaseRepository[F]) extends MyPurchaseService[F] {

  // TODO P.S. payment can be made, but db write by repo.addPurchase still can fail
  //  (very not likely, but why not to consider some fallback)
  override def makePurchase(quatro: QuatroExosUsrCard): F[Either[String, PurchaseSuccess]] = {
    (for {
      _      <- EitherT(reservationService.verifyAndExtendReservation(quatro.exoplanetName, quatro.username, 5.minute))
      _      <- EitherT(bankingService.makePayment(quatro.card, BigDecimal(4.99), SomeId("XXX555PPP")))
      _      <- EitherT(repo.addPurchase(Purchase(quatro.exoplanetName,
        quatro.exoplanetNewName,
        quatro.username,
        PurchasePrice(BigDecimal(4.99)),
        Instant.now().toEpochMilli)) )
      _      <- EitherT.right[String](reservationService.releaseReservation(quatro.exoplanetName, quatro.username))   // TODO release it in any case!
    } yield PurchaseSuccess.of(quatro.username, quatro.exoplanetName, quatro.exoplanetNewName))
      .value
  }


}
