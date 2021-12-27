package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.IO
import cats.effect.kernel.Ref
import cats.effect.unsafe.implicits.global
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepositoryT
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeExoplanets._
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakePurchases.{purEx1U1, purEx2U2}
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeUsers._
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration.{DurationInt, FiniteDuration}


class ReservationServiceTest extends AnyFlatSpec with MockFactory{

  val exoplanetRepositoryStub: ExoplanetRepositoryT[IO] = stub[ExoplanetRepositoryT[IO]]
  val purchaseRepositoryStub: PurchaseRepositoryT[IO]   = stub[PurchaseRepositoryT[IO]]

  val reservationDuration: FiniteDuration = 1.minute


  "ReservationService" should "let users reserve not purchased and not reserved exoplanets" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(None)

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx2U1 = reservationService.reserveExoplanet(realExoplanetName2, validUsername1, reservationDuration)
    val reserveEx3U2 = reservationService.reserveExoplanet(realExoplanetName3, validUsername2, reservationDuration)

    assert(reserveEx1U1.unsafeRunSync().isRight)
    assert(reserveEx2U1.unsafeRunSync().isRight)
    assert(reserveEx3U2.unsafeRunSync().isRight)
  }

  it should "not allow users to reserve exoplanets already reserved by other users" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx2U1 = reservationService.reserveExoplanet(realExoplanetName2, validUsername1, reservationDuration)
    val reserveEx1U2 = reservationService.reserveExoplanet(realExoplanetName1, validUsername2, reservationDuration)
    val reserveEx2U2 = reservationService.reserveExoplanet(realExoplanetName2, validUsername2, reservationDuration)

    reserveEx1U1.unsafeRunSync()
    reserveEx2U1.unsafeRunSync()

    assert(reserveEx1U2.unsafeRunSync().isLeft)
    assert(reserveEx2U2.unsafeRunSync().isLeft)
  }

  it should "let users to reserve previously reserved exoplanets by any users, which reservations are timeout" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    val oneSecondReservation = 1.second

    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, oneSecondReservation)
    val reserveEx2U2 = reservationService.reserveExoplanet(realExoplanetName2, validUsername2, oneSecondReservation)
    val reserveEx1U2 = reservationService.reserveExoplanet(realExoplanetName1, validUsername2, oneSecondReservation)

    reserveEx1U1.unsafeRunSync()
    reserveEx2U2.unsafeRunSync()
    IO.sleep(1.second).unsafeRunSync()

    assert(reserveEx1U2.unsafeRunSync().isRight)
    assert(reserveEx2U2.unsafeRunSync().isRight)
  }

  it should "not allow users to reserve already purchased exoplanets" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(Some(purEx1U1))
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(Some(purEx2U2))

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx1U2 = reservationService.reserveExoplanet(realExoplanetName1, validUsername2, reservationDuration)
    val reserveEx2U1 = reservationService.reserveExoplanet(realExoplanetName2, validUsername1, reservationDuration)

    assert(reserveEx1U1.unsafeRunSync().isLeft)
    assert(reserveEx1U2.unsafeRunSync().isLeft)
    assert(reserveEx2U1.unsafeRunSync().isLeft)
  }

  it should "successfully verify and extend valid exoplanet reservations for the users, which reserved it earlier" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(None)

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx2U1 = reservationService.reserveExoplanet(realExoplanetName2, validUsername1, reservationDuration)
    val reserveEx3U2 = reservationService.reserveExoplanet(realExoplanetName3, validUsername2, reservationDuration)

    val verify_Ex1U1  = reservationService.verifyAndExtendReservation(realExoplanetName1, validUsername1, reservationDuration)
    val verify_Ex2U1  = reservationService.verifyAndExtendReservation(realExoplanetName2, validUsername1, reservationDuration)
    val verify_Ex3U2  = reservationService.verifyAndExtendReservation(realExoplanetName3, validUsername2, reservationDuration)

    reserveEx1U1.unsafeRunSync()
    reserveEx2U1.unsafeRunSync()
    reserveEx3U2.unsafeRunSync()

    assert(verify_Ex1U1.unsafeRunSync().isRight)
    assert(verify_Ex2U1.unsafeRunSync().isRight)
    assert(verify_Ex3U2.unsafeRunSync().isRight)
  }

  it should "fail exoplanet reservation verification for users that not reserved particular exoplanet" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(None)

    val reserveEx2U1 = reservationService.reserveExoplanet(realExoplanetName2, validUsername1, reservationDuration)

    val verify_Ex1U1  = reservationService.verifyAndExtendReservation(realExoplanetName1, validUsername1, reservationDuration)
    val verify_Ex2U2  = reservationService.verifyAndExtendReservation(realExoplanetName2, validUsername2, reservationDuration)
    val verify_Ex3U2  = reservationService.verifyAndExtendReservation(realExoplanetName3, validUsername2, reservationDuration)

    reserveEx2U1.unsafeRunSync()

    assert(verify_Ex1U1.unsafeRunSync().isLeft)
    assert(verify_Ex2U2.unsafeRunSync().isLeft)
    assert(verify_Ex3U2.unsafeRunSync().isLeft)
  }

  it should "fail exoplanet reservation verification if the reservation is timeout" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    val oneSecondReservation = 1.second

    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(None)

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, oneSecondReservation)
    val reserveEx2U1 = reservationService.reserveExoplanet(realExoplanetName2, validUsername1, oneSecondReservation)
    val reserveEx3U2 = reservationService.reserveExoplanet(realExoplanetName3, validUsername2, oneSecondReservation)

    val verify_Ex1U1  = reservationService.verifyAndExtendReservation(realExoplanetName1, validUsername1, reservationDuration)
    val verify_Ex2U1  = reservationService.verifyAndExtendReservation(realExoplanetName2, validUsername1, reservationDuration)
    val verify_Ex3U2  = reservationService.verifyAndExtendReservation(realExoplanetName3, validUsername2, reservationDuration)

    reserveEx1U1.unsafeRunSync()
    reserveEx2U1.unsafeRunSync()
    reserveEx3U2.unsafeRunSync()
    IO.sleep(1.second).unsafeRunSync()

    assert(verify_Ex1U1.unsafeRunSync().isLeft)
    assert(verify_Ex2U1.unsafeRunSync().isLeft)
    assert(verify_Ex3U2.unsafeRunSync().isLeft)
  }

  it should "release reservation by the same user" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(None)

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx2U1 = reservationService.reserveExoplanet(realExoplanetName2, validUsername1, reservationDuration)
    val reserveEx3U2 = reservationService.reserveExoplanet(realExoplanetName3, validUsername2, reservationDuration)

    val releaseEx1U1 = reservationService.releaseReservation(realExoplanetName1, validUsername1)
    val releaseEx2U1 = reservationService.releaseReservation(realExoplanetName2, validUsername1)
    val releaseEx3U2 = reservationService.releaseReservation(realExoplanetName3, validUsername2)

    reserveEx1U1.unsafeRunSync()
    reserveEx2U1.unsafeRunSync()
    reserveEx3U2.unsafeRunSync()

    assert(releaseEx1U1.unsafeRunSync().isRight)
    assert(releaseEx2U1.unsafeRunSync().isRight)
    assert(releaseEx3U2.unsafeRunSync().isRight)
  }

  it should "not allow to release reservation by another user" in {
    val reservationService = new ReservationService[IO](
      exoplanetRepositoryStub,
      purchaseRepositoryStub,
      Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
    )
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(None)

    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx2U2 = reservationService.reserveExoplanet(realExoplanetName2, validUsername2, reservationDuration)

    val releaseEx1U2 = reservationService.releaseReservation(realExoplanetName1, validUsername2)
    val releaseEx2U1 = reservationService.releaseReservation(realExoplanetName2, validUsername1)

    reserveEx1U1.unsafeRunSync()
    reserveEx2U2.unsafeRunSync()

    assert(releaseEx1U2.unsafeRunSync().isLeft)
    assert(releaseEx2U1.unsafeRunSync().isLeft)
  }

}
