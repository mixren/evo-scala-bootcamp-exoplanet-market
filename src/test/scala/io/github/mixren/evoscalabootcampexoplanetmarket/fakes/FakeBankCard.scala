package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.{BankCard, CardCvc, CardExpiration, CardHolderName, CardNumber}

import java.time.YearMonth
import java.time.format.DateTimeFormatter

object FakeBankCard {
  val fakeBankCard: BankCard = BankCard(
    CardHolderName("Bake Baker"),
    CardNumber("1111222233334444"),
    CardExpiration(YearMonth.parse("2023-12", DateTimeFormatter.ofPattern("uuuu-MM"))),
    CardCvc("123")
  )
}
