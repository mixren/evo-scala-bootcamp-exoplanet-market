package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import cats.effect.Async
import cats.implicits.{catsSyntaxApply, catsSyntaxEitherId}
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.{BankingServiceT, PaymentSuccessful, SomeId}
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.BankCard

import scala.concurrent.duration.DurationInt

class FakeBankingService[F[_]: Async] extends BankingServiceT[F] {
  override def makePayment(payerCard: BankCard, amount: BigDecimal, receiverId: SomeId): F[Either[String, PaymentSuccessful]] = {
    Async[F].sleep(1.second) *> Async[F].pure(PaymentSuccessful("wowow").asRight)
  }
}
