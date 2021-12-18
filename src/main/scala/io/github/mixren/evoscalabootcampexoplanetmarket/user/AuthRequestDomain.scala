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
                        password: AuthPassword
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


case class AuthPassword private(value: String) extends AnyVal
object AuthPassword {
  def isValidPassword(str: String): Boolean = str.length > 5 && !str.contains(' ')

  def of(value: String): Option[AuthPassword] = value match {
    case v if isValidPassword(v) => Some(AuthPassword(v))
    case _ => None
  }

  val strError = "Invalid password value. Password should have 6 or more symbols and not contain spaces."
  implicit val decoder: Decoder[AuthPassword] = deriveUnwrappedDecoder[AuthPassword].validate(
    _.value.asString match {
      case Some(value) => isValidPassword(value)
      case None => false
    },
    strError
  )
  implicit val encoder: Encoder[AuthPassword] = deriveUnwrappedEncoder[AuthPassword]

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

