package io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import scala.util.Try


case class BankCard(
                     cardHolderName: CardHolderName,
                     cardNumber: CardNumber,
                     cardExpiration: CardExpiration,
                     cardCvc: CardCvc
                   )
object BankCard{
  implicit val decoder: Decoder[BankCard] = deriveDecoder[BankCard]
  implicit val encoder: Encoder[BankCard] = deriveEncoder[BankCard]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, BankCard] = accumulatingJsonOf[F, BankCard]
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, BankCard] = jsonEncoderOf[F, BankCard]
}


case class CardHolderName private(value: String) extends AnyVal
object CardHolderName{
  def isValidName(str: String): Boolean =
    (2 to 26 contains str.length) &&        // 2-26 chars
    (str.trim.length == str.length) &&      // No spaces around
    str.matches("^[A-z ]+$")         // Contain only spaces, upper and lower case letters

  def of(value: String): Option[CardHolderName] = value match {
    case v if isValidName(v) => Some(CardHolderName(v))
    case _ => None
  }

  val strError: String = "Invalid Cardholder name. Cardholder name should contain 2-26 chars." +
    " It must contain only spaces, upper and lower case letters. It must not start or end with a space."
  implicit val decoder: Decoder[CardHolderName] = deriveUnwrappedDecoder[CardHolderName].validate(
    _.value.asString match {
      case Some(value) => isValidName(value)
      case None => false
    },
    strError
  )
  implicit val encoder: Encoder[CardHolderName] = deriveUnwrappedEncoder[CardHolderName]

  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, CardHolderName] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, CardHolderName] = jsonEncoderOf
}

case class CardNumber private(value: String) extends AnyVal
object CardNumber{
  def isValidNumber(str: String): Boolean =
    (8 to 19 contains str.length) &&        // 8-19 chars
    str(0) != '0' &&                        // Can't start with 0
    str.matches("^[0-9]*$")          // Only digits

  def of(value: String): Option[CardNumber] = value match {
    case v if isValidNumber(v) => Some(CardNumber(v))
    case _ => None
  }

  val strError = "Invalid card number. Card number must contain between 8 and 19 digits (inclusive) and must not start with 0."
  implicit val decoder: Decoder[CardNumber] = deriveUnwrappedDecoder[CardNumber].validate(
    _.value.asString match {
      case Some(value) => isValidNumber(value)
      case None => false
    },
    strError
  )
  implicit val encoder: Encoder[CardNumber] = deriveUnwrappedEncoder[CardNumber]

  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, CardNumber] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, CardNumber] = jsonEncoderOf
}


case class CardExpiration private(value: YearMonth) extends AnyVal
object CardExpiration{
  val dateFormat: String = "uuuu-MM"
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat)
  def isValidExpiration(str: String): Boolean = {
    if (str.nonEmpty && str.matches("^[0-9]{1,4}-(0[1-9]|1[012])$"))
      Try(new SimpleDateFormat(dateFormat).parse(str)).toOption match {
        case Some(_) =>
          if (YearMonth.parse(str, dateTimeFormatter).compareTo(YearMonth.now()) >= 0) true
          else false
        case None => false
      }
    else false
  }

  def of(value: String): Option[CardExpiration] = value match {
    case v if isValidExpiration(v) => Some(CardExpiration(YearMonth.parse(v, dateTimeFormatter)))
    case _ => None
  }

  val strError = "Invalid card expiration. Card expiration should be of format YYYY-MM, where MM ranges 01-12, YYYY ranges 0001-9999 and not in the past."
  implicit val decoder: Decoder[CardExpiration] = deriveUnwrappedDecoder[CardExpiration].validate(
    _.value.asString match {
      case Some(value) => isValidExpiration(value)
      case None => false
    },
    strError
  )
  implicit val encoder: Encoder[CardExpiration] = deriveUnwrappedEncoder[CardExpiration]

  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, CardExpiration] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, CardExpiration] = jsonEncoderOf
}


case class CardCvc private(value: String) extends AnyVal
object CardCvc {
  private def isValidCvc(v: String): Boolean = Seq(3, 4).contains(v.length) && v.forall(_.isDigit)

  def of(value: String): Option[CardCvc] = value match {
    case v if isValidCvc(v) => Some(CardCvc(v))
    case _ => None
  }

  val strError = "Invalid CVC. Card CVC must contain between 3 and 4 digits (inclusive)."
  implicit val decoder: Decoder[CardCvc] = deriveUnwrappedDecoder[CardCvc].validate(
    _.value.asString match {
      case Some(value) => isValidCvc(value)
      case None => false
    },
    strError
  )
  implicit val encoder: Encoder[CardCvc] = deriveUnwrappedEncoder[CardCvc]

  implicit def entityDecoder[F[_] : Concurrent]: EntityDecoder[F, CardCvc] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:              EntityEncoder[F, CardCvc] = jsonEncoderOf
}

