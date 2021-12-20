package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.Async
import cats.implicits.catsSyntaxApply
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.PaymentResult.Successful

import scala.concurrent.duration.DurationInt

sealed trait PaymentResult
object PaymentResult {
  final case class Successful(value: String) extends PaymentResult
  final case class Failed(value: String) extends PaymentResult
}
case class SomeId(value: String) extends AnyVal

trait BankingService[F[_]] {
  def makePayment(payerCard: BankCard, amount: BigDecimal, receiverId: SomeId): F[PaymentResult] //receiver Id will always be the same
}

class BankingServiceForTesting[F[_]: Async] extends BankingService[F] {
  override def makePayment(payerCard: BankCard, amount: BigDecimal, receiverId: SomeId): F[PaymentResult] = {
    Async[F].sleep(1.second) *> Async[F].pure(Successful("wowow"))
  }
}



