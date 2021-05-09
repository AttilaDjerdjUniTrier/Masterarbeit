create database faa_db;
#
create database opensky_db;
#
create database sun_db;
#
CREATE TABLE faa_db.local_schema_faa (    
    `N-NUMBER` VARCHAR(64),
    `SERIAL NUMBER` VARCHAR(64),
    `MFR MDL CODE` VARCHAR(64),
    `ENG MFR MDL` VARCHAR(64),
    `YEAR MFR` VARCHAR(64),
    `TYPE REGISTRANT` VARCHAR(64),
    `NAME` VARCHAR(64),
    `STREET` VARCHAR(64),
    `STREET2` VARCHAR(64),
    `CITY` VARCHAR(64),
    `STATE` VARCHAR(64),
    `ZIP CODE` VARCHAR(64),
    `REGION` VARCHAR(64),
    `COUNTY` VARCHAR(64),
    `COUNTRY` VARCHAR(64),
    `LAST ACTION DATE` VARCHAR(64),
    `CERT ISSUE DATE` VARCHAR(64),
    `CERTIFICATION` VARCHAR(64),
    `TYPE AIRCRAFT` VARCHAR(64),
    `TYPE ENGINE` VARCHAR(64),
    `STATUS CODE` VARCHAR(64),
    `MODE S CODE` VARCHAR(64),
    `FRACT OWNER` VARCHAR(64),
    `AIR WORTH DATE` VARCHAR(64),
	`OTHER NAMES(1)` VARCHAR(64),
    `OTHER NAMES(2)` VARCHAR(64),
    `OTHER NAMES(3)` VARCHAR(64),
    `OTHER NAMES(4)` VARCHAR(64),
    `OTHER NAMES(5)` VARCHAR(64),
    `EXPIRATION DATE` VARCHAR(64),
    `U NIQUE ID` VARCHAR(64),
    `KIT MFR` VARCHAR(64),
    `KIT MODEL` VARCHAR(64),
    `MODE S CODE HEX` VARCHAR(64),
    `NONE` VARCHAR(64),
    
    PRIMARY KEY (`MODE S CODE HEX`)
);
#
CREATE TABLE opensky_db.local_schema_opensky (
	`icao24` VARCHAR(64),
	`registration` VARCHAR(64),
	`manufacturericao` VARCHAR(64),
	`manufacturername` VARCHAR(128),
	`model` VARCHAR(64),
	`typecode` VARCHAR(64),
	`serialnumber` VARCHAR(64),
	`linenumber` VARCHAR(64),
	`icaoaircrafttype` VARCHAR(64),
	`operator` VARCHAR(64),
	`operatorcallsign` VARCHAR(64),
	`operatoricao` VARCHAR(64),
	`operatoriata` VARCHAR(64),
	`owner` VARCHAR(128),
	`testreg` VARCHAR(64),
	`registered` VARCHAR(64),
	`reguntil` VARCHAR(64),
	`status` VARCHAR(64),
	`built` VARCHAR(64),
	`firstflightdate` VARCHAR(64),
	`seatconfiguration` VARCHAR(64),
	`engines` VARCHAR(256),
	`modes` VARCHAR(64),
	`adsb` VARCHAR(64),
	`acars` VARCHAR(64),
	`notes` VARCHAR(64),
	`categoryDescription` VARCHAR(64),
  

    PRIMARY KEY(`icao24`)
);
#
CREATE TABLE sun_db.local_schema_sun (    
    `icao` VARCHAR(64),
    `regid` VARCHAR(64),
    `mdl` VARCHAR(64),
    `type` VARCHAR(64),
    `operator` VARCHAR(64),

    PRIMARY KEY (`icao`)
);
#
LOAD DATA INFILE 'C:/Users/faaAircrafts.txt' 
IGNORE INTO TABLE faa_db.local_schema_faa
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;
#
LOAD DATA INFILE 'C:/Users/openSkyAircrafts.csv' 
IGNORE INTO TABLE opensky_db.local_schema_opensky
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;
#
LOAD DATA INFILE 'C:/Users/sunAircrafts.csv' 
IGNORE INTO TABLE sun_db.local_schema_sun
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
IGNORE 1 ROWS;
#
create database global_views;
#
CREATE VIEW global_views.Query_Modell AS
    SELECT 
        sun_db.local_schema_sun.icao AS ICAO,
        sun_db.local_schema_sun.type AS MODELL
    FROM
        sun_db.local_schema_sun
    WHERE
        sun_db.local_schema_sun.type != '' 
    UNION SELECT 
        opensky_db.local_schema_opensky.icao24 AS ICAO,
        opensky_db.local_schema_opensky.model AS MODELL
    FROM
        opensky_db.local_schema_opensky
    WHERE
        opensky_db.local_schema_opensky.model != '';
