package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl

object DatafetchRoutes {


  def dataFetchRoutes[F[_]: Sync](client: Client[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      // Fetch exoplanets .csv from http://exoplanet.eu/catalog/csv and store locally
      case GET -> Root / "fetchexo" =>
        // client.expect[String](uri / "hello" / "world") >>= printLine
        client.toString
        //implicit val csvDecoder: EntityDecoder[F, CSV] = jsonOf[F, CSV]
        //client.expect[CSV](uri"http://exoplanet.eu/catalog/csv/") >>= println
        //  .adaptError{ case t => JokeError(t)} // Prevent Client Json Decoding Failure Leaking
        Ok("Fetch was successful")
    }
  }
}