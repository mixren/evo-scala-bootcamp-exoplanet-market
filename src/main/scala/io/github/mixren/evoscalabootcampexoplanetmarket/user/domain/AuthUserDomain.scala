package io.github.mixren.evoscalabootcampexoplanetmarket.user.domain

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}

/**UserName has UnwrappedCodecs, which is not suitable for JWToken encoding/decoding.
Thus wrap in this case class to use UserName in AuthedRoutes.
Bonus advantage is that it can be easily extended.
*/
case class AuthUser(username: UserName)
object AuthUser{
  implicit val decoder: Decoder[AuthUser] = deriveDecoder[AuthUser]
  implicit val encoder: Encoder[AuthUser] = deriveEncoder[AuthUser]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, AuthUser] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, AuthUser] = jsonEncoderOf
}