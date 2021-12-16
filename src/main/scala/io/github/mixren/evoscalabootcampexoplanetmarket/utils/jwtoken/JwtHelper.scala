package io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken

import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.User
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import cats.implicits._
import io.circe.parser.decode
import io.circe.syntax.EncoderOps


import java.time.Clock

object JwtHelper {

  implicit val clock: Clock = Clock.systemUTC
  private val key = "secretKey"
  private val algo = JwtAlgorithm.HS256
  private val expirationSec: Long = 24 * 3600

  /**
   * Uses external library.
   *
   * @param user Login user
   * @return JWT token, which of form:
   *         "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.
   *         eyJleHAiOjE3OTExMjMyNTYsImlhdCI6MTYzMzMzODQ5Nn0.
   *         mvDSTVzGgZvhBf6Iw7zdijJ3bFozj9UeJkelFyr-pws"
   */
  def jwtEncode(user: User): JWToken = {
    val userJson = user.asJson.noSpaces
    val claim = JwtClaim(content = userJson)
      .issuedNow
      .expiresIn(expirationSec)
    JWToken(JwtCirce.encode(claim, key, algo))
  }

  def tokenDecode(token: JWToken): Either[String, JwtClaim] = {
    JwtCirce.decode(token.value, key, Seq(algo)).toEither.leftMap(_ => "Error decoding jwt claim from token")
  }


  def verifyJwtClaims(jwtClaim: JwtClaim): Either[String, User] = {
    if (jwtClaim.isValid) {
      decode[User](jwtClaim.content).leftMap(_ => "Error decoding user in JWT claim")
    } else {
      "Token is expired".asLeft
    }

  }

}
