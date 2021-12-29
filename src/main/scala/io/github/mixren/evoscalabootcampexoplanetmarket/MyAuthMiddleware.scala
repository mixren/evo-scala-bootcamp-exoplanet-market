package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.data.{Kleisli, OptionT}
import cats.effect.Async
import cats.syntax.applicative._
import cats.syntax.either._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.AuthUser
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JwtHelper._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server._

class MyAuthMiddleware[F[_]: Async] {

  val dsl = new Http4sDsl[F] {}
  import dsl._

  // Since we dont group the users, only username is passed in the AuthUser
  private val authUser = Kleisli[F, Request[F], Either[String, AuthUser]] { req => {
    for {
      token     <- getToken(req)
      jwt       <- tokenDecode(token)
      username  <- verifyJwtClaims(jwt)
    } yield username
  }.pure[F]
  }

  // Bearer token is used here. Basic auth is not used because it's not user and password what is passed
  private def getToken(request: Request[F]) = request.headers.get[Authorization] match {
    case Some(Authorization(Credentials.Token(AuthScheme.Bearer, token))) => JWToken(token).asRight[String]
    case Some(_)  => "Invalid auth header".asLeft[JWToken]
    case None     => "Couldn't find an Authorization header".asLeft[JWToken]
  }


  val onFailure: AuthedRoutes[String, F] = Kleisli(req => OptionT.liftF(Forbidden(req.context)))
  private val middleware: AuthMiddleware[F, AuthUser] = AuthMiddleware(authUser, onFailure)

  def apply(authedService: AuthedRoutes[AuthUser, F]): HttpRoutes[F] = middleware(authedService)

}
