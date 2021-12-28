package io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain

import cats.effect.Concurrent
import cats.implicits._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{ExoplanetNewName, ExoplanetOfficialName}
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}


//"""{"exoplanetName" : "2I/Borisov", "exoplanetNewName" : "2I/Borisov", "card" : {"cardHolderName" : "Manny", "cardNumber" : "111122223333", "cardExpiration" : "2030-12", "cardCvc" : "123"}}"""
case class ExoOldNewCardRequest(exoplanetName: ExoplanetOfficialName,
                                exoplanetNewName: ExoplanetNewName,
                                card: BankCard)
object ExoOldNewCardRequest{
  /*def of(args: List[String]): Option[ExoOldNewCardRequest] = args match {
    case exoOldName :: exoNewName :: cardHolderName :: cardNum :: cardExp :: cardCvc :: Nil =>
      val chn = CardHolderName.of(cardHolderName)
      val cn  = CardNumber.of(cardNum)
      val ce  = CardExpiration.of(cardExp)
      val cc  = CardCvc.of(cardCvc)
      val bc  = (chn, cn, ce, cc).mapN(BankCard(_,_,_,_))
      val eon = ExoplanetOfficialName.of(exoOldName)
      val enn = ExoplanetNewName.of(exoNewName)
      (eon, enn, bc).mapN(ExoOldNewCardRequest(_,_,_))
    case _ =>
      None
  }*/

  /**
   * Convert a list of args into string Json not validating parameters.
   * @return Some Json string if number of parameters is correct. Else None.
   */
  def novalidJsonStrOf(args: List[String]): Option[String] = args match {
    case exoOldName :: exoNewName :: cardHolderName :: cardNum :: cardExp :: cardCvc :: Nil =>
      val jsonStr = s"""
        |{
        |"exoplanetName":"$exoOldName",
        |"exoplanetNewName":"$exoNewName",
        |"card":
        |   {
        |   "cardHolderName":"$cardHolderName",
        |   "cardNumber":"$cardNum",
        |   "cardExpiration":"$cardExp",
        |   "cardCvc":"$cardCvc"
        |   }
        |}
        |"""
        .stripMargin
      Some(jsonStr)

    case _ =>
      None
  }

  implicit val decoder: Decoder[ExoOldNewCardRequest] = deriveDecoder[ExoOldNewCardRequest]
  implicit val encoder: Encoder[ExoOldNewCardRequest] = deriveEncoder[ExoOldNewCardRequest]
  implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, ExoOldNewCardRequest] = accumulatingJsonOf[F, ExoOldNewCardRequest]
  implicit def entityEncoder[F[_]]:             EntityEncoder[F, ExoOldNewCardRequest] = jsonEncoderOf
}
