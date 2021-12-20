package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import scala.concurrent.duration.Deadline

object MapReservations {
  type MapReservations = Map[ExoplanetOfficialName, (UserName, Deadline)]
}
