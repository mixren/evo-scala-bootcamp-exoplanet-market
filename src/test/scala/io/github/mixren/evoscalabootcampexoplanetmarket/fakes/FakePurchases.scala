package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetNewName
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeExoplanets._
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeUsers._
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.{Purchase, PurchasePrice}

import java.time.Instant

object FakePurchases {
  val newExoName1: ExoplanetNewName = ExoplanetNewName("new name 1")
  val newExoName2: ExoplanetNewName = ExoplanetNewName("new name 2")
  val newExoName3: ExoplanetNewName = ExoplanetNewName("new name 2")

  val purchasePrice: PurchasePrice = PurchasePrice(BigDecimal(4.99))

  val purEx1U1: Purchase = Purchase(realExoplanetName1, newExoName1, validUsername1, purchasePrice, Instant.now().toEpochMilli)
  val purEx2U2: Purchase = Purchase(realExoplanetName2, newExoName2, validUsername2, purchasePrice, Instant.now().toEpochMilli)
  val purEx3U3: Purchase = Purchase(realExoplanetName3, newExoName3, validUsername3, purchasePrice, Instant.now().toEpochMilli)


}
