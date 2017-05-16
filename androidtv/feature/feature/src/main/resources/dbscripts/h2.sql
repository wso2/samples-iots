
-- -----------------------------------------------------
--  Agent Database
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `androidtv_DEVICE` (
  `androidtv_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `DEVICE_NAME` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`androidtv_DEVICE_ID`) );

CREATE TABLE IF NOT EXISTS `edge_DEVICE` (
  `androidtv_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `SERIAL` VARCHAR(45) NOT NULL ,
  `DEVICE_NAME` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`SERIAL`) );



