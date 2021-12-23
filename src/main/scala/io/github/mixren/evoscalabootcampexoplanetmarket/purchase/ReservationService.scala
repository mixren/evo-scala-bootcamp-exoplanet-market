package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.kernel.{Async, Ref}
import cats.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepositoryT
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import scala.concurrent.duration.FiniteDuration


/*
sealed trait ReservationResult
object ReservationResult {
  case class SpecificError(msg: String) extends Error
}
*/
trait ReservationServiceT[F[_]] {
  def reserveExoplanet(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, String]]
  def verifyAndExtendReservation(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, Unit]]
  def releaseReservation(exoplanetName: ExoplanetOfficialName, username: UserName): F[Either[String,Unit]]
}

class ReservationService[F[_]: Async](exoRepo: ExoplanetRepositoryT[F],
                                      purRepo: PurchaseRepositoryT[F],
                                      reservedExoplanets: Ref[F, MapReservations]) extends ReservationServiceT[F] {

  private def success(exoplanetName: ExoplanetOfficialName, username: UserName, duration: FiniteDuration): Either[String, String] =
    s"Reservation successful. ${exoplanetName.name} is reserved by ${username.value} for ${duration.toCoarsest.toString()}.".asRight

  private def failure(exoplanetName: ExoplanetOfficialName): Either[String, String] =
    s"Reservation failed. ${exoplanetName.name} is reserved by another user.".asLeft

  private def noEntry(exoplanetName: ExoplanetOfficialName): Either[String, String] =
    s"Reservation failed. Exoplanet ${exoplanetName.name} doesn't exist.".asLeft

  private def purchasedAlready(exoplanetName: ExoplanetOfficialName): Either[String, String] =
    s"Reservation failed. Exoplanet ${exoplanetName.name} is purchased already".asLeft

  private def noReservation(exoplanetName: ExoplanetOfficialName, username: UserName): Either[String, Unit] =
    s"Error. No ${exoplanetName.name} reservation for ${username.value}".asLeft

  private def noRelease(exoplanetName: ExoplanetOfficialName, username: UserName): Either[String, Unit] =
    (s"Error. Can't release ${exoplanetName.name} reservation for ${username.value}." +
      s" It might not be reserved or reserved by another user").asLeft

  private def reserve(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration)=
    reservedExoplanets.modify{ state =>
      state.get(exoplanetName) match {
        case Some((sameUsername, _)) if sameUsername equals username  =>
          (state.updated(exoplanetName, (username, reservationDuration.fromNow)), success(exoplanetName, username, reservationDuration))
        case None                                                     =>
          (state.updated(exoplanetName, (username, reservationDuration.fromNow)), success(exoplanetName, username, reservationDuration))
        case _                                                        =>
          (state, failure(exoplanetName))
      }
    }

  /**
   *  Reserve an exoplanet for a user.
   *  Is reserved only if the exoplanet exists, not purchased and not reserved for another user at the moment.
   *  Re-reservation by the same user is possible.
   */
  override def reserveExoplanet(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, String]] = {
    for {
      exoO  <- exoRepo.exoplanetByName(exoplanetName)
      purO  <- purRepo.purchaseByExoOfficialName(exoplanetName)
      res   <- (exoO.isDefined, purO.isDefined) match {
        case (true, true)  => Async[F].delay(purchasedAlready(exoplanetName))
        case (true, false) => reserve(exoplanetName, username, reservationDuration)
        case _             => Async[F].delay(noEntry(exoplanetName))
      }
    } yield res
  }


  /**
   *  Verify reservation.
   *  If successfully verified, the reservations gets extended by the given amount.
   */
  override def verifyAndExtendReservation(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, Unit]] =
    reservedExoplanets.modify{ state =>
      state.get(exoplanetName) match {
        case Some((sameUsername, deadline)) if (sameUsername equals username) && deadline.hasTimeLeft() =>
          (state.updated(exoplanetName, (username, reservationDuration.fromNow)), ().asRight)
        case _                                                                                          =>
          (state, noReservation(exoplanetName, username))
      }
    }

  /**
   *  Release exoplanet reservation by user.
   *  No release if the reservation is made by another user.
   */
  override def releaseReservation(exoplanetName: ExoplanetOfficialName, username: UserName): F[Either[String, Unit]] =
    reservedExoplanets.modify{ state =>
      state.get(exoplanetName) match {
        case Some((sameUsername, _)) if sameUsername equals username  =>
          (state.removed(exoplanetName), ().asRight)
        case _                                                        =>
          (state, noRelease(exoplanetName, username))
      }
    }

}
