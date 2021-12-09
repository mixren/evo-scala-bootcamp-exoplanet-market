package io.github.mixren.evoscalabootcampexoplanetmarket

import scala.util.Try

// Some params are options because the original .csv file might not have these values (they are "")
case class Exoplanet(id: Int, officialName: OfficialName, mass: Option[Mass],
                     radius: Option[Radius], distance: Option[Distance],
                     ra: Option[Ra], dec: Option[Dec], discoveryYear: Option[Year])
object Exoplanet {
  def fromCsvData(id: Int, name: String, mass: String, radius: String,
                  distance: String, ra: String, dec: String, year: String): Exoplanet =
    new Exoplanet(
      id,
      OfficialName(name),
      Mass.fromString(mass),
      Radius.fromString(radius),
      Distance.fromString(distance),
      Ra.fromString(ra),
      Dec.fromString(dec),
      Year.fromString(year)
    )
}


case class OfficialName(name: String) extends AnyVal

case class Mass(mass: Float) extends AnyVal
object Mass {
  def fromString(str: String): Option[Mass] =
    if (str.isEmpty) Option.empty[Mass]          // for long files is better to check first, than throw exceptions
    else Try(str.toFloat).toOption.map(Mass(_))
}

case class Radius(radius: Float) extends AnyVal
object Radius {
  def fromString(str: String): Option[Radius] =
    if (str.isEmpty) Option.empty[Radius]          // for long files is better to check first, than throw exceptions
    else Try(str.toFloat).toOption.map(Radius(_))
}

case class Distance(distance: Float) extends AnyVal
object Distance {
  def fromString(str: String): Option[Distance] =
    if (str.isEmpty) Option.empty[Distance]          // for long files is better to check first, than throw exceptions
    else Try(str.toFloat).toOption.map(Distance(_))
}

case class Ra(ra: Float) extends AnyVal
object Ra {
  def fromString(str: String): Option[Ra] =
    if (str.isEmpty) Option.empty[Ra]           // for long files is better to check first, than throw exceptions
    else Try(str.toFloat).toOption.map(Ra(_))
}

case class Dec(dec: Float) extends AnyVal
object Dec {
  def fromString(str: String): Option[Dec] =
    if (str.isEmpty) Option.empty[Dec]          // for long files is better to check first, than throw exceptions
    else Try(str.toFloat).toOption.map(Dec(_))
}

case class Year(year: Int) extends AnyVal
object Year {
  def fromString(str: String): Option[Year] =
    if (str.isEmpty) Option.empty[Year]         // for long files is better to check first, than throw exceptions
    else Try(str.toInt).toOption.map(Year(_))
}
