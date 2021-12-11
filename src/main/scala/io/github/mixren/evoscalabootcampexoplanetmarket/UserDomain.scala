package io.github.mixren.evoscalabootcampexoplanetmarket

import io.circe.generic.semiauto._
import org.http4s.{EntityDecoder, EntityEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import cats.effect.Concurrent


// name is unique
case class User(name: String, password: String)
object User{
  implicit val decoder: Decoder[User] = deriveDecoder[User]
  implicit val encoder: Encoder[User] = deriveEncoder[User]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, User] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, User] = jsonEncoderOf
}
