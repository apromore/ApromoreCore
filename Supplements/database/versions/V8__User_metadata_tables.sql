# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.25)
# Database: apromore
# Generation Time: 2020-08-15 05:58:50 +0000
# ************************************************************

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `usermetadata_type`;

CREATE TABLE `usermetadata_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL COMMENT 'Metadata type',
  `version` int(11) DEFAULT NULL COMMENT 'Metadata type',
  `is_valid` tinyint(1) DEFAULT NULL COMMENT 'Indicate whether this record is valid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usermetadata`;

CREATE TABLE `usermetadata` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_id` int(11) NOT NULL COMMENT 'FK User mtadata type id',
  `created_by` varchar(255) DEFAULT NULL COMMENT 'The user create this metadata',
  `created_time` varchar(40) DEFAULT NULL COMMENT 'Create time',
  `updated_by` varchar(255) DEFAULT NULL COMMENT 'The user updated this metadata',
  `updated_time` varchar(40) DEFAULT NULL COMMENT 'Last update time',
  `content` mediumtext COMMENT 'Content of user metadata',
  `revision` int(11) DEFAULT NULL COMMENT 'reserve for optimistic lock',
  `is_valid` tinyint(1) NOT NULL COMMENT 'Indicate whether this record is valid',
  PRIMARY KEY (`id`),
  KEY `type_id` (`type_id`),
  CONSTRAINT `usermetadata_ibfk_2` FOREIGN KEY (`type_id`) REFERENCES `usermetadata_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `usermetadata_type` WRITE;
/*!40000 ALTER TABLE `usermetadata_type` DISABLE KEYS */;

INSERT INTO `usermetadata_type` (`id`, `type`, `version`, `is_valid`)
VALUES
	(1,'FILTER',1,1),
	(2,'DASHBOARD',1,1),
	(3,'CSV_IMPORTER',1,1),
	(4,'LOG_ANIMATION',1,1),
	(5,'DASH_TEMPLATE',1,1);

/*!40000 ALTER TABLE `usermetadata_type` ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS `usermetadata_log`;

CREATE TABLE `usermetadata_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usermetadata_id` int(11) DEFAULT NULL COMMENT 'FK USERMETADATA ID',
  `log_id` int(11) DEFAULT NULL COMMENT 'FK LOG ID',
  PRIMARY KEY (`id`),
  KEY `log_id` (`log_id`),
  KEY `usermetadata_id` (`usermetadata_id`),
  CONSTRAINT `usermetadata_log_ibfk_1` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `usermetadata_log_ibfk_2` FOREIGN KEY (`usermetadata_id`) REFERENCES `usermetadata` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usermetadata_process`;

CREATE TABLE `usermetadata_process` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usermetadata_id` int(11) DEFAULT NULL COMMENT 'FK USERMETADATA ID',
  `process_id` int(11) DEFAULT NULL COMMENT 'FK PROCESS ID',
  PRIMARY KEY (`id`),
  KEY `usermetadata_id` (`usermetadata_id`),
  KEY `process_id` (`process_id`),
  CONSTRAINT `usermetadata_process_ibfk_1` FOREIGN KEY (`usermetadata_id`) REFERENCES `usermetadata` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `usermetadata_process_ibfk_2` FOREIGN KEY (`process_id`) REFERENCES `process` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `group_usermetadata`;

CREATE TABLE `group_usermetadata` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_id` int(11) NOT NULL COMMENT 'FK GROUP ID',
  `usermetadata_id` int(11) NOT NULL COMMENT 'FK USER METADATA ID',
  `has_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Has read permission',
  `has_write` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Has write permission',
  `has_ownership` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Has owner permission',
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  KEY `user_metadata_id` (`usermetadata_id`),
  CONSTRAINT `group_usermetadata_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_usermetadata_ibfk_2` FOREIGN KEY (`usermetadata_id`) REFERENCES `usermetadata` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1;