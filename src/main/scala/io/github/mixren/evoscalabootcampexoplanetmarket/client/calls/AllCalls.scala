package io.github.mixren.evoscalabootcampexoplanetmarket.client.calls

import cats.Applicative
import cats.data.{Kleisli, OptionT}

object AllCalls {

  def apply[F[_] : Applicative](
                                 exoplanetsCalls: Kleisli[OptionT[F, *], List[String], String],
                                 userCalls: Kleisli[OptionT[F, *], List[String], String],
                                 purchaseCalls: Kleisli[OptionT[F, *], List[String], String],
                                 helpCalls: Kleisli[OptionT[F, *], List[String], String]
                               ): Kleisli[OptionT[F, *], List[String], String] =
    Kleisli[OptionT[F, *], List[String], String] {
      case "exoplanets" :: args => exoplanetsCalls(args)
      case "user" :: args => userCalls(args)
      case "purchase" :: args => purchaseCalls(args)
      case "help" :: args => helpCalls(args)
      case _ => OptionT.none[F, String]
    }
}
