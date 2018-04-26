USE `apromore`;

SET FOREIGN_KEY_CHECKS=0;

-- Pkls are often larger than the default 4194304
SET GLOBAL max_allowed_packet=1073741824;

-- DROP TABLE IF EXISTS `prediction`;
DROP TABLE IF EXISTS `predictive_monitor`;
DROP TABLE IF EXISTS `predictive_monitor_event`;
DROP TABLE IF EXISTS `predictor`;
DROP TABLE IF EXISTS `predictive_monitor_predictor`;

-- CREATE TABLE `prediction` (
--   -- defined in JpaRepository
--   `id`                    int(11) unsigned NOT NULL AUTO_INCREMENT,
--   `predictive_monitor_id` int(11) unsigned NOT NULL,
--   `case_id`               text NOT NULL,
--   `event_nr`              int(11) unsigned NOT NULL,
--   `json`                  text NOT NULL,
--
--   PRIMARY KEY (`id`),
--   CONSTRAINT `fk_prediction_predictive_monitor`
--     FOREIGN KEY (`predictive_monitor_id`) REFERENCES `predictive_monitor` (`id`)
--       ON DELETE CASCADE
--       ON UPDATE CASCADE
-- ) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

CREATE TABLE `predictive_monitor` (
  -- defined in JpaRepository
  `id`   int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

CREATE TABLE `predictive_monitor_event` (
  -- defined in JpaRepository
  `id`                    int(11) unsigned NOT NULL AUTO_INCREMENT,
  `predictive_monitor_id` int(11) unsigned NOT NULL,
  `case_id`               text NOT NULL,
  `event_nr`              int(11) unsigned NOT NULL,
  `json`                  text NOT NULL,

  PRIMARY KEY (`id`),
  CONSTRAINT `fk_predictive_monitor_event_predictive_monitor`
    FOREIGN KEY (`predictive_monitor_id`) REFERENCES `predictive_monitor` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

CREATE TABLE `predictor` (
  -- defined in JpaRepository
  `id`   int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `type` text NOT NULL,
  `pkl`  longblob NOT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

CREATE TABLE `predictive_monitor_predictor` (
  -- defined in JpaRepository
  `predictive_monitor_id` int(11) unsigned NOT NULL,
  `predictor_id`          int(11) unsigned NOT NULL,

  PRIMARY KEY (`predictive_monitor_id`, `predictor_id`),
  CONSTRAINT `fk_predictive_monitor_predictor_predictive_monitor`
    FOREIGN KEY (`predictive_monitor_id`) REFERENCES `predictive_monitor` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  CONSTRAINT `fk_predictive_monitor_predictor_predictor`
    FOREIGN KEY (`predictor_id`) REFERENCES `predictor` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1;
