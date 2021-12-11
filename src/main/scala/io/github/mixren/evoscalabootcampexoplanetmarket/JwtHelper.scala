package io.github.mixren.evoscalabootcampexoplanetmarket

import java.time.Instant
import pdi.jwt.{JwtCirce, JwtAlgorithm, JwtClaim}
import io.circe.syntax.EncoderOps

object JwtHelper {

  /**
   * Uses external library.
   * @param user Login user
   * @return JWT token, which of form:
   *         "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.
   *         eyJleHAiOjE3OTExMjMyNTYsImlhdCI6MTYzMzMzODQ5Nn0.
   *         mvDSTVzGgZvhBf6Iw7zdijJ3bFozj9UeJkelFyr-pws"
   */
  def jwtEncode(user: User): String = {
    val userJson = user.asJson.noSpaces
    println(userJson)
    val claim = JwtClaim(
      content = userJson,
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )
    // claim: JwtClaim = JwtClaim({}, None, None, None, Some(1791123256), None, Some(1633338496), None)
    val key = "secretKey"
    val algo = JwtAlgorithm.HS256
    JwtCirce.encode(claim, key, algo)
  }

}
