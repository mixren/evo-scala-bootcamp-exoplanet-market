package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.effect.{ExitCode, IO, IOApp, Ref}
import fs2.Stream
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.jwtoken.JWToken
import org.http4s.ember.client._
import org.http4s.implicits.http4sLiteralsSyntax


object ClientApp extends IOApp {
  val uri = uri"http://localhost:8080/"

  // TODO mb add middleware for storing auth token
  override def run(args: List[String]): IO[ExitCode] = {
    (for {
      client     <- Stream.resource(EmberClientBuilder.default[IO].build)
      ref        <- Stream.eval(Ref.of[IO, Option[JWToken]](None))
      allCalls  = AllCalls[IO](
        ExoplanetsCalls[IO](client, uri),
        UserCalls[IO](client, uri, ref),
        PurchaseCalls[IO](client, uri, ref),
        HelpCalls[IO]()
      )
      _          <- Stream.eval(ConsoleInterface[IO](allCalls).repl)
      //exitCode <- ExitCode.Success
    } yield ()).compile.drain.as(ExitCode.Success)
  }
}
