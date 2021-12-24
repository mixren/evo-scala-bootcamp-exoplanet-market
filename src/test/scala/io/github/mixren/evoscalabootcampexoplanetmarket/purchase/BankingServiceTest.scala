package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeBankCard.fakeBankCard
import org.scalatest.flatspec.AnyFlatSpec


class BankingServiceTest extends AnyFlatSpec{

  val bankingService = new BankingServiceForTesting[IO]

  "BankingService" should "work successfully" in{
    assert(bankingService.makePayment(fakeBankCard, BigDecimal(4.99), SomeId("someId")).unsafeRunSync().isRight)
  }

}
