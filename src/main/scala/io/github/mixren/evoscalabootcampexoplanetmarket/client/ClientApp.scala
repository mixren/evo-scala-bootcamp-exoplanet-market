package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.effect.{ExitCode, IO, IOApp, Ref}
import fs2.Stream
import io.github.mixren.evoscalabootcampexoplanetmarket.client.calls.{AllCalls, ExoplanetsCalls, HelpCalls, PurchaseCalls, UserCalls}
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import org.http4s.ember.client._
import org.http4s.implicits.http4sLiteralsSyntax

/**
 * Client for the ExoplanetMarket Server.
 * It uses a command line interface, which makes use of all server routes.
 * Tip: When running type "help" and hit Enter to see all Client commands!!
 */
object ClientApp extends IOApp {
  val uri = uri"http://localhost:8080/"

  // TODO mb add middleware for storing auth token
  override def run(args: List[String]): IO[ExitCode] = {
    (for {
      client    <- Stream.resource(EmberClientBuilder.default[IO].build)
      tokenRef  <- Stream.eval(Ref.of[IO, Option[JWToken]](None))
      allCalls  = AllCalls[IO](
        ExoplanetsCalls[IO](client, uri),
        UserCalls[IO](client, uri, tokenRef),
        PurchaseCalls[IO](client, uri, tokenRef),
        HelpCalls[IO]()
      )
      _         <- Stream.eval(ConsoleInterface[IO](allCalls).start)
    } yield ()).compile.drain.as(ExitCode.Success)
  }
}
