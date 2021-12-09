package io.github.mixren.evoscalabootcampexoplanetmarket

//import cats.effect.{Async, IO, Resource}
//import doobie.hikari.HikariTransactor
//import doobie.{ExecutionContexts, Transactor}
import doobie.Transactor
import cats.effect.IO
import doobie.util.transactor.Transactor.Aux
import io.github.mixren.evoscalabootcampexoplanetmarket.DbConfig._

object DbTransactor {
/*
  /** Simplest `transactor`, slow[er], inefficient for large apps, but OK for testing and learning.
    * Derives transactor from driver.
    *
    * `Transactor` is a means for transformation `ConnectionIO ~> IO`
    */
  def make[F[_]: ContextShift: Async]: Resource[F, Transactor[F]] =
    Blocker[F].map { be =>
      Transactor.fromDriverManager[F](
        driver = dbDriverName,
        url = dbUrl,
        user = dbUser,
        pass = dbPwd,
        blocker = be,
      )
    }

  /** `transactor` backed by connection pool. It uses 3 execution contexts:
    *
    * 1 - for handling queue of connection requests
    *
    * 2 - for handling blocking result retrieval
    *
    * 3 - CPU-bound provided by `ContextShift` (usually `global` from `IOApp`)
    */
  def pooled[F[_]: ContextShift: Async]: Resource[F, Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      be <- Blocker[F]
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = dbDriverName,
        url = dbUrl,
        user = dbUser,
        pass = dbPwd,
        connectEC = ce, // await connection on this EC
        blocker = be, // execute JDBC operations on this EC
      )
    } yield xa
 */

  // Simple transactor (or close to it), but for the commented ones I cant implement Blocker,
  // I cant add cats effect 2 dependency because it asks for the later versions
  def makeXa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    dbDriverName,
    dbUrl,
    dbUser,
    dbPwd
  )
}
