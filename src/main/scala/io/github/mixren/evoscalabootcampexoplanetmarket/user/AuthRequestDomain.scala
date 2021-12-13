package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class AuthRequest(
                        userName: UserName,
                        userPassword: UserPassword,
                      ) {
//  def asUser[A](hashedPassword: PasswordHash[A]): User = User(
//    userName,
//    hashedPassword.toString
//  )
}

object AuthRequest{
  implicit val decoder: Decoder[AuthRequest] = deriveDecoder[AuthRequest]
  implicit val encoder: Encoder[AuthRequest] = deriveEncoder[AuthRequest]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, AuthRequest] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, AuthRequest] = jsonEncoderOf


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

