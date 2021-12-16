package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.data.{Kleisli, OptionT}
import cats.effect.Async
import cats.syntax.applicative._
import cats.syntax.either._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.User
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JwtHelper._
import org.http4s.headers.Authorization
import org.http4s.server._
import org.http4s._
import org.http4s.dsl.Http4sDsl

class MyAuthMiddleware[F[_]: Async] {

  val dsl = new Http4sDsl[F] {}
  import dsl._

  // TODO DO NOT ENCODE User password, password should be kept as hash and not used anywhere beside logging
  // TODO I'd suggest return UserName here only
  private val authUser = Kleisli[F, Request[F], Either[String, User]] { req => {
    for {
      token <- getToken(req)
      jwt   <- tokenDecode(JWToken(token))
      user  <- verifyJwtClaims(jwt)
    } yield user
  }.pure[F]
  }

  //TODO Keep in mind Bearer token is used here. You do not use Basic auth because it's not user and password what is passed
  private def getToken(request: Request[F]) = request.headers.get[Authorization] match {
    case Some(Authorization(Credentials.Token(AuthScheme.Bearer, token))) => token.asRight[String]
    case Some(_)  => "Invalid auth header".asLeft[String]
    case None     => "Couldn't find an Authorization header".asLeft[String]
  }


  val onFailure: AuthedRoutes[String, F] = Kleisli(req => OptionT.liftF(Forbidden(req.context)))
  private val middleware: AuthMiddleware[F, User] = AuthMiddleware(authUser, onFailure)

  def apply(authedService: AuthedRoutes[User, F]): HttpRoutes[F] = middleware(authedService)

}
