
-- -----------------------------------------------------
--  Agent Database
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `device` (
  `deviceId` VARCHAR(45) NOT NULL ,
  `buildingId` VARCHAR(100) NULL DEFAULT NULL,
  `floorId` VARCHAR(100) NULL DEFAULT NULL,
  `xCoordinate` VARCHAR(100) NULL DEFAULT NULL,
  `yCoordinate` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`deviceId`) );



