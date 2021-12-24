package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.IO
import cats.effect.kernel.Ref
import cats.effect.unsafe.implicits.global
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepositoryT
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeExoplanets._
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakePurchases.purEx3U3
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeUsers._
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration.{DurationInt, FiniteDuration}

class ReservationServiceTest extends AnyFlatSpec with MockFactory{

  val exoplanetRepositoryStub: ExoplanetRepositoryT[IO] = stub[ExoplanetRepositoryT[IO]]
  val purchaseRepositoryStub: PurchaseRepositoryT[IO]   = stub[PurchaseRepositoryT[IO]]

  val reservationService2 = new ReservationService[IO](
    exoplanetRepositoryStub,
    purchaseRepositoryStub,
    Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
  )

  val reservationDuration: FiniteDuration = 5.seconds


  "ReservationService2" should "let users reserve exoplanets not reserved by other users and not purchased" in {
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName1 returns IO.pure(Some(exo1))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName2 returns IO.pure(Some(exo2))
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName1 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName2 returns IO.pure(None)
    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(Some(purEx3U3))

    val reserveEx1U1 = reservationService2.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx1U2 = reservationService2.reserveExoplanet(realExoplanetName1, validUsername2, reservationDuration)
    val reserveEx2U2 = reservationService2.reserveExoplanet(realExoplanetName2, validUsername2, reservationDuration)
    val reserveEx3U1 = reservationService2.reserveExoplanet(realExoplanetName3, validUsername1, reservationDuration)

    assert(reserveEx1U1.unsafeRunSync().isRight)
    assert(reserveEx1U1.unsafeRunSync().isRight)
    assert(reserveEx1U2.unsafeRunSync().isLeft)
    assert(reserveEx2U2.unsafeRunSync().isRight)
    assert(reserveEx3U1.unsafeRunSync().isLeft)
  }

  it should "verify reservations and, if successful, extend them" in {
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName3 returns IO.pure(Some(exo3))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName3 returns IO.pure(None)

    val reserveEx3U3  = reservationService2.reserveExoplanet(          realExoplanetName3, validUsername3, reservationDuration)
    val verify_Ex3U3  = reservationService2.verifyAndExtendReservation(realExoplanetName3, validUsername3, reservationDuration)
    val verify_Ex3U1  = reservationService2.verifyAndExtendReservation(realExoplanetName3, validUsername1, reservationDuration)

    assert(verify_Ex3U3.unsafeRunSync().isLeft)
    assert(reserveEx3U3.unsafeRunSync().isRight)
    assert(verify_Ex3U3.unsafeRunSync().isRight)
    assert(verify_Ex3U1.unsafeRunSync().isLeft)
  }

  it should "release reservation by the same user" in {
    exoplanetRepositoryStub.exoplanetByName _ when realExoplanetName4 returns IO.pure(Some(exo4))

    purchaseRepositoryStub.purchaseByExoOfficialName _ when realExoplanetName4 returns IO.pure(None)

    val reserveEx4U4 = reservationService2.reserveExoplanet(  realExoplanetName4, validUsername4, reservationDuration)
    val releaseEx4U3 = reservationService2.releaseReservation(realExoplanetName4, validUsername3)
    val releaseEx4U4 = reservationService2.releaseReservation(realExoplanetName4, validUsername4)

    assert(reserveEx4U4.unsafeRunSync().isRight)
    assert(releaseEx4U3.unsafeRunSync().isLeft)
    assert(releaseEx4U4.unsafeRunSync().isRight)
  }

/*
  // Working shit
  val reservationService = new ReservationService[IO](
    new FakeExoplanetRepository[IO](),
    new FakePurchaseRepository[IO](),
    Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
  )



  "ReservationService" should "let users reserve exoplanets not reserved by other users" in {
    val reserveEx1U1 = reservationService.reserveExoplanet(realExoplanetName1, validUsername1, reservationDuration)
    val reserveEx1U2 = reservationService.reserveExoplanet(realExoplanetName1, validUsername2, reservationDuration)
    val reserveEx2U2 = reservationService.reserveExoplanet(realExoplanetName2, validUsername2, reservationDuration)
    assert(reserveEx1U1.unsafeRunSync().isRight)
    assert(reserveEx1U1.unsafeRunSync().isRight)
    assert(reserveEx1U2.unsafeRunSync().isLeft)
    assert(reserveEx2U2.unsafeRunSync().isRight)
  }

  it should "verify reservations and, if successful, extend them" in {
    val reserveEx3U3  = reservationService.reserveExoplanet(          realExoplanetName3, validUsername3, reservationDuration)
    val verify_Ex3U3  = reservationService.verifyAndExtendReservation(realExoplanetName3, validUsername3, reservationDuration)
    val verify_Ex3U1  = reservationService.verifyAndExtendReservation(realExoplanetName3, validUsername1, reservationDuration)
    assert(verify_Ex3U3.unsafeRunSync().isLeft)
    assert(reserveEx3U3.unsafeRunSync().isRight)
    assert(verify_Ex3U3.unsafeRunSync().isRight)
    assert(verify_Ex3U1.unsafeRunSync().isLeft)
  }

  it should "release reservation by the same user" in {
    val reserveEx4U4 = reservationService.reserveExoplanet(  realExoplanetName4, validUsername4, reservationDuration)
    val releaseEx4U3 = reservationService.releaseReservation(realExoplanetName4, validUsername3)
    val releaseEx4U4 = reservationService.releaseReservation(realExoplanetName4, validUsername4)
    assert(reserveEx4U4.unsafeRunSync().isRight)
    assert(releaseEx4U3.unsafeRunSync().isLeft)
    assert(releaseEx4U4.unsafeRunSync().isRight)
  }
*/

}
