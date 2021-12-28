package io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken

import cats.implicits._
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.AuthUser
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import java.time.Clock

object JwtHelper {

  implicit val clock: Clock = Clock.systemUTC
  private val key = "secretKey"
  private val algo = JwtAlgorithm.HS256
  private val expirationSec: Long = 24 * 3600

  /**
   * Encode user info into token. Token is used in the authenticated requests.
   * Uses external library.
   *
   * @return JWT token, which has form:
   *         "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.
   *         eyJleHAiOjE3OTExMjMyNTYsImlhdCI6MTYzMzMzODQ5Nn0.
   *         mvDSTVzGgZvhBf6Iw7zdijJ3bFozj9UeJkelFyr-pws"
   */
  def jwtEncode(authUser: AuthUser): JWToken = {
    val userJson = authUser.asJson.noSpaces
    val claim = JwtClaim(content = userJson)
      .issuedNow
      .expiresIn(expirationSec)
    JWToken(JwtCirce.encode(claim, key, algo))
  }

  def tokenDecode(token: JWToken): Either[String, JwtClaim] = {
    JwtCirce.decode(token.value, key, Seq(algo)).toEither.leftMap(_
    => "Error decoding jwt claim from token. The token might be not valid.")
  }


  def verifyJwtClaims(jwtClaim: JwtClaim): Either[String, AuthUser] = {
    if (jwtClaim.isValid) {
      decode[AuthUser](jwtClaim.content).leftMap(_ => "Error decoding user in JWT claim")
    } else {
      "Token is expired".asLeft
    }

  }

}
