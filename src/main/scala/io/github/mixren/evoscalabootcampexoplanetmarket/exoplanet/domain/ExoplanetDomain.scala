package io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain

import cats.effect.Concurrent
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import scala.util.Try


// Some params are options because the original .csv file might not have these values (they are "")
case class Exoplanet(id: Int,
                     officialName: ExoplanetOfficialName,
                     mass: Option[Mass],
                     radius: Option[Radius],
                     distance: Option[Distance],
                     ra: Option[Ra],
                     dec: Option[Dec],
                     discoveryYear: Option[Year])
object Exoplanet {
  implicit val decoder: Decoder[Exoplanet] = deriveDecoder[Exoplanet]
  implicit val encoder: Encoder[Exoplanet] = deriveEncoder[Exoplanet]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Exoplanet] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Exoplanet] = jsonEncoderOf

  def fromCsvData(id: Int, name: String, mass: String, radius: String,
                  distance: String, ra: String, dec: String, year: String): Exoplanet =
    new Exoplanet(
      id,
      ExoplanetOfficialName(name),
      Mass.fromString(mass),
      Radius.fromString(radius),
      Distance.fromString(distance),
      Ra.fromString(ra),
      Dec.fromString(dec),
      Year.fromString(year)
    )
}

/*object MyConverter {
  def odFromString[A](str: String): Option[Double] = {
    if (str.isEmpty) Option.empty[Double] // for long files is better to check first, than throw exceptions
    else Try(str.toDouble).toOption
  }
}*/


case class ExoplanetOfficialName(name: String) extends AnyVal
object ExoplanetOfficialName {
  implicit val decode: Decoder[ExoplanetOfficialName] = deriveUnwrappedDecoder[ExoplanetOfficialName]
  implicit val encode: Encoder[ExoplanetOfficialName] = deriveUnwrappedEncoder[ExoplanetOfficialName]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, ExoplanetOfficialName] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, ExoplanetOfficialName] = jsonEncoderOf
}

case class ExoplanetNewName(value: String) extends AnyVal
object ExoplanetNewName {
  implicit val decode: Decoder[ExoplanetNewName] = deriveUnwrappedDecoder[ExoplanetNewName]
  implicit val encode: Encoder[ExoplanetNewName] = deriveUnwrappedEncoder[ExoplanetNewName]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, ExoplanetNewName] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, ExoplanetNewName] = jsonEncoderOf
}

case class Mass(mass: Double) extends AnyVal
object Mass {
  implicit val decode: Decoder[Mass] = deriveUnwrappedDecoder[Mass]
  implicit val encode: Encoder[Mass] = deriveUnwrappedEncoder[Mass]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Mass] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Mass] = jsonEncoderOf

  def fromString(str: String): Option[Mass] =
    if (str.isEmpty) Option.empty[Mass]          // for long files is better to check first, than throw exceptions
    else Try(str.toDouble).toOption.map(Mass(_))
}


case class Radius(radius: Double) extends AnyVal
object Radius {
  implicit val decode: Decoder[Radius] = deriveUnwrappedDecoder[Radius]
  implicit val encode: Encoder[Radius] = deriveUnwrappedEncoder[Radius]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Radius] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Radius] = jsonEncoderOf

  def fromString(str: String): Option[Radius] =
    if (str.isEmpty) Option.empty[Radius]          // for long files is better to check first, than throw exceptions
    else Try(str.toDouble).toOption.map(Radius(_))
}


case class Distance(distance: Double) extends AnyVal
object Distance {
  implicit val decode: Decoder[Distance] = deriveUnwrappedDecoder[Distance]
  implicit val encode: Encoder[Distance] = deriveUnwrappedEncoder[Distance]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Distance] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Distance] = jsonEncoderOf

  def fromString(str: String): Option[Distance] =
    if (str.isEmpty) Option.empty[Distance]          // for long files is better to check first, than throw exceptions
    else Try(str.toDouble).toOption.map(Distance(_))
}


case class Ra(ra: Double) extends AnyVal
object Ra {
  implicit val decode: Decoder[Ra] = deriveUnwrappedDecoder[Ra]
  implicit val encode: Encoder[Ra] = deriveUnwrappedEncoder[Ra]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Ra] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Ra] = jsonEncoderOf

  def fromString(str: String): Option[Ra] =
    if (str.isEmpty) Option.empty[Ra]           // for long files is better to check first, than throw exceptions
    else Try(str.toDouble).toOption.map(Ra(_))
}


case class Dec(dec: Double) extends AnyVal
object Dec {
  implicit val decode: Decoder[Dec] = deriveUnwrappedDecoder[Dec]
  implicit val encode: Encoder[Dec] = deriveUnwrappedEncoder[Dec]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Dec] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Dec] = jsonEncoderOf

  def fromString(str: String): Option[Dec] =
    if (str.isEmpty) Option.empty[Dec]          // for long files is better to check first, than throw exceptions
    else Try(str.toDouble).toOption.map(Dec(_))
}


case class Year(year: Int) extends AnyVal
object Year {
  implicit val decode: Decoder[Year] = deriveUnwrappedDecoder[Year]
  implicit val encode: Encoder[Year] = deriveUnwrappedEncoder[Year]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Year] = jsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Year] = jsonEncoderOf

  def fromString(str: String): Option[Year] =
    if (str.isEmpty) Option.empty[Year]         // for long files is better to check first, than throw exceptions
    else Try(str.toInt).toOption.map(Year(_))
}
