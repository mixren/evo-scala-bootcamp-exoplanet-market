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


class AuthUserTest extends AnyFlatSpec with Matchers{

  val authUserValid: AuthUser = AuthUser(UserName("Jax"))
  val authUserJsonStrValid: String =
    """
      |{
      |"username":"Jax"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val authUserJsonStrInvalid1: String =
    """
      |{
      |"username":" J",
      |"password":"123456"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")


  "AuthUser" should "have working encoder" in{
    authUserValid.asJson.noSpaces must be (authUserJsonStrValid)
  }

  it should "have working decoder with working validator" in{
    decode[AuthUser](authUserJsonStrValid) must be (authUserValid.asRight)
    assert(decode[AuthUser](authUserJsonStrInvalid1).isLeft)
  }

  it should "have working EntityDecoder with working validator" in{
    checkEntityDecoder(authUserJsonStrValid).unsafeRunSync().noSpaces must be (authUserJsonStrValid)
    checkEntityDecoder(authUserJsonStrInvalid1).unsafeRunSync().noSpaces must not be authUserJsonStrInvalid1
  }

  def checkEntityDecoder(userJsonString: String): IO[Json] = {
    Ok(userJsonString).flatMap(_.as[AuthUser])
      .redeemWith({
        case InvalidMessageBodyFailure(_, Some(DecodingFailures(f))) =>
          IO(f.map(_.toString()).asJson)
        case o => IO(o.getMessage.asJson)
      }, v => IO(v.asJson))
  }
}
