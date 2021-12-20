package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.kernel.{Async, Ref}
import cats.implicits._
import MapReservations.MapReservations
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import scala.concurrent.duration.FiniteDuration


/*
sealed trait ReservationResult
object ReservationResult {
  case class SpecificError(msg: String) extends Error
}
*/

class ReservationService[F[_]: Async](repo: ExoplanetRepository[F], reservedExoplanets: Ref[F, MapReservations]) {

  private def success(exoplanetName: ExoplanetOfficialName, username: UserName, duration: FiniteDuration): Either[String, String] =
    s"Reservation successful. ${exoplanetName.name} is reserved by ${username.value} for ${duration.toCoarsest.toString()}.".asRight

  private def failure(exoplanetName: ExoplanetOfficialName): Either[String, String] =
    s"Reservation failed. ${exoplanetName.name} is reserved by another user.".asLeft

  private def noEntry(exoplanetName: ExoplanetOfficialName): Either[String, String] =
    s"Reservation failed. Exoplanet ${exoplanetName.name} doesn't exist.".asLeft

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
   *  Is reserved only if the exoplanet exists and not reserved for another user at the moment
   */
  def reserveExoplanet(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, String]] = {
    for {
      opt <- repo.exoplanetByName(exoplanetName)
      res   <- opt match{
        case Some(_)  => reserve(exoplanetName, username, reservationDuration)
        case None     => Async[F].delay(noEntry(exoplanetName))
      }
    } yield res
  }


  /**
   *  Verify reservation.
   *  Throws custom NoReservation error if no reservation. Otherwise extends reservation by the given amount.
   */
  def verifyReservation(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, String]] =
    reservedExoplanets.modify{ state =>
      state.get(exoplanetName) match {
        case Some((sameUsername, deadline)) if (sameUsername equals username) && deadline.hasTimeLeft() =>
          (state.updated(exoplanetName, (username, reservationDuration.fromNow)), "Reservation verified".asRight)
        case _                                                                                          =>
          (state, s"No $exoplanetName reservation for $username".asLeft)
      }
    }

  /**
   *  Release exoplanet reservation by user.
   *  No release if the reservation is made by another user.
   */
  def releaseReservation(exoplanetName: ExoplanetOfficialName, username: UserName): F[Unit] =
    reservedExoplanets.modify{ state =>
      state.get(exoplanetName) match {
        case Some((sameUsername, _)) if sameUsername equals username  =>
          (state.removed(exoplanetName), ())
        case _                                                        =>
          (state, ())
      }
    }

}
