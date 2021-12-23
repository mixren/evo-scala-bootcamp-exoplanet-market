package io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain

import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName

case class PurchaseSuccess(msg: String) extends AnyVal
object PurchaseSuccess{
  def of(username: UserName, exoplanetName: ExoplanetOfficialName, exoplanetNewName: ExoplanetNewName): PurchaseSuccess ={
    PurchaseSuccess(s"""$username has successfully named the exoplanet \"$exoplanetName\" as \"$exoplanetNewName\".""")
  }
}
