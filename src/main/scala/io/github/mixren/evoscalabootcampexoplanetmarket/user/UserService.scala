package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{AuthRequest, AuthUser, PasswordHash}
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.HashGenerator
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JwtHelper.jwtEncode
import java.time.Instant


trait UserServiceT[F[_]]{
  def userLogin(authRequest: AuthRequest): F[Either[String, JWToken]]
  def userRegister(authRequest: AuthRequest): F[Either[String, JWToken]]
}

class UserService[F[_]: Async](repo: UserRepository[F]) extends UserServiceT[F]{

  override def userLogin(authRequest: AuthRequest): F[Either[String, JWToken]] = {
    val passHash = HashGenerator.run(authRequest.password.value)
    for {
      userO <- repo.userByName(authRequest.username)
    } yield userO match {
      case Some(user) if user.validate(passHash)  =>
        jwtEncode(AuthUser(user.username)).asRight[String]
      case None                               =>
        s"Error. User ${authRequest.username.value} is not registered".asLeft[JWToken]
      case _                                  =>
        s"Error. Wrong password".asLeft[JWToken]
    }
  }

  override def userRegister(authRequest: AuthRequest): F[Either[String, JWToken]] = {
    val passHash = HashGenerator.run(authRequest.password.value)
    val user = authRequest.asUser(PasswordHash(passHash))
    for {
      e <- repo.createUser(user, Instant.now())
    } yield e.map(_ => jwtEncode(AuthUser(user.username)))
  }

}
