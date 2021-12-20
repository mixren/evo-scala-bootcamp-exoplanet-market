package io.github.mixren.evoscalabootcampexoplanetmarket

class PurchaseService[F[_]](repo: PurchaseRepository[F]) {

  def savePurchase(purchase: Purchase): F[Int] ={
    repo.addPurchase(purchase)
  }

}
