package io.github.mixren.evoscalabootcampexoplanetmarket.client.calls

import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.{Async, Ref}
import cats.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain._
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import org.http4s.Method.{GET, POST}
import org.http4s.client.Client
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Request, Response, Uri}

/**
 * User routes calls.
 * These routes are located in the Server's UserRoutes file.
 */
object UserCalls {

  def getToken[F[_]](response: Response[F]): Option[JWToken] = response.headers.get[Authorization] match {
    case Some(Authorization(Credentials.Token(AuthScheme.Bearer, token))) => Some(JWToken(token))
    case _ => None
  }

  def apply[F[_] : Async](client: Client[F],
                          uri: Uri,
                          tokenRef: Ref[F, Option[JWToken]]
                         ): Kleisli[OptionT[F, *], List[String], String] =

    Kleisli[OptionT[F, *], List[String], String] {
      case "login" :: username :: password :: Nil =>
        OptionT.liftF {
          val target = uri / "user" / "login"
          val body = AuthRequest(UserName(username), AuthPassword(password))
          val req = Request[F](POST, target).withEntity(body)
          client.run(req).use { resp =>
            tokenRef.set(getToken(resp)) *> resp.bodyText.compile.string
          }
        }

      case "register" :: username :: password :: Nil =>
        OptionT.liftF {
          val target = uri / "user" / "register"
          val body = AuthRequest(UserName(username), AuthPassword(password))
          val req = Request[F](POST, target).withEntity(body)
          client.run(req).use { resp =>
            tokenRef.set(getToken(resp)) *> resp.bodyText.compile.string
          }
        }

      case "auth" :: "loggedin" :: Nil =>
        OptionT.liftF {
          val target = uri / "user" / "auth" / "loggedin"
          tokenRef.get.flatMap {
            case None => Async[F].pure("Not valid token. Login first.")
            case Some(token) => client.expect[String](Request[F](GET, target).withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value))))
          }
        }

      case _ =>
        OptionT.none
    }


}
