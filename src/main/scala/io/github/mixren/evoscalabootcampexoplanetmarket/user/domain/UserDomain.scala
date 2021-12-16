package io.github.mixren.evoscalabootcampexoplanetmarket.user.domain

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}


// userName is unique
case class User(userName: UserName, passwordHash: PasswordHash){
  def validate(hash: String): Boolean =
    passwordHash.value equals hash
}
object User{
  implicit val decoder: Decoder[User] = deriveDecoder[User]
  implicit val encoder: Encoder[User] = deriveEncoder[User]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, User] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, User] = jsonEncoderOf
}


case class UserName(value: String) extends AnyVal
object UserName {
  implicit val decode: Decoder[UserName] = deriveUnwrappedDecoder[UserName]
  implicit val encode: Encoder[UserName] = deriveUnwrappedEncoder[UserName]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, UserName] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, UserName] = jsonEncoderOf
}

case class PasswordHash(value: String) extends AnyVal
object PasswordHash {
  implicit val decode: Decoder[PasswordHash] = deriveUnwrappedDecoder[PasswordHash]
  implicit val encode: Encoder[PasswordHash] = deriveUnwrappedEncoder[PasswordHash]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, PasswordHash] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, PasswordHash] = jsonEncoderOf
}

