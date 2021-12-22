# Exoplanet Market

## Overview
Personal Scala project that concludes the Scala Bootcamp from Evolution training.
Started on December the 7th, 2021.
Due to December the 29th, 2021.

### Project Idea
Exoplanet market. Show users all discovered exoplanets.
Offer to purchase a privilege to name an unnamed exoplanet (official names like _GJ 3942 b_ or _HD 29021 b_ are considered unnamed).
Once purchased let everyone see that name and the user who named it.

### Implementation
- Run an http4s server and create a simple webpage user interface. 
- Fetch exoplanets data from an opensource database and store the stripped down version locally using SQLite database.
Regularly check the online exoplanets data for updates. 
- Authenticate users
- Demonstrate users lists of unnamed and named exoplanets, and let them choose and name one if they so wish.
- Handle financial transactions of authenticated users.
- Once exoplanet is named keep all the changes with the user who named it.

## Exoplanets Source
1) http://exoplanet.eu/catalog/ - **this one is used** in the project because of all (or close to that) discovered
exoplanets can be fetched as one .csv file. (**hmm, site was down for 10 min 07/12/2021**)
2) https://exoplanetarchive.ipac.caltech.edu/docs/data.html - not used in the project, because exoplanets here are
distributed over multiple datasets grouped by the detection method, combining data is too much work.
3) https://github.com/OpenExoplanetCatalogue/open_exoplanet_catalogue/ - Open Exoplanet Catalogue maintained and
populated from NASA Exoplanet Archive. Not used, just added here for completion.

## Useful links
1) https://http4s.org/v0.23/index.html - http4s server quick start
2) https://circe.github.io/circe/ - JSON codecs library
3) https://tpolecat.github.io/doobie/ - database helper, e.g. to work with sql
4) https://www.scala-exercises.org/doobie/connecting_to_database - Doobie helpful examples
5) https://gist.github.com/isyufu/c2136288a41f3567aace09f030a83edd - Doobie + SQL + YOLO gist
6) https://github.com/tpolecat/doobie/issues/795 - How to use pooled Transactor (wrap the whole server)
7) https://index.scala-lang.org/tototoshi/scala-csv/scala-csv/1.3.8?target=_3.x - CSV reading/writing library,
handy for complex CSVs where commas are where not expected
8) https://jwt.io/introduction - JSON Web Token based authentication
9) https://jwt-scala.github.io/jwt-scala/jwt-circe.html - JWT usage with Circe codecs
10) https://flywaydb.org/documentation/concepts/migrations.html - DB migrations. Useful for DB consistency and transparency.

## Current State
- Quickstarted the http4s Server with "HelloWorld" endpoint.
- Manually uploaded the .csv file full of exoplanets data
- Now I have Exoplanet case class
- Now I can read .csv, filter and convert it to objects
- Now I can save objets to the local sql db using Doobie. My database is src/sql/exoplanets.db
- Have a route, which reads DB and returns exoplanets as JSON
- Authentication is enabled using JWT stateless method.
- DB migrations is added
- Case classes have String/Json validation
- Exoplanets can be reserved with timeout by an authenticated user
- Exoplanets can be purchased by an authenticated user
- Purchased exoplanets can be shown

### To do:
1) Write tests!!!!
2) Maybe add some generous offer - pay 2 for 3 names. Then Ill need to store this info somewhere.
Might be unfair when all planets taken.
3) Fetch exoplanets .csv from http://exoplanet.eu/catalog/csv via routes