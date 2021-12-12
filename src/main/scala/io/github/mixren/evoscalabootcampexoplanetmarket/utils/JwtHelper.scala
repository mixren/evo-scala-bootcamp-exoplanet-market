package io.github.mixren.evoscalabootcampexoplanetmarket.utils

import io.github.mixren.evoscalabootcampexoplanetmarket.user.User
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import io.circe.syntax.EncoderOps

import java.time.Instant

object JwtHelper {

  private val key = "secretKey"
  private val algo = JwtAlgorithm.HS256

  /**
   * Uses external library.
   *
   * @param user Login user
   * @return JWT token, which of form:
   *         "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.
   *         eyJleHAiOjE3OTExMjMyNTYsImlhdCI6MTYzMzMzODQ5Nn0.
   *         mvDSTVzGgZvhBf6Iw7zdijJ3bFozj9UeJkelFyr-pws"
   */
  def jwtEncode(user: User): String = {
    val userJson = user.asJson.noSpaces
    val claim = JwtClaim(
      content = userJson,
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )
    // claim: JwtClaim = JwtClaim({}, None, None, None, Some(1791123256), None, Some(1633338496), None)

    JwtCirce.encode(claim, key, algo)
  }

  def jwtDecode(token: String) = {
    // You can decode to JsObject
    JwtCirce.decodeJson(token, key, Seq(JwtAlgorithm.HS256))
    // res8: util.Try[io.circe.Json] = Success(
    //   value = JObject(value = object[exp -> 1791123256,iat -> 1633338496])
    // )
    JwtCirce.decodeJsonAll(token, key, Seq(JwtAlgorithm.HS256))
    // res9: util.Try[(io.circe.Json, io.circe.Json, String)] = Success(
    //   value = (
    //     JObject(value = object[typ -> "JWT",alg -> "HS256"]),
    //     JObject(value = object[exp -> 1791123256,iat -> 1633338496]),
    //     "mvDSTVzGgZvhBf6Iw7zdijJ3bFozj9UeJkelFyr-pws"
    //   )
    // )
    // Or to case classes
    JwtCirce.decode(token, key, Seq(JwtAlgorithm.HS256))
    // res10: util.Try[JwtClaim] = Success(
    //   value = JwtClaim({}, None, None, None, Some(1791123256), None, Some(1633338496), None)
    // )
    JwtCirce.decodeAll(token, key, Seq(JwtAlgorithm.HS256))
    // res11: util.Try[(pdi.jwt.JwtHeader, JwtClaim, String)] = Success(
    //   value = (
    //     JwtHeader(Some(HS256), Some(JWT), None, None),
    //     JwtClaim({}, None, None, None, Some(1791123256), None, Some(1633338496), None),
    //     "mvDSTVzGgZvhBf6Iw7zdijJ3bFozj9UeJkelFyr-pws"
    //   )
    // )
  }

}
