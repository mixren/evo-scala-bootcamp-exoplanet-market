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

class UserDomainTest extends AnyFlatSpec with Matchers{

  val userValid: User = User(UserName("Jax"), PasswordHash("7c814943b254da2d533159a2cb682c4a4a89c04babe76d0d9ca17a5fe9973340"))
  val userJsonStrValid: String =
    """
      |{
      |"username":"Jax",
      |"passwordHash":"7c814943b254da2d533159a2cb682c4a4a89c04babe76d0d9ca17a5fe9973340"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val userJsonStrInvalid1: String =
    """
      |{
      |"username":"J",
      |"passwordHash":"7c814943b254da2d533159a2cb682c4a4a89c04babe76d0d9ca17a5fe9973340"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")

  val userJsonStrInvalid2: String =
    """
      |{
      |"username":"Jax",
      |"passwordHash":"7c814943bd"
      |}
      |"""
      .stripMargin
      .replaceAll("\r\n","")


  "UserDomain" should "have working encoder" in{
    userValid.asJson.noSpaces must be (userJsonStrValid)
  }

  it should "have working decoder with working validator" in{
    decode[User](userJsonStrValid) must be (userValid.asRight)
    assert(decode[User](userJsonStrInvalid1).isLeft)
    assert(decode[User](userJsonStrInvalid2).isLeft)
  }

  it should "have working EntityDecoder with working validator" in{
    checkEntityDecoder(userJsonStrValid).unsafeRunSync().noSpaces must be (userJsonStrValid)
    checkEntityDecoder(userJsonStrInvalid1).unsafeRunSync().noSpaces must not be userJsonStrInvalid1
    checkEntityDecoder(userJsonStrInvalid2).unsafeRunSync().noSpaces must not be userJsonStrInvalid2
  }

  def checkEntityDecoder(userJsonString: String): IO[Json] = {
    Ok(userJsonString).flatMap(_.as[User])
      .redeemWith({
        case InvalidMessageBodyFailure(_, Some(DecodingFailures(f))) =>
          IO(f.map(_.toString()).asJson)
        case o => IO(o.getMessage.asJson)
      }, v => IO(v.asJson))
  }
}
