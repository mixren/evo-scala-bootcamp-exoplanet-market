package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.kernel.{Async, Ref}
import cats.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.MapReservations.MapReservations

import scala.concurrent.duration.FiniteDuration

class ReservationService[F[_]:Async](repo: ExoplanetRepository[F], reservedExoplanets: Ref[F, MapReservations]) {

  def success(exoplanetName: ExoplanetOfficialName, username: UserName, duration: FiniteDuration): Right[String, String] =
    Right[String,String](s"Reservation successful. ${exoplanetName.name} is reserved by ${username.value} for ${duration.toCoarsest.toString()}.")

  def failure(exoplanetName: ExoplanetOfficialName): Left[String, String] =
    Left[String,String](s"Reservation failed. ${exoplanetName.name} is reserved by another user.")

  def noEntry(exoplanetName: ExoplanetOfficialName): Left[String, String] =
    Left[String,String](s"Reservation failed. Exoplanet ${exoplanetName.name} doesn't exist.")

  def reserve(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration)=
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

  def reserveExoplanet(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, String]] = {
    for {
      opt <- repo.exoplanetByName(exoplanetName)
      res   <- opt match{
        case Some(_)  => reserve(exoplanetName, username, reservationDuration)
        case None     => Async[F].delay(noEntry(exoplanetName))
      }
    } yield res

    /*reservedExoplanets.modify{ state =>
      state.get(exoplanetName) match {
        case Some((sameUsername, _)) if sameUsername equals username  =>
          (state.updated(exoplanetName, (username, reservationDuration.fromNow)), true)
        case None                                                     =>
          (state.updated(exoplanetName, (username, reservationDuration.fromNow)), true)
        case _                                                        =>
          (state, false)
      }
    }*/
  }

}
