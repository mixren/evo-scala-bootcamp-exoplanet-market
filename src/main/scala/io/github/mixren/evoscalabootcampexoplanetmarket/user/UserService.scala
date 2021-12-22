package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Async
import cats.implicits.{catsSyntaxEitherId, toFlatMapOps}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{AuthRequest, PasswordHash}
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.HashGenerator
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JwtHelper.jwtEncode

import java.time.Instant

class UserService[F[_]: Async](repo: UserRepository[F]) {

  /*def userLogin(authRequest: F[AuthRequest]): EitherT[F, String, JWToken] =
    for {
      authReq     <- authRequest.attemptT.leftMap(t => t.getMessage)
      userO       <- repo.userByName(authReq.userName)
      user        <- EitherT.fromOption[F](userO, s"User ${authReq.userName.value} is not registered")
      hash        = HashGenerator.run(authReq.password.value)
      result      <- if (user.validate(hash)) {
        EitherT.rightT[F, String](jwtEncode(user))
      } else {
        EitherT.leftT[F, JWToken]("Wrong password")
      }
    } yield result*/

  def userLogin2(authRequest: AuthRequest): F[Either[String, JWToken]] = {
    val passHash = HashGenerator.run(authRequest.password.value)
    repo.userByName2(authRequest.userName).flatMap {
      case Some(user) if user.validate(passHash)  =>
        Async[F].pure(jwtEncode(user).asRight[String])
      case None                               =>
        Async[F].pure(s"Error. User ${authRequest.userName.value} is not registered".asLeft[JWToken])
      case _                                  =>
        Async[F].pure(s"Error. Wrong password".asLeft[JWToken])
    }
  }

  /*def userRegister(req: F[AuthRequest]): EitherT[F, String, JWToken] =
    for {
      authReq   <- req.attemptT.leftMap(t => t.getMessage)
      hash      = HashGenerator.run(authReq.password.value)
      user      = authReq.asUser(PasswordHash(hash))
      user      <- repo.createUser(user, Instant.now())
    } yield jwtEncode(user)*/

  def userRegister2(authRequest: AuthRequest): F[Either[String, JWToken]] = {
    val passHash = HashGenerator.run(authRequest.password.value)
    val user = authRequest.asUser(PasswordHash(passHash))
    repo.createUser2(user, Instant.now()).flatMap{a =>
      Async[F].delay(a.map{_ =>
        jwtEncode(user)
      })
    }

  }

}
