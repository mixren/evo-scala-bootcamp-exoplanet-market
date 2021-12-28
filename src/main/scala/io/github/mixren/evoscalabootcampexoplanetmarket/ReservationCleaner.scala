package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.kernel.{Ref, Temporal}
import cats.syntax.flatMap._
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.MapReservations.MapReservations

import scala.concurrent.duration.FiniteDuration

/**
 * Release/clean all exoplanets reservations from memory if they are timeout.
 * Useful, because users reserve exoplanets before purchasing them the reservations are stored in memory in cats Ref.
 */
class ReservationCleaner[F[_]: Temporal](reservedExoplanets: Ref[F, MapReservations]) {
  def cleanExpired(): F[Unit] = reservedExoplanets.update(_.filter {
    case (_, (_, deadline)) => deadline.hasTimeLeft()
  })

  def delayedCleaning(duration: FiniteDuration): F[Unit] = Temporal[F].sleep(duration) >> cleanExpired()

}