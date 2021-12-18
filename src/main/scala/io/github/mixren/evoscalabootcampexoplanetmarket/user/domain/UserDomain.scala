package io.github.mixren.evoscalabootcampexoplanetmarket.user.domain

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, accumulatingJsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}


// userName is unique
case class User(userName: UserName, passwordHash: PasswordHash){
  def validate(hash: String): Boolean =
    passwordHash.value equals hash
}
object User{
  implicit val decoder: Decoder[User] = deriveDecoder[User]
  implicit val encoder: Encoder[User] = deriveEncoder[User]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, User] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, User] = jsonEncoderOf
}


case class UserName private(value: String) extends AnyVal
object UserName {
  def isValidName(str: String): Boolean = str.trim.nonEmpty && str.trim.length == str.length

  def of(value: String): Option[UserName] = value match {
    case v if isValidName(v) => Some(UserName(v))
    case _ => None
  }

  val strError: String = "Username invalid. Username should not be surrounded by spaces and be non-empty"
  implicit val decoder: Decoder[UserName] = deriveUnwrappedDecoder[UserName].validate(
    _.value.asString match {
      case Some(value) => isValidName(value)
      case None => false
    },
    strError
  )
  implicit val encoder: Encoder[UserName] = deriveUnwrappedEncoder[UserName]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, UserName] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, UserName] = jsonEncoderOf
}


case class PasswordHash private(value: String) extends AnyVal
object PasswordHash {
   def isValidPassword(str: String): Boolean = !str.contains(' ') && str.length == 64

   def of(value: String): Option[PasswordHash] = value match {
     case v if isValidPassword(v) => Some(PasswordHash(v))
     case _ => None
   }

   val strError = "Invalid password hash value. Password hash should not contain spaces and should be 64 chars long."
   implicit val decoder: Decoder[PasswordHash] = deriveUnwrappedDecoder[PasswordHash].validate(
     _.value.asString match {
       case Some(value) => isValidPassword(value)
       case None => false
     },
     strError
   )
   implicit val encoder: Encoder[PasswordHash] = deriveUnwrappedEncoder[PasswordHash]

   implicit def entityDecoder[F[_] : Concurrent]: EntityDecoder[F, PasswordHash] = accumulatingJsonOf
   implicit def entityEncoder[F[_]]: EntityEncoder[F, PasswordHash] = jsonEncoderOf

}

