package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.data.EitherT
import cats.effect.Async
import cats.implicits.catsSyntaxApplicativeError
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.PasswordHash
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.HashGenerator
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JwtHelper.jwtEncode

import java.time.Instant

class UserRoutesService[F[_]: Async](repo: UserRepository[F]) {

  def userLogin(authRequest: F[AuthRequest]): EitherT[F, String, JWToken] =
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
    } yield result


  def userRegister(req: F[AuthRequest]): EitherT[F, String, JWToken] =
    for {
      authReq   <- req.attemptT.leftMap(t => t.getMessage)
      hash      = HashGenerator.run(authReq.password.value)
      user      = authReq.asUser(PasswordHash(hash))
      user      <- repo.createUser(user, Instant.now())
    } yield jwtEncode(user)


}
