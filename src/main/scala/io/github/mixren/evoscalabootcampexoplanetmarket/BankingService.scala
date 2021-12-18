package io.github.mixren.evoscalabootcampexoplanetmarket

//import cats.effect.Async


case class PaymentResult()  // TODO fix it
case class SomeId()         // TODO fix it
trait BankingService[F[_]] {
  def makePayment(payerCard: BankCard, amount: BigDecimal, receiverId: SomeId): F[PaymentResult] //receiver Id will always be the same
}

//class BankingServiceForTesting[F[_]: Async] extends BankingService[F] {
//  override def makePayment(payerCard: BankCard, amount: BigDecimal, receiverId: SomeId): F[PaymentResult] = ??? // some custom logic just for sake of testing
//}


