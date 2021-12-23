package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.IO
import cats.effect.kernel.{Async, Ref}
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxEitherId
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepositoryT
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{Dec, Distance, Exoplanet, ExoplanetOfficialName, Mass, Ra, Radius, Year}
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.Purchase
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration.DurationInt

class ReservationServiceTest extends AnyFlatSpec{

  val reservationService = new ReservationService[IO](
    new TestExoplanetRepository[IO](),
    new TestPurchaseRepository[IO](),
    Ref.of[IO, MapReservations](Map.empty).unsafeRunSync()
  )

  val realExoplanetName1   = ExoplanetOfficialName("2I/Borisov")
  val realExoplanetName2   = ExoplanetOfficialName("1RXS 1609 b")
  val realExoplanetName3   = ExoplanetOfficialName("1SWASP J1407 b")
  val realExoplanetName4   = ExoplanetOfficialName("16 Cyg B b")

  val validUsername1       = UserName("Jax")
  val validUsername2       = UserName("Allah")
  val validUsername3       = UserName("Jin")
  val validUsername4       = UserName("Vasya")
  val reservationDuration  = 5.seconds


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

  private class TestExoplanetRepository[F[_]: Async] extends ExoplanetRepositoryT[F] {
    // Real are only Exoplanet names
    val exo1 = Exoplanet(1, ExoplanetOfficialName("2I/Borisov"), Some(Mass(13)), None, None, Some(Ra(200.347819)), Some(Dec(9.134452)), Some(Year(2011)))
    val exo2 = Exoplanet(2, ExoplanetOfficialName("1RXS 1609 b"), None, Some(Radius(0.77)), None, Some(Ra(136.674787)), Some(Dec(19.9390135139)), Some(Year(2020)))
    val exo3 = Exoplanet(3, ExoplanetOfficialName("1SWASP J1407 b"), None, None, Some(Distance(60.2)), Some(Ra(80.9473181)), Some(Dec(5.7486729294)), Some(Year(2001)))
    val exo4 = Exoplanet(4, ExoplanetOfficialName("16 Cyg B b"), Some(Mass(6)), None, Some(Distance(124.8)), Some(Ra(54.56542)), Some(Dec(11.84532507)), Some(Year(1994)))
    val exoList = List(exo1, exo2, exo3, exo4)

    override def insertExoplanets(exps: List[Exoplanet]): F[Int] = Async[F].pure(0)

    override def fetchAllExoplanets: F[List[Exoplanet]] = Async[F].pure(exoList)

    override def deleteAllExoplanets(): F[Int] = Async[F].pure(0)

    override def exoplanetByName(exoplanetName: ExoplanetOfficialName): F[Option[Exoplanet]] =
      Async[F].delay(exoList.find(_.officialName equals exoplanetName))
  }

  private class TestPurchaseRepository[F[_]: Async] extends PurchaseRepositoryT[F] {
    override def addPurchase(purchase: Purchase): F[Either[String, Int]] = Async[F].pure(0.asRight[String])

    override def purchaseByExoOfficialName(name: ExoplanetOfficialName): F[Option[Purchase]] = Async[F].pure(None)

    override def purchaseByUser(username: UserName): F[List[Purchase]] = Async[F].pure(List[Purchase]())
  }
}
