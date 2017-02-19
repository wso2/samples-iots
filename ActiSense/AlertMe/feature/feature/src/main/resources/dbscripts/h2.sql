
-- -----------------------------------------------------
--  Agent Database
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `alertme_DEVICE` (
  `alertme_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `DEVICE_NAME` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`alertme_DEVICE_ID`) );

CREATE  TABLE IF NOT EXISTS `SENSE_ALERT_MAPPINGS` (
  `senseme_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `alertme_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `policy` VARCHAR(200),
  PRIMARY KEY (`senseme_DEVICE_ID`,`alertme_DEVICE_ID`) );



