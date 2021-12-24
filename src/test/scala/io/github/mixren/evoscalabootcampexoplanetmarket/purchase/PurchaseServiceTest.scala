package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxEitherId
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeUsers.validUsername1
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

import java.time.YearMonth
import java.time.format.DateTimeFormatter

class PurchaseServiceTest extends AnyFlatSpec with MockFactory{

  val reservationServiceMock: ReservationServiceT[IO] = mock[ReservationServiceT[IO]]
  val bankingServiceMock:     BankingServiceT[IO]     = mock[BankingServiceT[IO]]
  val purchaseRepositoryMock: PurchaseRepositoryT[IO] = mock[PurchaseRepositoryT[IO]]

  val purchaseService = new PurchaseService[IO](
    reservationServiceMock,
    bankingServiceMock,
    purchaseRepositoryMock
  )

  val trioExosCard: TrioExosCard = TrioExosCard(
    ExoplanetOfficialName("exoOffName"),
    ExoplanetNewName("exoNewName"),
    BankCard(
      CardHolderName("Bake Baker"),
      CardNumber("1111222233334444"),
      CardExpiration(YearMonth.parse("2023-12", DateTimeFormatter.ofPattern("uuuu-MM"))),
      CardCvc("123")
    )
  )

  "PurchaseService" should "successfully process the exoplanet name purchase in case the user-purchaser" +
    " anticipatorily reserved the exoplanet and the payment is processed sucessfully." in {

    (reservationServiceMock.verifyAndExtendReservation _).expects(*, *, *).returning(IO.pure(().asRight))
    (bankingServiceMock.makePayment _).expects(*, *, *).returning(IO.pure(PaymentSuccessful("").asRight))
    (purchaseRepositoryMock.addPurchase _).expects(*).returning(IO.pure(0.asRight))
    (reservationServiceMock.releaseReservation _).expects(*, *).returning(IO.pure(().asRight))
    assert(purchaseService.makePurchase(trioExosCard, validUsername1).unsafeRunSync().isRight)

    (reservationServiceMock.verifyAndExtendReservation _).expects(*, *, *).returning(IO.pure("".asLeft))
    assert(purchaseService.makePurchase(trioExosCard, validUsername1).unsafeRunSync().isLeft)

    (reservationServiceMock.verifyAndExtendReservation _).expects(*, *, *).returning(IO.pure(().asRight))
    (bankingServiceMock.makePayment _).expects(*, *, *).returning(IO.pure("".asLeft))
    assert(purchaseService.makePurchase(trioExosCard, validUsername1).unsafeRunSync().isLeft)
    }
}
