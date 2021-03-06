DROP TABLE IF EXISTS Fielding;

CREATE TABLE Fielding (
    ID INTEGER,
    playerID VARCHAR,
    yearID VARCHAR,
    stint VARCHAR,
    teamID VARCHAR,
    lgID VARCHAR,
    POS VARCHAR,
    G VARCHAR,
    GS VARCHAR,
    InnOuts VARCHAR,
    PO VARCHAR,
    A VARCHAR,
    E VARCHAR,
    DP VARCHAR,
    PB VARCHAR,
    WP VARCHAR,
    SB VARCHAR,
    CS VARCHAR,
    ZR VARCHAR,
    PRIMARY KEY (ID)
) WITH "template=partitioned2, CACHE_NAME=Fielding";
