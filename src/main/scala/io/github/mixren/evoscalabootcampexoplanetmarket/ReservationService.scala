package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.kernel.{Async, Ref}
import cats.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.MapReservations.MapReservations

import scala.concurrent.duration.FiniteDuration

class ReservationService[F[_]:Async](repo: ExoplanetRepository[F], reservedExoplanets: Ref[F, MapReservations]) {

  private def success(exoplanetName: ExoplanetOfficialName, username: UserName, duration: FiniteDuration): Right[String, String] =
    Right[String,String](s"Reservation successful. ${exoplanetName.name} is reserved by ${username.value} for ${duration.toCoarsest.toString()}.")

  private def failure(exoplanetName: ExoplanetOfficialName): Left[String, String] =
    Left[String,String](s"Reservation failed. ${exoplanetName.name} is reserved by another user.")

  private def noEntry(exoplanetName: ExoplanetOfficialName): Left[String, String] =
    Left[String,String](s"Reservation failed. Exoplanet ${exoplanetName.name} doesn't exist.")

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

  sealed trait ReservationError extends Exception
  case class NoReservation(exoplanetName: ExoplanetOfficialName, username: UserName) extends ReservationError

  /**
   *  Throws custom NoReservation error if no reservation. Otherwise extends reservation by the given amount.
   */
  def verifyReservation(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Unit] =
    reservedExoplanets.modify{ state =>
      state.get(exoplanetName) match {
        case Some((sameUsername, _)) if sameUsername equals username  =>
          (state.updated(exoplanetName, (username, reservationDuration.fromNow)), ())
        case _                                                        =>
          (state, throw NoReservation(exoplanetName, username))
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
