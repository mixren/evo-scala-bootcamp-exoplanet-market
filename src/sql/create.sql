create table IF NOT EXISTS "EXOPLANETS" ("ID" INTEGER NOT NULL,"OFFICIAL_NAME" VARCHAR NOT NULL,
                                         "MASS_JUPITER" FLOAT NOT NULL,"RADIUS_JUPITER" FLOAT NOT NULL,
                                         "DISTANCE_PC" FLOAT NOT NULL,"RA" VARCHAR NOT NULL,"DEC" VARCHAR NOT NULL,
                                         "DISCOVERY_YEAR" INTEGER NOT NULL);

create table IF NOT EXISTS "PURCHASES" ("ID" INTEGER NOT NULL,"EXOPLANET_OFFICIAL_NAME" VARCHAR NOT NULL,
                                         "BOUGHT_NAME" VARCHAR NOT NULL,"USER" VARCHAR NOT NULL,
                                         "TIMESTAMP" FLOAT NOT NULL);

--In CSV from http://exoplanet.eu/ the columns are:
--   1      3       9           77         71   72        25
-- # name  mass   radius   star_distance   ra   dec   discovered
-- 0 in float == no info
insert into "EXOPLANETS" values (1,'ups And e',     10,   0.000062,47.2,  '01:03:36.0','-55:15:56',2001);
insert into "EXOPLANETS" values (2,'ups And b',     12,   0,       106.3, '',           '',        2002);
insert into "EXOPLANETS" values (3,'tau Cet e',     0,    2.2,     29.26, '12:48:11.0','55:15:06', 2005);
insert into "EXOPLANETS" values (4,'omi UMa A b',   5.9,  1.2,     60.2,  '05:43:12.0','-25:16:53',1998);
insert into "EXOPLANETS" values (5,'ome Ser b',     20.3, 0,       0,     '02:51:11.0','-51:19:16',2019);
insert into "EXOPLANETS" values (6,'mu Ara e',      0.5,  0.003,   0,     '00:32:59.0','-50:45:36',2017);
insert into "EXOPLANETS" values (7,'16 Cyg B b',    11.22,0.028,   200,   '',          '',         2001);
insert into "EXOPLANETS" values (8,'1RXS 1609 b',   21.2, 0.9,     173.26,'',          '',         2010);
insert into "EXOPLANETS" values (9,'1SWASP J1407 b',13,   0,       0,     '20:39:16.0','25:25:26', 2011);
insert into "EXOPLANETS" values (10,'2I/Borisov',   0,    0.77,    0,     '01:13:26.0','25:05:49', 2020);
insert into "EXOPLANETS" values (11,'2M 0441+23 b', 1.8,  0.00639, 87.29, '',          '',         1993);
insert into "EXOPLANETS" values (12,'M2M0838+15 c', 0  ,  0.0509,  109.7, '23:00:01.0','-45:05:11',1990);

--timestamp should be long
insert into "PURCHASES" values (1,'1RXS 1609 b','Goliath','user1', 3545983475);