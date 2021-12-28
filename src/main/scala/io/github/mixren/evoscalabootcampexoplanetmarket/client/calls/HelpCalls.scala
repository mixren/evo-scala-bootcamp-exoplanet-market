package io.github.mixren.evoscalabootcampexoplanetmarket.client.calls

import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.Async

object HelpCalls {

  def apply[F[_] : Async](): Kleisli[OptionT[F, *], List[String], String] =
    Kleisli[OptionT[F, *], List[String], String] {
      case Nil =>
        OptionT.some {
          """===========================================================================================================
            |All commands:
            |0) help                                          - show this all commands info
            |1) exoplanets all                                - show all exoplanets
            |2) exoplanets random {amount}                    - show that much random exoplanets
            |3) user register {username} {password}           - register unregistered user
            |4) user login {username} {password}              - login with registered user
            |5) user auth loggedin                            - check if the user is logged in
            |6) purchase auth reserve exoplanet : {exoplanet} - reserve an exoplanet by an authenticated user before the purchase
            |7) purchase auth exoplanet : {exoplanetOldName} : {exoNewName} : {cardholderName} : {cardNumber} : {cardExpiration} : {cardCvc}
            |                                                 - purchase the reserved exoplanet by an authenticated user (Card Expiration ex.: 2023-12)
            |8) purchase auth history user                    - check all purchases by the authenticated user
            |9) purchase history all                          - show all purchases by all users
            |===========================================================================================================
            |""".stripMargin
        }

      case _ =>
        OptionT.none
    }
}
