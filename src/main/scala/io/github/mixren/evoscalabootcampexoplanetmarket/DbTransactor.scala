package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.{Async, Resource}
import doobie.Transactor
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux
import io.github.mixren.evoscalabootcampexoplanetmarket.DbConfig._

object DbTransactor {

  /** `transactor` backed by connection pool. It uses 3 execution contexts:
    *
    * 1 - for handling queue of connection requests
    *
    * 2 - for handling blocking result retrieval
    *
    * 3 - CPU-bound provided by `ContextShift` (usually `global` from `IOApp`)
   *
   * How to use:
   *  transactor.use(42.pure[ConnectionIO].transact[IO]).unsafeRunSync()
   *  transactor.use(xa => 42.pure[ConnectionIO].transact[IO](xa)).unsafeRunSync()
    */
  def pooled[F[_]: Async]: Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = dbDriverName,
        url = dbUrl,
        user = dbUser,
        pass = dbPwd,
        connectEC = ce, // await connection on this EC
      )
    } yield xa


  // Simple transactor (or close to it), but for the commented ones I cant implement Blocker,
  // I cant add cats effect 2 dependency because it asks for the later versions
  def makeXa[F[_]: Async]: Aux[F, Unit] = Transactor.fromDriverManager[F](
    dbDriverName,
    dbUrl,
    dbUser,
    dbPwd
  )

}