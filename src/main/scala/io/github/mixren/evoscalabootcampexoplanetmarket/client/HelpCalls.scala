package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.Async

object HelpCalls {

  def apply[F[_]: Async](): Kleisli[OptionT[F, *], List[String], String] =
    Kleisli[OptionT[F, *], List[String], String] {
      case Nil                                            =>
        OptionT.some {
          """===========================================================================================================
            |All commands:
            |0) help                                    - show this all commands info
            |1) exoplanets all                          - show all exoplanets
            |2) exoplanets random {amount}              - show that much random exoplanets
            |3) user register {username} {password}     - register unregistered user
            |4) user login {username} {password}        - login with registered user
            |5) user auth loggedin                      - check if the user is logged in
            |6) purchase auth reserve exoplanet..TODO   - reserve an exoplanet by an authenticated user before the purchase
            |7) purchase auth exoplanet..TODO           - purchase the reserved exoplanet by an authenticated user
            |8) purchase auth history user..TODO        - check all purchases by the authenticated user
            |===========================================================================================================
            |""".stripMargin
        }

      case _                                                                 =>
        OptionT.none
    }
}
