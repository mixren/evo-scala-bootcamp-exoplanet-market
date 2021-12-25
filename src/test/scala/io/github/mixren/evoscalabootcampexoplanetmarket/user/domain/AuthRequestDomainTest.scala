package io.github.mixren.evoscalabootcampexoplanetmarket.user.domain

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxEitherId
import io.circe.Json
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.http4s.InvalidMessageBodyFailure
import org.http4s.circe.DecodingFailures
import org.http4s.dsl.io.Ok
import org.scalatest.flatspec.AnyFlatSpec
import org.http4s.dsl.io._
import org.scalatest.matchers.must.Matchers


class AuthRequestDomainTest extends AnyFlatSpec with Matchers{

  val authReqValid: AuthRequest = AuthRequest(UserName("Jax"), AuthPassword("123456"))
  val authReqJsonStrValid: String =
    """
      |{
      |"username":"Jax",
      |"password":"123456"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val authReqJsonStrInvalid1: String =
    """
      |{
      |"username":" J",
      |"password":"123456"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val authReqJsonStrInvalid2: String =
    """
      |{
      |"username":"Jax",
      |"password":"1 "
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")


  "AuthRequestDomain" should "have working encoder" in{
    authReqValid.asJson.noSpaces must be (authReqJsonStrValid)
  }

  it should "have working decoder with working validator" in{
    decode[AuthRequest](authReqJsonStrValid) must be (authReqValid.asRight)
    assert(decode[AuthRequest](authReqJsonStrInvalid1).isLeft)
    assert(decode[AuthRequest](authReqJsonStrInvalid2).isLeft)
  }

  it should "have working EntityDecoder with working validator" in{
    checkEntityDecoder(authReqJsonStrValid).unsafeRunSync().noSpaces must be (authReqJsonStrValid)
    checkEntityDecoder(authReqJsonStrInvalid1).unsafeRunSync().noSpaces must not be authReqJsonStrInvalid1
    checkEntityDecoder(authReqJsonStrInvalid2).unsafeRunSync().noSpaces must not be authReqJsonStrInvalid2
  }

  def checkEntityDecoder(userJsonString: String): IO[Json] = {
    Ok(userJsonString).flatMap(_.as[AuthRequest])
      .redeemWith({
        case InvalidMessageBodyFailure(_, Some(DecodingFailures(f))) =>
          IO(f.map(_.toString()).asJson)
        case o => IO(o.getMessage.asJson)
      }, v => IO(v.asJson))
  }
}

