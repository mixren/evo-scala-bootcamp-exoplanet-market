package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain.Purchase

class PurchaseService[F[_]](repo: PurchaseRepository[F]) {

  def savePurchase(purchase: Purchase): F[Int] ={
    repo.addPurchase(purchase)
  }

}
