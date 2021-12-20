
create table IF NOT EXISTS purchases (
    id                      INTEGER   AUTO_INCREMENT  PRIMARY KEY,
    exoplanet_official_name TEXT      FOREIGN KEY REFERENCES exoplanets(official_name),
    exoplanet_bought_name   TEXT      NOT NULL,
    username                TEXT      FOREIGN KEY REFERENCES users(username),
    price                   TEXT      NOT NULL,
    timestamp               LONG      NOT NULL
);


