package io.github.mixren.evoscalabootcampexoplanetmarket

import scala.util.control.NoStackTrace


sealed abstract class ReservationError(message: String) extends Exception(message) with NoStackTrace
case class NoReservationError(message: String) extends ReservationError(message)
