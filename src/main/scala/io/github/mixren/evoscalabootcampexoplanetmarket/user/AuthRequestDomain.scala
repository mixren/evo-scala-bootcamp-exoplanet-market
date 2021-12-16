package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{PasswordHash, User, UserName}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

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
  // Validation works but doesnt display the error
  /*def parse(str: String): Either[String, AuthPassword] =
    if (str.length > 3) Right(AuthPassword(str))
    else Left("Invalid password value. Password should have 4 or more symbols")

  def parseUnsafe(str: String): AuthPassword =
    parse(str).fold(error => throw new Exception(error), pwd => pwd)

  private case class AuthPasswordHelper(value: String)

  implicit val decode: Decoder[AuthPassword] = deriveUnwrappedDecoder[AuthPasswordHelper].emapTry(helper =>
    Try(parseUnsafe(helper.value))
  )*/
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

