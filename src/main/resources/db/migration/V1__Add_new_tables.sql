
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



