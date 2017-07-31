-- -----------------------------------------------------
--  Agent Database
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `device` (
  `deviceId` VARCHAR(45) NOT NULL ,
  `buildingId` VARCHAR(100) NULL DEFAULT NULL,
  `floorId` VARCHAR(100) NULL DEFAULT NULL,
  `xCoordinate` VARCHAR(100) NULL DEFAULT NULL,
  `yCoordinate` VARCHAR(100) NULL DEFAULT NULL,
  `lastKnown` BIGINT,
  PRIMARY KEY (`deviceId`) );

CREATE  TABLE IF NOT EXISTS `building` (
  `buildingId` INTEGER NOT NULL AUTO_INCREMENT,
  `buildingName` VARCHAR(100) NULL DEFAULT NULL,
  `owner` VARCHAR(100) NULL DEFAULT NULL,
  `lng` VARCHAR(100) NULL DEFAULT NULL,
  `lat` VARCHAR(100) NULL DEFAULT NULL,
  `numOfFloors` INTEGER NOT NULL,
  PRIMARY KEY (`buildingId`) ) ;

CREATE  TABLE IF NOT EXISTS `floor` (
  `floorNum` INTEGER NOT NULL ,
  `buildingId` INTEGER NOT NULL ,
  `image` BLOB NULL DEFAULT NULL,
  PRIMARY KEY (`buildingId`,`floorNum`),
  FOREIGN KEY (`buildingId`) REFERENCES building(`buildingId`)) ;