package io.github.mixren.evoscalabootcampexoplanetmarket.client

import cats.MonadThrow
import cats.data.{Kleisli, OptionT}
import cats.syntax.all._

trait ConsoleInterface[F[_]] {
  def start: F[Unit]
}

object ConsoleInterface {

  def apply[F[_]: MonadThrow: Console](
                                        calls: Kleisli[OptionT[F, *], List[String], String]
                                      ): ConsoleInterface[F] =
    new ConsoleInterface[F] {
      val greetingMessage: String =
        """
          |================================================================
          |Welcome to the ExoplanetMarket Client App!
          |Here you can name any "unnamed" exoplanet as you wish for money.
          |
          |Tip: type "help" to see all commands.
          |================================================================
          | """.stripMargin

      override def start: F[Unit] = {
        Console[F].putLine(greetingMessage) *> repl
      }

      def repl: F[Unit] = {
        val loop = for {
          line        <- Console[F].readLine // cart add ...
          args         = line.split(" ").toList
          result      <- calls(args).value
          resultString = result.getOrElse("Can't find router for the requested command")
          _           <- Console[F].putLine(resultString)
        } yield ()

        loop.handleErrorWith { error =>
          Console[F].putLine(s"Unexpected error: ${error.getMessage}")
        } >> repl // >> is the same as *> but suits for recursion, i.e. stack safe
      }
    }
}
