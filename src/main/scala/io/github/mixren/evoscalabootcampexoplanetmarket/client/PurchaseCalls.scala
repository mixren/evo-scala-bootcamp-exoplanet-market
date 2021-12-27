package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.{Async, Ref}
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import org.http4s.Uri
import org.http4s.client.Client

object PurchaseCalls {

  // TODO finish here
  def apply[F[_]: Async](client: Client[F],
                         uri: Uri,
                         ref :Ref[F, Option[JWToken]]
                        ): Kleisli[OptionT[F, *], List[String], String] =
    Kleisli[OptionT[F, *], List[String], String] {
      case "auth" :: "reserve" :: "exoplanet" :: Nil                                            =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "reserve" / "exoplanet"
          ref.equals( "ss")
          client.expect[String](target)
        }

      case "auth" :: "exoplanet" :: Nil                                            =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "exoplanet"
          client.expect[String](target)
        }

      case "auth" :: "history" :: "user" :: Nil                                            =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "history" / "user"
          client.expect[String](target)
        }

      case _                                                                 =>
        OptionT.none
    }
}
