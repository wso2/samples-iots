-- -----------------------------------------------------
--  Agent Database
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alertme_DEVICE` (
  `alertme_DEVICE_ID` VARCHAR(45)  NOT NULL,
  `DEVICE_NAME`       VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`alertme_DEVICE_ID`)
);

CREATE TABLE IF NOT EXISTS `SENSE_ALERT_MAPPINGS` (
  `alertme_DEVICE_ID` VARCHAR(45)  NOT NULL,
  `senseme_DEVICE_ID` VARCHAR(45)  NOT NULL,
  `distance`          INTEGER      NOT NULL,
  `duration`          INTEGER      NOT NULL,
  'TENANT_DOMAIN'     VARCHAR(100) NOT NULL,
  PRIMARY KEY (`alertme_DEVICE_ID`)
);