#
CREATE VIEW global_views.Query_Modell_Besitzer AS
    SELECT 
    sun_db.local_schema_sun.icao AS ICAO,
    sun_db.local_schema_sun.type AS Modell,
    opensky_db.local_schema_opensky.owner AS Besitzer
FROM
    sun_db.local_schema_sun,
    opensky_db.local_schema_opensky
WHERE
    sun_db.local_schema_sun.icao = opensky_db.local_schema_opensky.icao24
        && sun_db.local_schema_sun.type != ''
        && opensky_db.local_schema_opensky.owner != '' 
UNION SELECT 
    sun_db.local_schema_sun.icao,
    sun_db.local_schema_sun.type,
    faa_db.local_schema_faa.NAME
FROM
    sun_db.local_schema_sun,
    faa_db.local_schema_faa
WHERE
    sun_db.local_schema_sun.icao = faa_db.local_schema_faa.`MODE S CODE HEX`
        && sun_db.local_schema_sun.type != ''
        && faa_db.local_schema_faa.NAME != '' 
UNION SELECT 
    opensky_db.local_schema_opensky.icao24,
    opensky_db.local_schema_opensky.model,
    opensky_db.local_schema_opensky.owner
FROM
    opensky_db.local_schema_opensky
WHERE
    opensky_db.local_schema_opensky.model != ''
        && opensky_db.local_schema_opensky.owner != '' 
UNION SELECT 
    opensky_db.local_schema_opensky.icao24,
    opensky_db.local_schema_opensky.model,
    faa_db.local_schema_faa.NAME
FROM
    opensky_db.local_schema_opensky,
    faa_db.local_schema_faa
WHERE
    opensky_db.local_schema_opensky.icao24 = faa_db.local_schema_faa.`MODE S CODE HEX`
        && opensky_db.local_schema_opensky.model != ''
        && faa_db.local_schema_faa.NAME != '';
#
CREATE VIEW global_views.Query_Besitzer_RegAm_RegBis AS
SELECT 
    sun_db.local_schema_sun.icao AS icao,
    sun_db.local_schema_sun.operator AS Besitzer,
    opensky_db.local_schema_opensky.registered AS RegAm,
    opensky_db.local_schema_opensky.reguntil AS RegBis
FROM
    sun_db.local_schema_sun,
    opensky_db.local_schema_opensky
WHERE
    sun_db.local_schema_sun.icao = opensky_db.local_schema_opensky.icao24
        && sun_db.local_schema_sun.operator != ''
        && opensky_db.local_schema_opensky.registered != ''
        && opensky_db.local_schema_opensky.reguntil != '' 
UNION SELECT 
    sun_db.local_schema_sun.icao,
    sun_db.local_schema_sun.operator,
    faa_db.local_schema_faa.`CERT ISSUE DATE`,
    faa_db.local_schema_faa.`EXPIRATION DATE`
FROM
    sun_db.local_schema_sun,
    faa_db.local_schema_faa
WHERE
    sun_db.local_schema_sun.icao = faa_db.local_schema_faa.`MODE S CODE HEX`
        && sun_db.local_schema_sun.operator != ''
        && faa_db.local_schema_faa.`CERT ISSUE DATE` != ''
        && faa_db.local_schema_faa.`EXPIRATION DATE` != '' 
UNION SELECT 
    opensky_db.local_schema_opensky.icao24,
    opensky_db.local_schema_opensky.owner,
    opensky_db.local_schema_opensky.registered,
    opensky_db.local_schema_opensky.reguntil
FROM
    opensky_db.local_schema_opensky
WHERE
    opensky_db.local_schema_opensky.owner != ''
        && opensky_db.local_schema_opensky.registered != ''
        && opensky_db.local_schema_opensky.reguntil != '' 
UNION SELECT 
    opensky_db.local_schema_opensky.icao24,
    opensky_db.local_schema_opensky.owner,
    faa_db.local_schema_faa.`CERT ISSUE DATE`,
    faa_db.local_schema_faa.`EXPIRATION DATE`
FROM
    opensky_db.local_schema_opensky,
    faa_db.local_schema_faa
WHERE
    opensky_db.local_schema_opensky.icao24 = faa_db.local_schema_faa.`MODE S CODE HEX`
        && opensky_db.local_schema_opensky.owner != ''
        && faa_db.local_schema_faa.`CERT ISSUE DATE` != ''
        && faa_db.local_schema_faa.`EXPIRATION DATE` != ""
UNION SELECT 
    faa_db.local_schema_faa.`MODE S CODE HEX`,
    faa_db.local_schema_faa.`NAME`,
    faa_db.local_schema_faa.`CERT ISSUE DATE`,
    faa_db.local_schema_faa.`EXPIRATION DATE`
FROM
    faa_db.local_schema_faa
WHERE
	faa_db.local_schema_faa.`NAME` != ''
        && faa_db.local_schema_faa.`CERT ISSUE DATE` != ''
        && faa_db.local_schema_faa.`EXPIRATION DATE` != ""






