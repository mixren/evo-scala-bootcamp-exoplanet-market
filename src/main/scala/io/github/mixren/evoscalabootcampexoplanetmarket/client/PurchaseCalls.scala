package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.{Async, Ref}
import cats.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.client.ListFormatter.listFormatterOps
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{Exoplanet, ExoplanetOfficialName}
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain._
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import org.http4s.Method.{GET, POST}
import org.http4s.client.Client
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Request, Uri}
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import io.circe.syntax.EncoderOps


object PurchaseCalls {

  def removeQuotes(str: String): String = str.stripPrefix("\"").stripSuffix("\"")

  def apply[F[_]: Async: ListFormatter](client: Client[F],
                                        uri: Uri,
                                        ref :Ref[F, Option[JWToken]]
                                      ): Kleisli[OptionT[F, *], List[String], String] =
    Kleisli[OptionT[F, *], List[String], String] {
      case "auth" :: "reserve" :: "exoplanet" :: exoplanetRaw                                            =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "reserve" / "exoplanet"
          val args = exoplanetRaw.removeColons
          val body = ExoplanetOfficialName(args.head)
          ref.get.flatMap{
            case None         => Async[F].pure("Not valid token. Login first.")
            case Some(token)  => client.run(Request[F](POST, target)
              .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
              .withEntity(body)
            ).use (_.bodyText.compile.string)
          }
        }

      // TODO Correct this call. I don't like that the validation errors are not shown because of ExoOldNewCardRequest.of(args)
        // : Leo ssa b : very my name : Manny Hugo : {cardNumber} {cardExpiration} {cardCvc}
      case "auth" :: "exoplanet" :: exoOldNewCardRequest  =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "exoplanet"
          val args = exoOldNewCardRequest.removeColons
          val bodyO = ExoOldNewCardRequest.of(args)
          bodyO match {
            case None       => Async[F].pure("Some parameters are not valid.")
            case Some(body) => ref.get.flatMap{
              case None         => Async[F].pure("Not valid token. Login first.")
              case Some(token)  => client.run(Request[F](POST, target)
                .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
                .withEntity(body)
              ).use (_.bodyText.compile.string)
            }
          }
        }

      case "auth" :: "history" :: "user" :: Nil                                            =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "history" / "user"
          ref.get.flatMap{
            case None         => Async[F].pure("Not valid token. Login first.")
            case Some(token)  => client.expect[List[Exoplanet]](Request[F](GET, target)
              .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
            ).map(_.asJson.toString())
          }
        }

      case _                                                                 =>
        OptionT.none
    }
}
