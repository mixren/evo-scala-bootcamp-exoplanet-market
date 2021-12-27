package io.github.mixren.evoscalabootcampexoplanetmarket.client

trait ListFormatter[F[_]] {
  def removeColons(list: List[String]): List[String]
}

object ListFormatter {

  def apply[F[_]: ListFormatter]: ListFormatter[F] = implicitly

  implicit class listFormatterOps[F[_]: ListFormatter](list: List[String]) {
    def removeColons: List[String]             = ListFormatter[F].removeColons(list)
  }

  implicit def ListFormatter[F[_]]: ListFormatter[F] =
    new ListFormatter[F] {
      override def removeColons(list: List[String]): List[String] =
        list.mkString(" ").split(":").toList.map(_.trim).drop(1)
    }
}


