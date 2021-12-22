
CREATE TABLE IF NOT EXISTS purchases (
    id                      INTEGER   PRIMARY KEY AUTOINCREMENT,
    exoplanet_official_name TEXT      NOT NULL,
    exoplanet_bought_name   TEXT      NOT NULL,
    username                TEXT      NOT NULL,
    price                   TEXT      NOT NULL,
    timestamp               LONG      NOT NULL,
    FOREIGN KEY (exoplanet_official_name) REFERENCES exoplanets(official_name),
    FOREIGN KEY (username)                REFERENCES users(username)
);


