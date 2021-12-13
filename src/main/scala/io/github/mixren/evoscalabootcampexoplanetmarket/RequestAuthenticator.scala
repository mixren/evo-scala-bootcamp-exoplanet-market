package io.github.mixren.evoscalabootcampexoplanetmarket

import cats._, cats.effect._, cats.implicits._, cats.data._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.{AuthedRoutes, Header, HttpRoutes, Request}

import io.github.mixren.evoscalabootcampexoplanetmarket.user.User
import org.http4s.headers.Authorization

class RequestAuthenticator[F[_]: Async] {

  // TODO modify (doesnt work now)
  private val authUser = Kleisli[F, Request[F], Either[String,User]] { req =>
    val message = for {
      header <- req.headers.get(Authorization).toRight("Couldn't find an Authorization header")
      token <- crypto.validateSignedToken(header.value).toRight("Invalid token")
      message <- Either.catchOnly[NumberFormatException](token.toLong).leftMap(_.toString)
    } yield message
    message.traverse(retrieveUser.run)

  }

  val onFailure: AuthedRoutes[String, F] = Kleisli(req => OptionT.liftF(Forbidden(req.authInfo)))
  private val middleware = AuthMiddleware(authUser, onFailure)

  def apply(authedService: AuthedRoutes[User, F]): HttpRoutes[F] = middleware(authedService)

}
