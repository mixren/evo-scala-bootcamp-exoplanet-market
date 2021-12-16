package io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

case class JWToken(value: String) extends AnyVal
object JWToken{
  implicit val decode: Decoder[JWToken] = deriveUnwrappedDecoder[JWToken]
  implicit val encode: Encoder[JWToken] = deriveUnwrappedEncoder[JWToken]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, JWToken] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, JWToken] = jsonEncoderOf
}
