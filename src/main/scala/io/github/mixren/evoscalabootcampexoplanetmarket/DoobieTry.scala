package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.{ExitCode, IOApp}
//import cats.effect.unsafe.implicits.global

import scala.io.Source

/* library dependecies
   "org.xerial" % "sqlite-jdbc" % "3.23.1",
   "org.tpolecat" %% "doobie-core"      % "0.5.3",
   "org.tpolecat" %% "doobie-hikari"    % "0.5.3", // HikariCP transactor.
   "org.tpolecat" %% "doobie-specs2"    % "0.5.3", // Specs2 support for typechecking statements.
   "org.tpolecat" %% "doobie-scalatest" % "0.5.3",  // ScalaTest support for typechecking statements.
*/

object DoobieTry extends IOApp {

  import doobie._
  import doobie.implicits._
  //import cats._
  //import cats.data._
  import cats.effect.IO
  import cats.implicits._

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
}


object ExoplanetCsvToSql extends IOApp {

  import doobie._
  import doobie.implicits._
  //import cats._
  //import cats.data._
  import cats.effect.IO
  import cats.implicits._

  private val xa = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC",
    "jdbc:sqlite:src/sql/ololo.db",
    "",
    ""
  )

  private val y = xa.yolo

  import y._

  // CRUD
  private val drop =
    sql"""
    DROP TABLE IF EXISTS person
  """.update.run

  private val create: doobie.ConnectionIO[Int] =
    sql"""
    CREATE TABLE IF NOT EXISTS person (
      name TEXT NOT NULL UNIQUE,
      age  INTEGER
    )
  """.update.run


  def insert1(name: String, age: Option[Short]): Update0 =
    sql"insert into person (name, age) values ($name, $age)".update

  case class Person(id: Long, name: String, age: Option[Short])


  case class Exoplanet(id: Int, officialName: String, mass: Option[Long], radius: Option[Long],
                       distance: Option[Long], ra: Option[String], dec: Option[String], discoveryYear: Int)

  def parseCsv(uri: String = "src/main/resources/exoplanet.eu_catalog.csv") =
    IO {
      val source = Source.fromFile(uri)
      val data = source.getLines().map(_.split(",")).toArray
      source.close
      data
    }

  private def filter(data: Array[Array[String]]) =
    IO {
      val indices = Array(0, 2, 8, 76, 70, 71, 24)  // csv columns
      data.map(row => indices collect row).drop(1)  // first element is column names
    }

  /*private def generateExoplanets(filteredData: Array[Array[String]]) =
    IO {
      // TODO finish it
      filteredData.map(a => a collect {case Array(name, mass, radius, distance, ra, dec, year) =>
        Exoplanet(1, name, mass, radius, distance,ra ,dec ,year)})
    }*/

  override def run(args: List[String]): IO[ExitCode] =
    for {
      data <- parseCsv("src/main/resources/exoplanet.eu_catalog.csv")
      _ <- IO.delay(data(0).foreach(println))
      filteredData <- filter(data)
      _ <- IO.delay(filteredData(0).foreach(println))
      //exoplanets <- generateExoplanets(filteredData)
      //_ <- insertExoplanets(exoplanets)
      _ <- (drop, create).mapN(_ + _).transact(xa)
      _ <- insert1("Alice", Some(12)).run.transact(xa)
      _ <- insert1("Bob", None).quick // switch to YOLO mode
      l <- sql"select rowid, name, age from person".query[Person].to[List].transact(xa)
      _ <- IO.delay(l.foreach(println))
    } yield ExitCode.Success
}