package io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain

import cats.effect.Concurrent
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}

case class Purchase(
                     officialName: ExoplanetOfficialName,
                     newName: ExoplanetNewName,
                     purchaserUserName: UserName,
                     price: PurchasePrice,
                     timestamp: Long
                   )
object Purchase{
  implicit val decoder: Decoder[Purchase] = deriveDecoder[Purchase]
  implicit val encoder: Encoder[Purchase] = deriveEncoder[Purchase]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, Purchase] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, Purchase] = jsonEncoderOf
}


case class PurchasePrice(value: BigDecimal) extends AnyVal
object PurchasePrice{
  implicit val decoder: Decoder[PurchasePrice] = deriveDecoder[PurchasePrice]
  implicit val encoder: Encoder[PurchasePrice] = deriveEncoder[PurchasePrice]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, PurchasePrice] = accumulatingJsonOf
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, PurchasePrice] = jsonEncoderOf
}



