
CREATE TABLE IF NOT EXISTS exoplanets (
    id                INTEGER NOT NULL,
    official_name     TEXT    NOT NULL PRIMARY KEY,
    mass_jupiter      DOUBLE  NULL,
    radius_jupiter    DOUBLE  NULL,
    distance_pc       DOUBLE  NULL,
    ra                DOUBLE  NULL,
    dec               DOUBLE  NULL,
    discovery_year    INTEGER NULL
);

CREATE TABLE IF NOT EXISTS users (
    id                        INTEGER AUTO_INCREMENT,
    username                  TEXT    NOT NULL PRIMARY KEY,
    password                  TEXT    NOT NULL,
    registration_timestamp    LONG    NOT NULL
);


--create table IF NOT EXISTS purchases (
--    id                      INTEGER NOT NULL,
--    exoplanet_official_name VARCHAR NOT NULL,
--    exoplanet_bought_name   VARCHAR NOT NULL,
--    user                    VARCHAR NOT NULL,
--    timestamp               FLOAT   NOT NULL
--);


