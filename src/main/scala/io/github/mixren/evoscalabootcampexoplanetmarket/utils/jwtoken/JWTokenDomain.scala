package io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, accumulatingJsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}


case class JWToken private(value: String) extends AnyVal
object JWToken{
  def isValidToken(str: String): Boolean = !str.contains(' ') && str.split(".").length == 3

  def of(value: String): Option[JWToken] = value match {
    case v if isValidToken(v) => Some(JWToken(v))
    case _ => None
  }

  val strError = "Invalid token (JWT) value. Token should not contain spaces and should have two \".\"."
  implicit val decoder: Decoder[JWToken] = deriveUnwrappedDecoder[JWToken].validate(
    _.value.asString match {
      case Some(value) => isValidToken(value)
      case None => false
    },
    strError
  )
  implicit val encode: Encoder[JWToken] = deriveUnwrappedEncoder[JWToken]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, JWToken] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, JWToken] = jsonEncoderOf
}
