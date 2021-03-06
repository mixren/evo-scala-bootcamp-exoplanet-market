package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxEitherId
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeBankCard.fakeBankCard
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeUsers.validUsername1
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

class PurchaseServiceTest extends AnyFlatSpec with MockFactory{

  val reservationServiceMock: ReservationServiceT[IO] = mock[ReservationServiceT[IO]]
  val bankingServiceMock:     BankingServiceT[IO]     = mock[BankingServiceT[IO]]
  val purchaseRepositoryMock: PurchaseRepositoryT[IO] = mock[PurchaseRepositoryT[IO]]

  val purchaseService = new PurchaseService[IO](
    reservationServiceMock,
    bankingServiceMock,
    purchaseRepositoryMock
  )

  val trioExosCard: ExoOldNewCardRequest = ExoOldNewCardRequest(
    ExoplanetOfficialName("exoOffName"),
    ExoplanetNewName("exoNewName"),
    fakeBankCard
  )

  "PurchaseService" should "successfully process the exoplanet name purchase in case the user-purchaser" +
    " anticipatorily reserved the exoplanet and the payment is processed sucessfully." in {
    (reservationServiceMock.verifyAndExtendReservation _).expects(trioExosCard.exoplanetName, validUsername1, *).returning(IO.pure(().asRight))
    (bankingServiceMock.makePayment _).expects(trioExosCard.card, *, *).returning(IO.pure(PaymentSuccessful("").asRight))
    (purchaseRepositoryMock.addPurchase _).expects(*).returning(IO.pure(0.asRight))
    (reservationServiceMock.releaseReservation _).expects(trioExosCard.exoplanetName, validUsername1).returning(IO.pure(().asRight))
    assert(purchaseService.makePurchase(trioExosCard, validUsername1).unsafeRunSync().isRight)
  }

  it should "not allow to process the purchase when exoplanet reservation verification fails" in {
    (reservationServiceMock.verifyAndExtendReservation _).expects(*, *, *).returning(IO.pure("".asLeft))
    (reservationServiceMock.releaseReservation _).expects(trioExosCard.exoplanetName, validUsername1).returning(IO.pure(().asRight))
    assert(purchaseService.makePurchase(trioExosCard, validUsername1).unsafeRunSync().isLeft)
  }

  it should "not allow to process the purchase if payment is unsuccessful" in {
    (reservationServiceMock.verifyAndExtendReservation _).expects(*, *, *).returning(IO.pure(().asRight))
    (bankingServiceMock.makePayment _).expects(*, *, *).returning(IO.pure("".asLeft))
    (reservationServiceMock.releaseReservation _).expects(trioExosCard.exoplanetName, validUsername1).returning(IO.pure(().asRight))
    assert(purchaseService.makePurchase(trioExosCard, validUsername1).unsafeRunSync().isLeft)
  }

  it should "not depend on the exoplanet reservation release result" in {
    (reservationServiceMock.verifyAndExtendReservation _).expects(trioExosCard.exoplanetName, validUsername1, *).returning(IO.pure(().asRight))
    (bankingServiceMock.makePayment _).expects(trioExosCard.card, *, *).returning(IO.pure(PaymentSuccessful("").asRight))
    (purchaseRepositoryMock.addPurchase _).expects(*).returning(IO.pure(0.asRight))
    (reservationServiceMock.releaseReservation _).expects(trioExosCard.exoplanetName, validUsername1).returning(IO.pure("".asLeft))
    assert(purchaseService.makePurchase(trioExosCard, validUsername1).unsafeRunSync().isRight)
  }

}
