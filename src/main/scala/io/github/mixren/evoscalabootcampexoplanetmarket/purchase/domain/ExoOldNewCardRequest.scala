package io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain

import cats.effect.Concurrent
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}


//"""{"exoplanetName" : "2I/Borisov", "exoplanetNewName" : "2I/Borisov", "card" : {"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}}"""
case class ExoOldNewCardRequest(exoplanetName: ExoplanetOfficialName, exoplanetNewName: ExoplanetNewName, card: BankCard)
object ExoOldNewCardRequest{
  implicit val decoder: Decoder[ExoOldNewCardRequest] = deriveDecoder[ExoOldNewCardRequest]
  implicit val encoder: Encoder[ExoOldNewCardRequest] = deriveEncoder[ExoOldNewCardRequest]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, ExoOldNewCardRequest] = accumulatingJsonOf[F, ExoOldNewCardRequest]
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, ExoOldNewCardRequest] = jsonEncoderOf
}
