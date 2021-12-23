package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxApply, catsSyntaxEitherId}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.PurchaseRepositoryT
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.Purchase
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

import scala.collection.mutable.ListBuffer

class FakePurchaseRepository[F[_]: Async] extends PurchaseRepositoryT[F] {
  var purchases: ListBuffer[Purchase] = new ListBuffer[Purchase]()

  override def addPurchase(purchase: Purchase): F[Either[String, Int]] =
    Async[F].delay(purchases += purchase) *> Async[F].pure(0.asRight[String])

  override def purchaseByExoOfficialName(name: ExoplanetOfficialName): F[Option[Purchase]] =
    Async[F].pure(purchases.find(_.officialName equals name))

  override def purchasesByUser(username: UserName): F[List[Purchase]] =
    Async[F].pure(purchases.filter(_.purchaserUserName equals username).toList)
}
