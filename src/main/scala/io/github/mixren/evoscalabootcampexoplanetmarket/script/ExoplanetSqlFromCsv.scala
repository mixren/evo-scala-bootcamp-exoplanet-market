package io.github.mixren.evoscalabootcampexoplanetmarket.script

import cats.effect.{ExitCode, IO, IOApp}
import com.github.tototoshi.csv.CSVReader
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.db.{DbTransactor, DbFlywayMigrator}
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.ExoplanetRepository
import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.Exoplanet

import java.io.File

/**
  * Supportive script.
  *
  * Read raw .csv file downloaded from http://exoplanet.eu/catalog/
  * and stored as src/main/resources/exoplanet.eu_catalog.csv,
  * filter it and create 'exoplanets' table in local sql database.
  */
object ExoplanetSqlFromCsv extends IOApp {

  // ----Methods
  def parseCsv(uri: String = "src/main/resources/exoplanet.eu_catalog.csv") =
    IO {
      val reader = CSVReader.open(new File(uri))
      val data = reader.all()
      reader.close()
      data
    }

  private def filter(data: List[List[String]]) =
    IO {
      // # name  mass   radius   star_distance   ra   dec   discovered
      val colNames = List("# name", "mass", "radius", "star_distance", "ra", "dec", "discovered") // csv columns of interest
      val indices = colNames.collect(col => data.head.indexOf(col))
      data.map(row => indices collect row).drop(1) // first element is column names
    }

  private def generateExoplanets(filteredData: List[List[String]]) =
    IO {
      filteredData.zipWithIndex collect { case (List(name, mass, radius, distance, ra, dec, year), id) =>
        Exoplanet.fromCsvData(id + 1, name, mass, radius, distance, ra, dec, year)
      }
    }

  val dbMigrator = new DbFlywayMigrator[IO]

  override def run(args: List[String]): IO[ExitCode] = {
    DbTransactor.pooled[IO].use { implicit xa =>
      val repo = new ExoplanetRepository[IO]
      for {
        _ <- dbMigrator.migrate()
        _ <- repo.deleteAllExoplanets()
        data <- parseCsv("src/main/resources/exoplanet.eu_catalog.csv")
        //_             <- IO.delay(data(0).foreach(println))
        filteredData <- filter(data)
        //_             <- IO.delay((List(0, 10, 30) collect filteredData).foreach(l => println(l.mkString(", "))))
        exoplanets <- generateExoplanets(filteredData)
        _ <- IO.delay(println(exoplanets.head.toString))
        nIns <- repo.insertExoplanets(exoplanets)
        _ <- IO.delay(println(s"Inserted rows: $nIns"))
        l <- repo.fetchAllExoplanets
        _ <- IO.delay((List(0, 10, 30) collect l).foreach(println))
      } yield ExitCode.Success
    }
  }
}
