package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import cats.effect.kernel.Async
import cats.implicits.catsSyntaxApply
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepositoryT
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{Exoplanet, ExoplanetOfficialName}

import scala.collection.mutable.ListBuffer

class FakeExoplanetRepository[F[_]: Async] extends ExoplanetRepositoryT[F] {
  var exoplanets: ListBuffer[Exoplanet] = FakeExoplanets.exoSeq.to(ListBuffer)

  override def insertExoplanets(exps: List[Exoplanet]): F[Int] =
    Async[F].delay(exoplanets ++= exps) *> Async[F].pure(0)

  override def fetchAllExoplanets: F[List[Exoplanet]] = Async[F].pure(exoplanets.toList)

  override def deleteAllExoplanets(): F[Int] = Async[F].pure(exoplanets.clear()) *> Async[F].pure(0)

  override def exoplanetByName(exoplanetName: ExoplanetOfficialName): F[Option[Exoplanet]] =
    Async[F].delay(exoplanets.find(_.officialName equals exoplanetName))
}
