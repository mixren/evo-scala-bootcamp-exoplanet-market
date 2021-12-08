# Exoplanet Market

## Overview
Personal Scala project that concludes the Scala Bootcamp from Evolution training.
Started on December the 7th, 2021.
Due to ~ December the 26th, 2021.

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
1) http://exoplanet.eu/catalog/ - **this one is used** in the project because of all (or close to that) discovered exoplanets can be fetched as one .csv file. (**hmm, site was down for 10 min 07/12/2021**)
2) https://exoplanetarchive.ipac.caltech.edu/docs/data.html - not used in the project, because exoplanets here are distributed over multiple datasets grouped by the detection method, combining data is too much work.
3) https://github.com/OpenExoplanetCatalogue/open_exoplanet_catalogue/ - Open Exoplanet Catalogue maintained and populated from NASA Exoplanet Archive. Not used, just added here for completion.

## Useful links
1) https://http4s.org/v0.23/index.html - http4s server quick start
2) https://circe.github.io/circe/ - JSON codecs library
3) https://tpolecat.github.io/doobie/ - don't know yet...
4) https://gist.github.com/isyufu/c2136288a41f3567aace09f030a83edd - Doobie + SQL + YOLO gist


## Current State
Quickstarted the http4s Server with "HelloWorld" endpoint. Working on exoplanets' data fetching.

To do:
1) Get the data from the site (CSV file)
2) Create an exoplanet table and then insert the data to the SQLite
3) One endpoint in the application to query the DB ( SQLite ) and display it as a JSON