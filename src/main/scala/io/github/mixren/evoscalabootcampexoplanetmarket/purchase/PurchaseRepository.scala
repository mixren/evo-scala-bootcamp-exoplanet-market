package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.Async
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.update.Update
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.Purchase
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName


trait PurchaseRepositoryT[F[_]] {
  def addPurchase(purchase: Purchase): F[Either[String, Int]]
  def purchaseByExoOfficialName(name: ExoplanetOfficialName): F[Option[Purchase]]
  def purchasesByUser(username: UserName): F[List[Purchase]]
}

class PurchaseRepository[F[_]: Async](implicit xa: HikariTransactor[F]) extends PurchaseRepositoryT[F] {
  override def addPurchase(purchase: Purchase): F[Either[String, Int]] = {
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
      .attemptSomeSqlState(t => s"Something is wrong with fetching from db. $t")
  }

  override def purchaseByExoOfficialName(name: ExoplanetOfficialName): F[Option[Purchase]] = {
    sql"""SELECT exoplanet_official_name, exoplanet_bought_name, username, price, timestamp
          FROM purchases
          WHERE exoplanet_official_name = $name
       """
      .query[Purchase]
      .option
      .transact(xa)
  }

  override def purchasesByUser(username: UserName): F[List[Purchase]] = {
    sql"""SELECT exoplanet_official_name, exoplanet_bought_name, username, price, timestamp
          FROM purchases
          WHERE username = $username
       """
      .query[Purchase]
      .to[List]
      .transact(xa)
  }

}
