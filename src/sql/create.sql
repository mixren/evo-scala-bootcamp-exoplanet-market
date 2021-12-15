--In CSV from http://exoplanet.eu/ the columns are:
--   1      3       9           77         71   72        25
-- # name  mass   radius   star_distance   ra   dec   discovered
-- 0 in float == no info
insert into exoplanets values (1,'ups And e',     10,   0.000062,47.2,  242.595833,-75.343611126,2001);
insert into exoplanets values (2,'ups And b',     12,   0,       106.3, 0,         0,            2002);
insert into exoplanets values (3,'tau Cet e',     0,    2.2,     29.26, 53.2291667,-7.245277792  2005);
insert into exoplanets values (4,'omi UMa A b',   5.9,  1.2,     60.2,  257.73335 ,1.334455,     1998);
insert into exoplanets values (5,'ome Ser b',     20.3, 0,       0,     27.47083  ,50.2258334,   2019);
insert into exoplanets values (6,'mu Ara e',      0.5,  0.003,   0,     1.44647007,5.6664464,    2017);
insert into exoplanets values (7,'16 Cyg B b',    11.22,0.028,   200,   0,         0,            2001);
insert into exoplanets values (8,'1RXS 1609 b',   21.2, 0.9,     173.26,0,         0,            2010);
insert into exoplanets values (9,'1SWASP J1407 b',13,   0,       0,     200.347819,9.134452,     2011);
insert into exoplanets values (10,'2I/Borisov',   0,    0.77,    0,     136.674787,19.9390135139,2020);
insert into exoplanets values (11,'2M 0441+23 b', 1.8,  0.00639, 87.29, 0,         0,            1993);
insert into exoplanets values (12,'M2M0838+15 c', 0  ,  0.0509,  109.7, 86.0835,   15.44490135,  1990);

--timestamp should be long
insert into purchases values (1,'1RXS 1609 b','Goliath','fj094390jf3p9w', 3545983475);

--timestamp should be long
insert into users values (1,'fj094390jf3p9w','user1','user@my.com', 3545983475);