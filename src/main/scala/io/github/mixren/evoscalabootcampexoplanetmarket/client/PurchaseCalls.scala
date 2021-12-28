package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.{Async, Ref}
import cats.implicits._
import io.circe.syntax.EncoderOps
import io.github.mixren.evoscalabootcampexoplanetmarket.client.ListFormatter.listFormatterOps
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.ExoplanetOfficialName
import io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain._
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import org.http4s.Method.{GET, POST}
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Request, Uri}


/**
 * Purchase routes calls.
 * These routes are located in the Server's PurchaseRoutes file.
 */
object PurchaseCalls {

  def removeQuotes(str: String): String = str.stripPrefix("\"").stripSuffix("\"")

  def apply[F[_]: Async: ListFormatter](client: Client[F],
                                        uri: Uri,
                                        tokenRef :Ref[F, Option[JWToken]]
                                      ): Kleisli[OptionT[F, *], List[String], String] =
    Kleisli[OptionT[F, *], List[String], String] {
      case "auth" :: "reserve" :: "exoplanet" :: exoplanetRaw                                            =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "reserve" / "exoplanet"
          val args = exoplanetRaw.removeColons
          val body = ExoplanetOfficialName(args.head)
          tokenRef.get.flatMap{
            case None         => Async[F].pure("Not valid token. Login first.")
            case Some(token)  => client.run(Request[F](POST, target)
              .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
              .withEntity(body)
            ).use (_.bodyText.compile.string)
          }
        }

      // : Leo ssa b : very my name : Manny Hugo : {cardNumber} : {cardExpiration} : {cardCvc}
      case "auth" :: "exoplanet" :: exoOldNewCardRequest  =>
        OptionT.liftF {
          val target = uri / "purchase" / "auth" / "exoplanet"
          val args = exoOldNewCardRequest.removeColons
          val bodyO = ExoOldNewCardRequest.novalidJsonStrOf(args)
          bodyO match {
            case None       => Async[F].pure("Error. Check if number of parameters after : is correct. Should be 6.")
            case Some(body) => tokenRef.get.flatMap{
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
          tokenRef.get.flatMap{
            case None         => Async[F].pure("Not valid token. Login first.")
            case Some(token)  => client.expect[List[Purchase]](Request[F](GET, target)
              .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
            ).map(_.asJson.toString())
          }
        }

      case _                                                                 =>
        OptionT.none
    }
}
