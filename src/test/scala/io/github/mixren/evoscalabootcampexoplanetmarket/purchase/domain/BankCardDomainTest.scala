package io.github.mixren.evoscalabootcampexoplanetmarket.purchase.domain

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxEitherId
import io.circe.Json
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.http4s.InvalidMessageBodyFailure
import org.http4s.circe.DecodingFailures
import org.http4s.dsl.io.{Ok, _}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class BankCardDomainTest extends AnyFlatSpec with Matchers{

  val cardReqValid: BankCard = BankCard(
    CardHolderName("Bake Baker"),
    CardNumber("1111222233334444"),
    CardExpiration(YearMonth.parse("2023-12", DateTimeFormatter.ofPattern("uuuu-MM"))),
    CardCvc("123"))
  val cardReqJsonStrValid: String =
    """
      |{
      |"cardHolderName":"Bake Baker",
      |"cardNumber":"1111222233334444",
      |"cardExpiration":"2023-12",
      |"cardCvc":"123"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val cardReqJsonStrInvalid1: String =
    """
      |{
      |"cardHolderName":"B",
      |"cardNumber":"1111222233334444",
      |"cardExpiration":"2023-12",
      |"cardCvc":"123"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val cardReqJsonStrInvalid2: String =
    """
      |{
      |"cardHolderName":"Bake Baker",
      |"cardNumber":"sasf ",
      |"cardExpiration":"2023-12",
      |"cardCvc":"123"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val cardReqJsonStrInvalid3: String =
    """
      |{
      |"cardHolderName":"Bake Baker",
      |"cardNumber":"1111222233334444",
      |"cardExpiration":"2023-1256",
      |"cardCvc":"123"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val cardReqJsonStrInvalid4: String =
    """
      |{
      |"cardHolderName":"Bake Baker",
      |"cardNumber":"1111222233334444",
      |"cardExpiration":"2023-12",
      |"cardCvc":"123fgda"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")


  "BankCardDomain" should "have working encoder" in{
    cardReqValid.asJson.noSpaces must be (cardReqJsonStrValid)
  }

  it should "have working decoder with working validator" in{
    decode[BankCard](cardReqJsonStrValid) must be (cardReqValid.asRight)
    assert(decode[BankCard](cardReqJsonStrInvalid1).isLeft)
    assert(decode[BankCard](cardReqJsonStrInvalid2).isLeft)
    assert(decode[BankCard](cardReqJsonStrInvalid3).isLeft)
    assert(decode[BankCard](cardReqJsonStrInvalid4).isLeft)
  }

  it should "have working EntityDecoder with working validator" in{
    checkEntityDecoder(cardReqJsonStrValid).unsafeRunSync().noSpaces must be (cardReqJsonStrValid)
    checkEntityDecoder(cardReqJsonStrInvalid1).unsafeRunSync().noSpaces must not be cardReqJsonStrInvalid1
    checkEntityDecoder(cardReqJsonStrInvalid2).unsafeRunSync().noSpaces must not be cardReqJsonStrInvalid2
    checkEntityDecoder(cardReqJsonStrInvalid3).unsafeRunSync().noSpaces must not be cardReqJsonStrInvalid3
    checkEntityDecoder(cardReqJsonStrInvalid4).unsafeRunSync().noSpaces must not be cardReqJsonStrInvalid4
  }

  def checkEntityDecoder(userJsonString: String): IO[Json] = {
    Ok(userJsonString).flatMap(_.as[BankCard])
      .redeemWith({
        case InvalidMessageBodyFailure(_, Some(DecodingFailures(f))) =>
          IO(f.map(_.toString()).asJson)
        case o => IO(o.getMessage.asJson)
      }, v => IO(v.asJson))
  }
}
