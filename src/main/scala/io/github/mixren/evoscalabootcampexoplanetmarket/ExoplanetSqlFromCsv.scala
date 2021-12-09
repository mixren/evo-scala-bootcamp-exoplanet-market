package io.github.mixren.evoscalabootcampexoplanetmarket

import cats.effect.{ExitCode, IOApp}


import scala.util.Try
//import cats.effect.unsafe.implicits.global
//import cats._
//import cats.data._
import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.implicits._
import com.github.tototoshi.csv._
import java.io.File

//import scala.io.Source

/* library dependecies
   "org.xerial"   % "sqlite-jdbc"
   "org.tpolecat" %% "doobie-core"
   "org.tpolecat" %% "doobie-hikari"
   "org.tpolecat" %% "doobie-specs2"
   "org.tpolecat" %% "doobie-scalatest"
*/

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
    "org.sqlite.JDBC",
    "jdbc:sqlite:src/sql/exoplanets.db",
    "",
    ""
  )

  private val y = xa.yolo

  import y._

  // --------CRUD
  private val create: doobie.ConnectionIO[Int] =
    sql"""
        CREATE TABLE IF NOT EXISTS exoplanet (
          id INTEGER NOT NULL,
          official_name STRING PRIMARY KEY,
          mass_jupiter FLOAT NULL,
          radius_jupiter FLOAT NULL,
          distance_pc FLOAT NULL,
          ra FLOAT NULL,
          dec FLOAT NULL,
          discovery_year INTEGER NULL
        )
    """.update.run

  /*private val drop =
    sql"""
      DROP TABLE IF EXISTS exoplanet
    """.update.run*/

  /*def insertExoplanet(exp: Exoplanet): Update0 =
    sql"""
          INSERT INTO exoplanet(
            id, official_name, mass_jupiter, radius_jupiter,
            distance_pc, ra, dec, discoveryYear)
            values (${exp.id}, ${exp.officialName}, ${exp.mass}, ${exp.radius},
            ${exp.distance}, ${exp.ra}, ${exp.dec}, ${exp.discoveryYear}
          )
    """.update*/

  def insertExoplanets(exps: List[Exoplanet]): doobie.ConnectionIO[Int] = {
    val sql = """
          |INSERT INTO exoplanet(
          |  id, official_name, mass_jupiter, radius_jupiter,
          |  distance_pc, ra, dec, discovery_year)
          |  values (?, ?, ?, ?, ?, ?, ?, ?)
          |  """.stripMargin
    Update[Exoplanet](sql).updateMany(exps)
  }

  private def fetchAll(): doobie.ConnectionIO[List[Exoplanet]] = {
    sql"""SELECT id, official_name, mass_jupiter, radius_jupiter,
          distance_pc, ra, dec, discovery_year FROM exoplanet
    """.query[Exoplanet].to[List]
  }

  // -----Domain
  case class OfficialName(name: String) extends AnyVal
  case class Mass(mass: Float) extends AnyVal
  object Mass {
    def fromString(str: String): Option[Mass] =
      if (str.isEmpty) Option.empty[Mass]          // for long files is better to check first, than throw exceptions
      else Try(str.toFloat).toOption.map(Mass(_))
  }
  case class Radius(radius: Float) extends AnyVal
  object Radius {
    def fromString(str: String): Option[Radius] =
      if (str.isEmpty) Option.empty[Radius]          // for long files is better to check first, than throw exceptions
      else Try(str.toFloat).toOption.map(Radius(_))
  }
  case class Distance(distance: Float) extends AnyVal
  object Distance {
    def fromString(str: String): Option[Distance] =
      if (str.isEmpty) Option.empty[Distance]          // for long files is better to check first, than throw exceptions
      else Try(str.toFloat).toOption.map(Distance(_))
  }
  case class Ra(ra: Float) extends AnyVal
  object Ra {
    def fromString(str: String): Option[Ra] =
      if (str.isEmpty) Option.empty[Ra]           // for long files is better to check first, than throw exceptions
      else Try(str.toFloat).toOption.map(Ra(_))
  }
  case class Dec(dec: Float) extends AnyVal
  object Dec {
    def fromString(str: String): Option[Dec] =
      if (str.isEmpty) Option.empty[Dec]          // for long files is better to check first, than throw exceptions
      else Try(str.toFloat).toOption.map(Dec(_))
  }
  case class Year(year: Int) extends AnyVal
  object Year {
    def fromString(str: String): Option[Year] =
      if (str.isEmpty) Option.empty[Year]         // for long files is better to check first, than throw exceptions
      else Try(str.toInt).toOption.map(Year(_))
  }

  // Some params are options because the original .csv file might not have these values
  case class Exoplanet(id: Int, officialName: OfficialName, mass: Option[Mass],
                       radius: Option[Radius], distance: Option[Distance],
                       ra: Option[Ra], dec: Option[Dec], discoveryYear: Option[Year])
  object Exoplanet {
    def fromCsvData(id: Int, name: String, mass: String, radius: String,
                    distance: String, ra: String, dec: String, year: String): Exoplanet =
      new Exoplanet(
        id,
        OfficialName(name),
        Mass.fromString(mass),
        Radius.fromString(radius),
        Distance.fromString(distance),
        Ra.fromString(ra),
        Dec.fromString(dec),
        Year.fromString(year)
      )
  }

  // ----Methods
  def parseCsv(uri: String = "src/main/resources/exoplanet.eu_catalog.csv") =
    IO {
      val reader = CSVReader.open(new File(uri))
      val data = reader.all()
      reader.close()
//      val source = Source.fromFile(uri)
//      val data = source.getLines().map(_.split(",")).toArray
//      source.close
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
      _ <- create.transact(xa)
      data <- parseCsv("src/main/resources/exoplanet.eu_catalog.csv")
      //_ <- IO.delay(data(0).foreach(println))
      filteredData <- filter(data)
      //_ <- IO.delay(filteredData(0).foreach(println))
      _ <- IO.delay((List(0, 10, 30) collect filteredData).foreach(l => println(l.mkString(", "))))
      exoplanets <- generateExoplanets(filteredData)
      _ <- IO.delay(println(exoplanets.head.toString))

      _ <- insertExoplanets(exoplanets).quick    // with YOLO
      l <- fetchAll().transact(xa)
      _ <- IO.delay((List(0, 10, 30) collect l).foreach(println))
    } yield ExitCode.Success
}