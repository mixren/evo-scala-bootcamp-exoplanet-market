package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.IO
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.{FakeBankingService, FakePurchaseRepository, FakeReservationService}
import org.scalatest.flatspec.AnyFlatSpec

class PurchaseServiceTest extends AnyFlatSpec{

  val purchaseService = new PurchaseService[IO](
    new FakeReservationService[IO],
    new FakeBankingService[IO],
    new FakePurchaseRepository[IO]
  )

  "PurchaseService" should "successfully process the exoplanet name purchase in case the user-purchaser" +
    " anticipatorily reserved the exoplanet and the payment is processed sucessfully." in {
      // TODO implement
      // Depends on:
      //      1) reservationService.verifyAndExtendReservation
      //      2) bankingService.makePayment
      // Looks like services need to be "mocked?" differently,
      // since I only need different success/fail(left/right) combinations of 1) and 2) results
      // With my approach I need 2 BankingService and 2 ReservationService mocks... waste of time...
    }
}
