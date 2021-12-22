package io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain

import cats.effect.Concurrent
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}

/*
  Here I put case classes combined of different value classes.
   They are needed for decoding Http requests Jsons.
 */


//"""{"exoplanetName" : "2I/Borisov", "exoplanetNewName" : "2I/Borisov", "card" : {"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}}"""
case class TrioExosCard(exoplanetName: ExoplanetOfficialName, exoplanetNewName: ExoplanetNewName, card: BankCard)
object TrioExosCard{
  implicit val decoder: Decoder[TrioExosCard] = deriveDecoder[TrioExosCard]
  implicit val encoder: Encoder[TrioExosCard] = deriveEncoder[TrioExosCard]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, TrioExosCard] = accumulatingJsonOf[F, TrioExosCard]
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, TrioExosCard] = jsonEncoderOf
}
