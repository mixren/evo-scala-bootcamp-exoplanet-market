package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.Async
import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.Exoplanet
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client

object ExoplanetsCalls {

  def apply[F[_]: Async](client: Client[F],
                         uri: Uri,
                        ): Kleisli[OptionT[F, *], List[String], String] =
    Kleisli[OptionT[F, *], List[String], String] {
      case "all" :: Nil                                            =>
        OptionT.liftF {
          val target = uri / "exoplanets" / "all"
          client.expect[List[Exoplanet]](target).map(_.asJson.toString())

        }

      case "random" :: amount :: Nil                                            =>
        OptionT.liftF {
          val target = uri / "exoplanets" / "random" / amount
          client.expect[List[Exoplanet]](target).map(_.asJson.toString())

        }

      case _                                                                 =>
        OptionT.none
    }
}
