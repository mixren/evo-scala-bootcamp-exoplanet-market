package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class AuthRequest(
                        userName: UserName,
                        password: AuthPassword,
                      ) {
  def asUser(hashedPassword: PasswordHash): User = User(
    userName,
    hashedPassword
  )
}

object AuthRequest{
  implicit val decoder: Decoder[AuthRequest] = deriveDecoder[AuthRequest]
  implicit val encoder: Encoder[AuthRequest] = deriveEncoder[AuthRequest]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, AuthRequest] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, AuthRequest] = jsonEncoderOf
}

case class AuthPassword(value: String) extends AnyVal
object AuthPassword {
  implicit val decode: Decoder[AuthPassword] = deriveUnwrappedDecoder[AuthPassword]
  implicit val encode: Encoder[AuthPassword] = deriveUnwrappedEncoder[AuthPassword]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, AuthPassword] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, AuthPassword] = jsonEncoderOf
}

//  final case class LoginRequest(
//                                 userName: String,
//                                 password: String,
//                               )
//
//  final case class SignupRequest(
//                                  userName: String,
//                                  firstName: String,
//                                  lastName: String,
//                                  email: String,
//                                  password: String,
//                                  phone: String,
//                                  role: Role,
//                                )

