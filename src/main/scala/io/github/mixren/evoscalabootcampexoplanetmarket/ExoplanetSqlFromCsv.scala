package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.{ExitCode, IOApp}
import io.github.mixren.evoscalabootcampexoplanetmarket.DbConfig._
import io.github.mixren.evoscalabootcampexoplanetmarket.DbCommon._

//import cats._
//import cats.data._
import cats.effect.IO
import doobie._
import doobie.implicits._
import com.github.tototoshi.csv._
import java.io.File


/*object DoobieTry extends IOApp {

  private val xa = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC",
    "jdbc:sqlite:src/sql/ololo.db",
    "",
    ""
  )

  private val y = xa.yolo
  import y._

  private val drop =
    sql"""
    DROP TABLE IF EXISTS person
  """.update.run

  private val create: doobie.ConnectionIO[Int] =
    sql"""
    CREATE TABLE person (
      name TEXT NOT NULL UNIQUE,
      age  INTEGER
    )
  """.update.run


  def insert1(name: String, age: Option[Short]): Update0 =
    sql"insert into person (name, age) values ($name, $age)".update

  case class Person(id: Long, name: String, age: Option[Short])


  override def run(args: List[String]): IO[ExitCode] =
    for {
    _ <- (drop, create).mapN(_ + _).transact(xa)
    _ <- insert1("Alice", Some(12)).run.transact(xa)
    _ <- insert1("Bob", None).quick // switch to YOLO mode
    l <- sql"select rowid, name, age from person".query[Person].to[List].transact(xa)
    _ <- IO.delay(l.foreach(println))
  } yield ExitCode.Success
}*/


object ExoplanetSqlFromCsv extends IOApp {

  private val xa = Transactor.fromDriverManager[IO](
    dbDriverName,
    dbUrl,
    dbUser,
    dbPwd
  )

  private val y = xa.yolo   // To use .quick
  import y._


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
      val indices = colNames.collect( col => data.head.indexOf(col) )
      data.map(row => indices collect row).drop(1)  // first element is column names
    }

  private def generateExoplanets(filteredData: List[List[String]]) =
    IO {
      filteredData.zipWithIndex collect { case (List(name, mass, radius, distance, ra, dec, year), id) =>
        Exoplanet.fromCsvData(id, name, mass, radius, distance, ra, dec, year)
      }
    }


  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- createTableExoplanetsSql.transact(xa)
      data <- parseCsv("src/main/resources/exoplanet.eu_catalog.csv")
      //_ <- IO.delay(data(0).foreach(println))
      filteredData <- filter(data)
      //_ <- IO.delay((List(0, 10, 30) collect filteredData).foreach(l => println(l.mkString(", "))))
      exoplanets <- generateExoplanets(filteredData)
      _ <- IO.delay(println(exoplanets.head.toString))
      _ <- insertExoplanets(exoplanets).quick    // with YOLO
      l <- fetchAllExoplanets().transact(xa)
      _ <- IO.delay((List(0, 10, 30) collect l).foreach(println))
    } yield ExitCode.Success
}