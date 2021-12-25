package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeUsers._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{AuthPassword, AuthRequest}

object FakeAuthRequest {
  val fakeAuthRequest1: AuthRequest = AuthRequest(validUsername1, AuthPassword("123456"))
  val fakeAuthRequest2: AuthRequest = AuthRequest(validUsername2, AuthPassword("123456"))

}
