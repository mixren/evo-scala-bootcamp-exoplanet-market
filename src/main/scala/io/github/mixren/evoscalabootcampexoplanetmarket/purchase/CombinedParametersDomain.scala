package io.github.mixren.evoscalabootcampexoplanetmarket.purchase

import cats.effect.Concurrent
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.UserName
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}

/*
  Here I put case classes combined of different value classes.
   They are needed for decoding Http requests Jsons.
 */

//"""{"exoplanetName" : "2I/Borisov", "username" : "Allah"}"""
case class PairExonameUsername(exoplanetName: ExoplanetOfficialName, username: UserName)
object PairExonameUsername{
  implicit val decoder: Decoder[PairExonameUsername] = deriveDecoder[PairExonameUsername]
  implicit val encoder: Encoder[PairExonameUsername] = deriveEncoder[PairExonameUsername]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, PairExonameUsername] = accumulatingJsonOf[F, PairExonameUsername]
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, PairExonameUsername] = jsonEncoderOf
}

//"""{"exoplanetName" : "2I/Borisov", "exoplanetNewName" : "2I/Borisov", "card" : {"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}}"""
case class QuatroExosUsrCard(exoplanetName: ExoplanetOfficialName, exoplanetNewName: ExoplanetNewName, username: UserName, card: BankCard)
object QuatroExosUsrCard{
  implicit val decoder: Decoder[QuatroExosUsrCard] = deriveDecoder[QuatroExosUsrCard]
  implicit val encoder: Encoder[QuatroExosUsrCard] = deriveEncoder[QuatroExosUsrCard]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, QuatroExosUsrCard] = accumulatingJsonOf[F, QuatroExosUsrCard]
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, QuatroExosUsrCard] = jsonEncoderOf
}
