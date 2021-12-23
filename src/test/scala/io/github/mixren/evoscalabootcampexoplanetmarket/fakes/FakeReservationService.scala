package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import cats.effect.Async
import cats.implicits.catsSyntaxEitherId
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.ReservationServiceT
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import scala.concurrent.duration.FiniteDuration

class FakeReservationService[F[_]: Async] extends ReservationServiceT[F]{
  override def reserveExoplanet(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, String]] =
    Async[F].pure("success".asRight)

  override def verifyAndExtendReservation(exoplanetName: ExoplanetOfficialName, username: UserName, reservationDuration: FiniteDuration): F[Either[String, Unit]] =
    Async[F].pure(().asRight)

  override def releaseReservation(exoplanetName: ExoplanetOfficialName, username: UserName): F[Either[String, Unit]] =
    Async[F].pure(().asRight)
}
