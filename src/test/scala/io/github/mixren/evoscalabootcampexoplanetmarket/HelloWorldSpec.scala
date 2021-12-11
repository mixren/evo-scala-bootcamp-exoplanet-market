package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.IO
import io.github.mixren.evoscalabootcampexoplanetmarket.domain.{Dec, Distance, Exoplanet, OfficialName, Ra, Radius, Year}
import io.github.mixren.evoscalabootcampexoplanetmarket.todelete.{DefRoutes, HelloWorld}
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.implicits._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HelloWorldSpec extends CatsEffectSuite {

  test("HelloWorld returns status code 200") {
    assertIO(retHelloWorld.map(_.status), Status.Ok)
  }

  test("HelloWorld returns hello world message") {
    assertIO(retHelloWorld.flatMap(_.as[String]), "{\"message\":\"Hello, world\"}")
  }

  private[this] val retHelloWorld: IO[Response[IO]] = {
    val getHW = Request[IO](Method.GET, uri"/hello/world")
    val helloWorld = HelloWorld.impl[IO]
    DefRoutes.helloWorldRoutes(helloWorld).orNotFound(getHW)
  }

}

class CodecsTest extends AnyWordSpec with Matchers {
  import io.circe.parser.decode
  import io.circe.syntax.EncoderOps

  private val exoplanet1 = Exoplanet.fromCsvData(13, "6 Leo b", "", "1.7", "148.89", "12.9675", "-9.3554224", "2011")
  private val exoplanet2 = Exoplanet.fromCsvData(1, "11 Com b", "", "", "110.6", "185.17917", "17.792778", "2008")

  private val exoplanetJson1 = exoplanet1.asJson.noSpaces
  private val exoplanetJson2 = exoplanet2.asJson.noSpaces

  private val exoplanetDecoded1 = decode[Exoplanet](exoplanetJson1)
  private val exoplanetDecoded2 = decode[Exoplanet](exoplanetJson2)

  "Exoplanet Codecs" in {
    exoplanetJson1 must be (
      """{"id":13,"officialName":"6 Leo b","mass":null,"radius":1.7,"distance":148.89,"ra":12.9675,"dec":-9.3554224,"discoveryYear":2011}"""
    )
    exoplanetJson2 must be (
      """{"id":1,"officialName":"11 Com b","mass":null,"radius":null,"distance":110.6,"ra":185.17917,"dec":17.792778,"discoveryYear":2008}"""
    )
    exoplanetDecoded1 must be(
      Right(Exoplanet(13,OfficialName("6 Leo b"),None,Some(Radius(1.7)),Some(Distance(148.89)),Some(Ra(12.9675)),Some(Dec(-9.3554224)),Some(Year(2011))))
    )
    exoplanetDecoded2 must be(
      Right(Exoplanet(1,OfficialName("11 Com b"),None,None,Some(Distance(110.6)),Some(Ra(185.17917)),Some(Dec(17.792778)),Some(Year(2008))))
    )
  }
}