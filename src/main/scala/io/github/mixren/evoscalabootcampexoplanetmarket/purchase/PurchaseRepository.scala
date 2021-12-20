package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.Async
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.update.Update
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.Purchase

class PurchaseRepository[F[_]: Async](implicit xa: HikariTransactor[F]) {
  def addPurchase(purchase: Purchase): F[Int] = {
    val sql =
      """
        |INSERT INTO purchases(
        |  exoplanet_official_name, exoplanet_bought_name, username, price, timestamp)
        |  values (?, ?, ?, ?, ?)
        |  """.stripMargin
    Update[Purchase](sql)
      .toUpdate0(purchase)
      .run
      .transact(xa)
  }

}
