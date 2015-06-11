# SQL Manager 2005 Lite for MySQL 3.7.0.1
# ---------------------------------------
# Host     : localhost
# Port     : 3306
# Database : pql


SET FOREIGN_KEY_CHECKS=0;

DROP DATABASE IF EXISTS `pql`;

CREATE DATABASE `pql`
    CHARACTER SET 'utf8'
    COLLATE 'utf8_general_ci';

USE `pql`;

#
# Structure for the `jbpt_labels` table : 
#

DROP TABLE IF EXISTS `jbpt_labels`;

CREATE TABLE `jbpt_labels` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `label` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `label` (`label`(5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

delimiter //
CREATE TRIGGER `jbpt_labels_before_del_tr` BEFORE DELETE ON `jbpt_labels`
  FOR EACH ROW
BEGIN
  DELETE FROM pql_tasks_sim WHERE pql_tasks_sim.label_id = OLD.id;
  DELETE FROM pql_tasks WHERE pql_tasks.label_id=OLD.id;
END;//
delimiter ;
#
# Structure for the `jbpt_petri_nets` table : 
#

DROP TABLE IF EXISTS `jbpt_petri_nets`;

CREATE TABLE `jbpt_petri_nets` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` varchar(50) NOT NULL,
  `name` text,
  `description` text,
  `identifier` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`(20)),
  UNIQUE KEY `identifier` (`identifier`(20))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

delimiter //
CREATE TRIGGER `jbpt_petri_nets_before_del_tr` BEFORE DELETE ON `jbpt_petri_nets`
  FOR EACH ROW
BEGIN
  DELETE FROM pql_can_occur WHERE pql_can_occur.net_id=OLD.id;
  DELETE FROM pql_always_occurs WHERE pql_always_occurs.net_id=OLD.id;
  DELETE FROM pql_can_conflict WHERE pql_can_conflict.net_id=OLD.id;
  DELETE FROM pql_can_cooccur WHERE pql_can_cooccur.net_id=OLD.id;
  DELETE FROM pql_total_causal WHERE pql_total_causal.net_id=OLD.id;
  DELETE FROM pql_total_concur WHERE pql_total_concur.net_id=OLD.id;

  DELETE FROM jbpt_petri_nodes WHERE jbpt_petri_nodes.net_id=OLD.id;
END;//
delimiter ;

#
# Structure for the `jbpt_petri_nodes` table : 
#

DROP TABLE IF EXISTS `jbpt_petri_nodes`;

CREATE TABLE `jbpt_petri_nodes` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `net_id` int(11) unsigned NOT NULL,
  `uuid` varchar(50) NOT NULL,
  `name` text,
  `description` text,
  `label_id` int(10) unsigned DEFAULT NULL,
  `is_transition` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `net_id` (`net_id`),
  KEY `label_id` (`label_id`),
  CONSTRAINT `jbpt_nodes_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `jbpt_petri_nodes_fk` FOREIGN KEY (`label_id`) REFERENCES `jbpt_labels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

delimiter //
CREATE TRIGGER `jbpt_petri_nodes_before_del_tr` BEFORE DELETE ON `jbpt_petri_nodes`
  FOR EACH ROW
BEGIN
  DELETE FROM jbpt_petri_markings WHERE jbpt_petri_markings.place_id=OLD.id;
  DELETE FROM jbpt_petri_flow WHERE jbpt_petri_flow.source=OLD.id OR jbpt_petri_flow.target=OLD.id;
END;//
delimiter ;

#
# Structure for the `jbpt_petri_flow` table : 
#

DROP TABLE IF EXISTS `jbpt_petri_flow`;

CREATE TABLE `jbpt_petri_flow` (
  `source` int(11) unsigned NOT NULL,
  `target` int(11) unsigned NOT NULL,
  `name` text,
  `description` text,
  PRIMARY KEY (`source`,`target`),
  KEY `source` (`source`),
  KEY `target` (`target`),
  CONSTRAINT `jbpt_flow_fk_source` FOREIGN KEY (`source`) REFERENCES `jbpt_petri_nodes` (`id`),
  CONSTRAINT `jbpt_flow_fk_target` FOREIGN KEY (`target`) REFERENCES `jbpt_petri_nodes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `jbpt_petri_markings` table : 
#

DROP TABLE IF EXISTS `jbpt_petri_markings`;

CREATE TABLE `jbpt_petri_markings` (
  `place_id` int(11) unsigned NOT NULL,
  `tokens` int(11) unsigned NOT NULL,
  PRIMARY KEY (`place_id`),
  CONSTRAINT `jbpt_petri_markings_fk` FOREIGN KEY (`place_id`) REFERENCES `jbpt_petri_nodes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `pql_tasks` table : 
#

DROP TABLE IF EXISTS `pql_tasks`;

CREATE TABLE `pql_tasks` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `label_id` int(10) unsigned NOT NULL,
  `similarity` double(15,3) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `label_id_and_sim` (`label_id`,`similarity`),
  KEY `label_id` (`label_id`),
  CONSTRAINT `pql_tasks_fk` FOREIGN KEY (`label_id`) REFERENCES `jbpt_labels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

delimiter //
CREATE TRIGGER `pql_tasks_before_del_tr` BEFORE DELETE ON `pql_tasks`
  FOR EACH ROW
BEGIN
  DELETE FROM pql_tasks_sim WHERE pql_tasks_sim.task_id=OLD.id;
END;//
delimiter ;

#
# Structure for the `pql_always_occurs` table : 
#

DROP TABLE IF EXISTS `pql_always_occurs`;

CREATE TABLE `pql_always_occurs` (
  `net_id` int(11) unsigned NOT NULL,
  `task_id` int(10) unsigned NOT NULL,
  `value` tinyint(1) NOT NULL,
  PRIMARY KEY (`net_id`,`task_id`),
  KEY `net_id` (`net_id`),
  KEY `task_id` (`task_id`),
  CONSTRAINT `pql_always_occurs_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_always_occurs_fk1` FOREIGN KEY (`task_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `pql_can_conflict` table : 
#

DROP TABLE IF EXISTS `pql_can_conflict`;

CREATE TABLE `pql_can_conflict` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  `value` tinyint(1) NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `taskB_id` (`taskB_id`),
  CONSTRAINT `pql_can_conflict_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_can_conflict_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_can_conflict_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `pql_can_cooccur` table : 
#

DROP TABLE IF EXISTS `pql_can_cooccur`;

CREATE TABLE `pql_can_cooccur` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  `value` tinyint(1) NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `pql_can_cooccur_fk2` (`taskB_id`),
  CONSTRAINT `pql_can_cooccur_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_can_cooccur_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_can_cooccur_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `pql_can_occur` table : 
#

DROP TABLE IF EXISTS `pql_can_occur`;

CREATE TABLE `pql_can_occur` (
  `net_id` int(11) unsigned NOT NULL,
  `task_id` int(10) unsigned NOT NULL,
  `value` tinyint(1) NOT NULL,
  PRIMARY KEY (`net_id`,`task_id`),
  KEY `net_id` (`net_id`),
  KEY `task_id` (`task_id`),
  CONSTRAINT `pql_can_occurs_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_can_occur_fk` FOREIGN KEY (`task_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `pql_tasks_sim` table : 
#

DROP TABLE IF EXISTS `pql_tasks_sim`;

CREATE TABLE `pql_tasks_sim` (
  `task_id` int(11) unsigned NOT NULL,
  `label_id` int(11) unsigned NOT NULL,
  UNIQUE KEY `task_and_label_ids` (`task_id`,`label_id`),
  KEY `label_id` (`label_id`),
  CONSTRAINT `pql_tasks_sim_fk_label_id` FOREIGN KEY (`label_id`) REFERENCES `jbpt_labels` (`id`),
  CONSTRAINT `pql_tasks_sim_fk_task_id` FOREIGN KEY (`task_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `pql_total_causal` table : 
#

DROP TABLE IF EXISTS `pql_total_causal`;

CREATE TABLE `pql_total_causal` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  `value` tinyint(1) NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `taskB_id` (`taskB_id`),
  CONSTRAINT `pql_total_causal_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_total_causal_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_total_causal_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for the `pql_total_concur` table : 
#

DROP TABLE IF EXISTS `pql_total_concur`;

CREATE TABLE `pql_total_concur` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  `value` tinyint(1) NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `taskB_id` (`taskB_id`),
  CONSTRAINT `pql_total_concurrent_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_total_concurrent_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_total_concurrent_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Definition for the `clear` procedure : 
#

DROP PROCEDURE IF EXISTS `clear`;

delimiter //
CREATE PROCEDURE `clear`()
    DETERMINISTIC
    SQL SECURITY INVOKER
    COMMENT ''
BEGIN
  DELETE FROM `jbpt_petri_nets`;
  
  DELETE FROM `pql_tasks` WHERE `pql_tasks`.`label_id` NOT IN
  (SELECT `jbpt_petri_nodes`.`label_id` FROM `jbpt_petri_nodes`);
  
  DELETE FROM jbpt_labels WHERE `jbpt_labels`.`id` NOT IN
  (SELECT `jbpt_petri_nodes`.`label_id` FROM `jbpt_petri_nodes`);
  
  ALTER TABLE jbpt_petri_nets AUTO_INCREMENT = 1;
  ALTER TABLE jbpt_petri_nodes AUTO_INCREMENT = 1;
  ALTER TABLE jbpt_labels AUTO_INCREMENT = 1;
  ALTER TABLE pql_tasks AUTO_INCREMENT = 1;
END;//
delimiter ;

#
# Definition for the `jbpt_get_all_net_identifiers` procedure : 
#

DROP PROCEDURE IF EXISTS `jbpt_get_all_net_identifiers`;

delimiter //
CREATE PROCEDURE `jbpt_get_all_net_identifiers`()
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  SELECT DISTINCT `jbpt_petri_nets`.`identifier`
  FROM `jbpt_petri_nets`;
END;//
delimiter ;

#
# Definition for the `jbpt_get_net_labels` procedure : 
#

DROP PROCEDURE IF EXISTS `jbpt_get_net_labels`;

delimiter //
CREATE PROCEDURE `jbpt_get_net_labels`(IN identifier TEXT)
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE nid INTEGER;
  
  SELECT id INTO nid FROM jbpt_petri_nets WHERE jbpt_petri_nets.`identifier`=identifier;
  
  SELECT DISTINCT `jbpt_labels`.`label`
  FROM `jbpt_labels`, `jbpt_petri_nodes`
  WHERE `jbpt_petri_nodes`.`net_id`=nid AND
        `jbpt_petri_nodes`.`label_id` IS NOT NULL AND
        `jbpt_labels`.`id` = `jbpt_petri_nodes`.`label_id`;
END;//
delimiter ;

#
# Definition for the `pql_always_occurs_create` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_always_occurs_create`;

delimiter //
CREATE PROCEDURE `pql_always_occurs_create`(IN net_id INTEGER(11), IN task_id INTEGER, IN value BOOLEAN)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DELETE FROM `pql_always_occurs`
  WHERE `pql_always_occurs`.`net_id`=net_id AND `pql_always_occurs`.`task_id`=task_id;
  
  INSERT INTO `pql_always_occurs`
  (`pql_always_occurs`.`net_id`,`pql_always_occurs`.`task_id`, `pql_always_occurs`.`value`)
  VALUES
  (net_id,task_id,value);
END;//
delimiter ;

#
# Definition for the `pql_can_conflict_create` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_can_conflict_create`;

delimiter //
CREATE PROCEDURE `pql_can_conflict_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER, IN value BOOLEAN)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DELETE FROM `pql_can_conflict`
  WHERE `pql_can_conflict`.`net_id`=net_id AND
  `pql_can_conflict`.`taskA_id`=taskA_id AND
  `pql_can_conflict`.`taskB_id`=taskB_id;

  INSERT INTO `pql_can_conflict`
  (`pql_can_conflict`.`net_id`,`pql_can_conflict`.`taskA_id`,`pql_can_conflict`.`taskB_id`,`pql_can_conflict`.`value`)
  VALUES (net_id,taskA_id,taskB_id,value);
END;//
delimiter ;

#
# Definition for the `pql_can_cooccur_create` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_can_cooccur_create`;

delimiter //
CREATE PROCEDURE `pql_can_cooccur_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER, IN value BOOLEAN)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DELETE FROM `pql_can_cooccur`
  WHERE `pql_can_cooccur`.`net_id`=net_id AND
  `pql_can_cooccur`.`taskA_id`=taskA_id AND
  `pql_can_cooccur`.`taskB_id`=taskB_id;

  INSERT INTO `pql_can_cooccur`
  (`pql_can_cooccur`.`net_id`,`pql_can_cooccur`.`taskA_id`,`pql_can_cooccur`.`taskB_id`,`pql_can_cooccur`.`value`)
  VALUES (net_id,taskA_id,taskB_id,value);
END;//
delimiter ;

#
# Definition for the `pql_can_occur_create` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_can_occur_create`;

delimiter //
CREATE PROCEDURE `pql_can_occur_create`(IN net_id INTEGER(11), IN task_id INTEGER, IN value BOOLEAN)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DELETE FROM `pql_can_occur`
  WHERE `pql_can_occur`.`net_id`=net_id AND `pql_can_occur`.`task_id`=task_id;
  
  INSERT INTO `pql_can_occur`
  (`pql_can_occur`.`net_id`,`pql_can_occur`.`task_id`, `pql_can_occur`.`value`)
  VALUES
  (net_id,task_id,value);
END;//
delimiter ;

#
# Definition for the `pql_levenshtein_label_sim_search` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_levenshtein_label_sim_search`;

delimiter //
CREATE PROCEDURE `pql_levenshtein_label_sim_search`(IN label TEXT)
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE x TEXT;
  SET x = TRIM(LOWER(label));
  SELECT jbpt_labels.`label`, 1-`pql_levenshtein`(x,TRIM(LOWER(jbpt_labels.`label`)))/(2*GREATEST(CHAR_LENGTH(x),CHAR_LENGTH(TRIM(LOWER(jbpt_labels.`label`))))) AS sim
  FROM jbpt_labels ORDER BY sim DESC LIMIT 0,10;
END;//
delimiter ;

#
# Definition for the `pql_tasks_get_in_net` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_tasks_get_in_net`;

delimiter //
CREATE PROCEDURE `pql_tasks_get_in_net`(IN net_id INTEGER(11))
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
 SELECT DISTINCT `pql_tasks_sim`.`task_id`
  FROM `pql_tasks_sim`
  WHERE label_id IN
  (
    SELECT label_id
    FROM `jbpt_petri_nets`
    WHERE `jbpt_petri_nets`.`id`=net_id
  );
END;//
delimiter ;

#
# Definition for the `pql_tasks_get_nets` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_tasks_get_nets`;

delimiter //
CREATE PROCEDURE `pql_tasks_get_nets`(IN task_id INTEGER)
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  SELECT DISTINCT net_id
  FROM `jbpt_petri_nodes`, `pql_tasks_sim`
  WHERE `jbpt_petri_nodes`.`is_transition` IS TRUE AND
    `jbpt_petri_nodes`.`label_id`=`pql_tasks_sim`.`label_id` AND
    `pql_tasks_sim`.`task_id`=task_id;
END;//
delimiter ;

#
# Definition for the `pql_tasks_get_sim` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_tasks_get_sim`;

delimiter //
CREATE PROCEDURE `pql_tasks_get_sim`(IN task_id INTEGER(11))
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  SELECT DISTINCT `jbpt_labels`.`label`
  FROM `pql_tasks_sim`,`jbpt_labels`
  WHERE `pql_tasks_sim`.`label_id`=`jbpt_labels`.`id` AND `pql_tasks_sim`.`task_id` = task_id;
END;//
delimiter ;

#
# Definition for the `pql_total_causal_create` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_total_causal_create`;

delimiter //
CREATE PROCEDURE `pql_total_causal_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER, IN value BOOLEAN)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DELETE FROM `pql_total_causal`
  WHERE `pql_total_causal`.`net_id`=net_id AND
  `pql_total_causal`.`taskA_id`=taskA_id AND
  `pql_total_causal`.`taskB_id`=taskB_id;

  INSERT INTO `pql_total_causal`
  (`pql_total_causal`.`net_id`,`pql_total_causal`.`taskA_id`,`pql_total_causal`.`taskB_id`,`pql_total_causal`.`value`)
  VALUES (net_id,taskA_id,taskB_id,value);
END;//
delimiter ;

#
# Definition for the `pql_total_concur_create` procedure : 
#

DROP PROCEDURE IF EXISTS `pql_total_concur_create`;

delimiter //
CREATE PROCEDURE `pql_total_concur_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER, IN value BOOLEAN)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DELETE FROM `pql_total_concur`
  WHERE `pql_total_concur`.`net_id`=net_id AND
  `pql_total_concur`.`taskA_id`=taskA_id AND
  `pql_total_concur`.`taskB_id`=taskB_id;

  INSERT INTO `pql_total_concur`
  (`pql_total_concur`.`net_id`,`pql_total_concur`.`taskA_id`,`pql_total_concur`.`taskB_id`,`pql_total_concur`.`value`)
  VALUES (net_id,taskA_id,taskB_id,value);
END;//
delimiter ;

#
# Definition for the `jbpt_labels_create` function : 
#

DROP FUNCTION IF EXISTS `jbpt_labels_create`;

delimiter //
CREATE FUNCTION `jbpt_labels_create`(label TEXT)
    RETURNS int(11)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result INTEGER;
  
  SET label = TRIM(label);
  
  SELECT `jbpt_labels`.`id` INTO result
  FROM `jbpt_labels`
  WHERE `jbpt_labels`.`label`=label;
  
  IF result IS NOT NULL THEN
    RETURN result;
  END IF;

  INSERT INTO `jbpt_labels` (`jbpt_labels`.`label`)
  VALUES (label);

  SET result = LAST_INSERT_ID();

  RETURN result;
END;//
delimiter ;

#
# Definition for the `jbpt_petri_flow_create` function : 
#

DROP FUNCTION IF EXISTS `jbpt_petri_flow_create`;

delimiter //
CREATE FUNCTION `jbpt_petri_flow_create`(source INTEGER(11), target INTEGER(11), name TEXT, description TEXT)
    RETURNS tinyint(1)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  INSERT INTO `jbpt_petri_flow`
  (`jbpt_petri_flow`.`source`,`jbpt_petri_flow`.`target`,`jbpt_petri_flow`.`name`,`jbpt_petri_flow`.`description`)
  VALUES
  (source,target,name,description);
  
  RETURN TRUE;
END;//
delimiter ;

#
# Definition for the `jbpt_petri_markings_create` function : 
#

DROP FUNCTION IF EXISTS `jbpt_petri_markings_create`;

delimiter //
CREATE FUNCTION `jbpt_petri_markings_create`(place_id INTEGER(11), tokens INTEGER(11))
    RETURNS tinyint(1)
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  INSERT INTO `jbpt_petri_markings`
  (`jbpt_petri_markings`.`place_id`,`jbpt_petri_markings`.`tokens`)
  VALUES
  (place_id,tokens);

  RETURN TRUE;
END;//
delimiter ;

#
# Definition for the `jbpt_petri_nets_create` function : 
#

DROP FUNCTION IF EXISTS `jbpt_petri_nets_create`;

delimiter //
CREATE FUNCTION `jbpt_petri_nets_create`(uuid VARCHAR(50), name TEXT, description TEXT, identifier TEXT)
    RETURNS int(11)
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  INSERT INTO `jbpt_petri_nets`
  (`jbpt_petri_nets`.`uuid`,`jbpt_petri_nets`.name,`jbpt_petri_nets`.description,`jbpt_petri_nets`.identifier)
  VALUES
  (uuid,name,description,identifier);

  RETURN LAST_INSERT_ID();
END;//
delimiter ;

#
# Definition for the `jbpt_petri_nets_delete` function : 
#

DROP FUNCTION IF EXISTS `jbpt_petri_nets_delete`;

delimiter //
CREATE FUNCTION `jbpt_petri_nets_delete`(identifier TEXT)
    RETURNS int(11)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE delID INTEGER;
  
  SELECT id INTO delID FROM jbpt_petri_nets WHERE `jbpt_petri_nets`.identifier = identifier;
  
  IF delID IS NULL THEN
    RETURN 0;
  END IF;
  
  DELETE FROM jbpt_petri_nets WHERE `jbpt_petri_nets`.`id` = delID;
  
  DELETE FROM jbpt_labels WHERE
    (NOT(`jbpt_labels`.`id` IN (
  SELECT
    DISTINCT `jbpt_petri_nodes`.`label_id`
  FROM
    `jbpt_petri_nodes`
  WHERE
    (`jbpt_petri_nodes`.`label_id` is not null))));

  RETURN delID;
END;//
delimiter ;

#
# Definition for the `jbpt_petri_nets_identifier2id` function : 
#

DROP FUNCTION IF EXISTS `jbpt_petri_nets_identifier2id`;

delimiter //
CREATE FUNCTION `jbpt_petri_nets_identifier2id`(identifier TEXT)
    RETURNS int(11)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result INTEGER;

  SELECT `jbpt_petri_nets`.`id` INTO result
  FROM `jbpt_petri_nets`
  WHERE `jbpt_petri_nets`.`identifier`=identifier;

  IF result IS NULL THEN
    return 0;
  END IF;
    
  RETURN result;
END;//
delimiter ;

#
# Definition for the `jbpt_petri_nets_uuid2id` function : 
#

DROP FUNCTION IF EXISTS `jbpt_petri_nets_uuid2id`;

delimiter //
CREATE FUNCTION `jbpt_petri_nets_uuid2id`(uuid VARCHAR(50))
    RETURNS tinyint(4)
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result INTEGER;

  SELECT `jbpt_petri_nets`.`id` INTO result
  FROM `jbpt_petri_nets`
  WHERE `jbpt_petri_nets`.`uuid`=uuid;

  RETURN result;
END;//
delimiter ;

#
# Definition for the `jbpt_petri_nodes_create` function : 
#

DROP FUNCTION IF EXISTS `jbpt_petri_nodes_create`;

delimiter //
CREATE FUNCTION `jbpt_petri_nodes_create`(net_id INTEGER(11), uuid VARCHAR(50), name TEXT, description TEXT, label TEXT, is_transition BOOLEAN)
    RETURNS int(11)
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE labelID INTEGER;
  
  IF label = "" THEN
    SET labelID = NULL;
  ELSE
    SET labelID = jbpt_labels_create(label);
  END IF;

  INSERT INTO `jbpt_petri_nodes`
  (`jbpt_petri_nodes`.`net_id`,`jbpt_petri_nodes`.`uuid`,`jbpt_petri_nodes`.name,`jbpt_petri_nodes`.description,`jbpt_petri_nodes`.label_id,`jbpt_petri_nodes`.is_transition)
  VALUES
  (net_id,uuid,name,description,labelID,is_transition);

  RETURN LAST_INSERT_ID();
END;//
delimiter ;

#
# Definition for the `pql_always_occurs` function : 
#

DROP FUNCTION IF EXISTS `pql_always_occurs`;

delimiter //
CREATE FUNCTION `pql_always_occurs`(net_id INTEGER(11), task_id INTEGER)
    RETURNS tinyint(1)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result tinyint(1);

  SELECT `pql_always_occurs`.`value` INTO result
  FROM `pql_always_occurs` WHERE `pql_always_occurs`.`net_id`=net_id AND `pql_always_occurs`.`task_id`=task_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;
  
  RETURN result;
END;//
delimiter ;

#
# Definition for the `pql_can_conflict` function : 
#

DROP FUNCTION IF EXISTS `pql_can_conflict`;

delimiter //
CREATE FUNCTION `pql_can_conflict`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER)
    RETURNS tinyint(1)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result tinyint(1);

  SELECT `pql_can_conflict`.`value` INTO result
  FROM `pql_can_conflict` WHERE `pql_can_conflict`.`net_id`=net_id AND `pql_can_conflict`.`taskA_id`=taskA_id AND `pql_can_conflict`.`taskB_id`=taskB_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;

  RETURN result;
END;//
delimiter ;

#
# Definition for the `pql_can_cooccur` function : 
#

DROP FUNCTION IF EXISTS `pql_can_cooccur`;

delimiter //
CREATE FUNCTION `pql_can_cooccur`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER)
    RETURNS tinyint(1)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result tinyint(1);

  SELECT `pql_can_cooccur`.`value` INTO result
  FROM `pql_can_cooccur` WHERE `pql_can_cooccur`.`net_id`=net_id AND `pql_can_cooccur`.`taskA_id`=taskA_id AND `pql_can_cooccur`.`taskB_id`=taskB_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;

  RETURN result;
END;//
delimiter ;

#
# Definition for the `pql_can_occur` function : 
#

DROP FUNCTION IF EXISTS `pql_can_occur`;

delimiter //
CREATE FUNCTION `pql_can_occur`(net_id INTEGER(11), task_id INTEGER)
    RETURNS tinyint(1)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result tinyint(1);

  SELECT `pql_can_occur`.`value` INTO result
  FROM `pql_can_occur` WHERE `pql_can_occur`.`net_id`=net_id AND `pql_can_occur`.`task_id`=task_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;
  
  RETURN result;
END;//
delimiter ;

#
# Definition for the `pql_levenshtein` function : 
#

DROP FUNCTION IF EXISTS `pql_levenshtein`;

delimiter //
CREATE FUNCTION `pql_levenshtein`(s1 TEXT, s2 TEXT)
    RETURNS int(11)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
    -- The implementation below, thanks to Jason Rust
    -- Taken from http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#MySQL
    -- on 22 April 2015
    DECLARE s1_len, s2_len, i, j, c, c_temp, cost INT;
    DECLARE s1_char CHAR;
    DECLARE cv0, cv1 TEXT; -- change from VARBINARY(256) to TEXT
    SET s1_len = CHAR_LENGTH(s1), s2_len = CHAR_LENGTH(s2), cv1 = 0x00, j = 1, i = 1, c = 0;
    IF s1 = s2 THEN
      RETURN 0;
    ELSEIF s1_len = 0 THEN
      RETURN s2_len;
    ELSEIF s2_len = 0 THEN
      RETURN s1_len;
    ELSE
      WHILE j <= s2_len DO
        SET cv1 = CONCAT(cv1, UNHEX(HEX(j))), j = j + 1;
      END WHILE;
      WHILE i <= s1_len DO
        SET s1_char = SUBSTRING(s1, i, 1), c = i, cv0 = UNHEX(HEX(i)), j = 1;
        WHILE j <= s2_len DO
          SET c = c + 1;
          IF s1_char = SUBSTRING(s2, j, 1) THEN
            SET cost = 0; ELSE SET cost = 1;
          END IF;
          SET c_temp = CONV(HEX(SUBSTRING(cv1, j, 1)), 16, 10) + cost;
          IF c > c_temp THEN SET c = c_temp; END IF;
          SET c_temp = CONV(HEX(SUBSTRING(cv1, j+1, 1)), 16, 10) + 1;
          IF c > c_temp THEN
            SET c = c_temp;
          END IF;
          SET cv0 = CONCAT(cv0, UNHEX(HEX(c))), j = j + 1;
        END WHILE;
        SET cv1 = cv0, i = i + 1;
      END WHILE;
    END IF;
    RETURN c;
  END;//
delimiter ;

#
# Definition for the `pql_tasks_create` function : 
#

DROP FUNCTION IF EXISTS `pql_tasks_create`;

delimiter //
CREATE FUNCTION `pql_tasks_create`(label TEXT, similarity DOUBLE)
    RETURNS int(11)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result INTEGER;
  DECLARE labelID INTEGER;

  SET labelID = jbpt_labels_create(label);
    
  SELECT `pql_tasks`.`id` INTO result
  FROM `pql_tasks`
  WHERE `pql_tasks`.`label_id`=labelID AND `pql_tasks`.`similarity`=similarity;

  IF result IS NOT NULL THEN
    RETURN result;
  END IF;

  INSERT INTO `pql_tasks` (`pql_tasks`.`label_id`,`pql_tasks`.`similarity`)
  VALUES (labelID,similarity);

  SET result = LAST_INSERT_ID();

  RETURN result;
END;//
delimiter ;

#
# Definition for the `pql_tasks_get` function : 
#

DROP FUNCTION IF EXISTS `pql_tasks_get`;

delimiter //
CREATE FUNCTION `pql_tasks_get`(label TEXT, sim DOUBLE, threshold DOUBLE)
    RETURNS int(11)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE labelID INTEGER;
  DECLARE result INTEGER;
  
  SET labelID = jbpt_labels_create(label);

  SELECT id INTO result
  FROM
  (SELECT id,label_id,ABS(similarity-sim) AS distance
  FROM pql_tasks
  WHERE label_id=labelID AND ABS(similarity-sim)<threshold
  ORDER BY distance ASC
  LIMIT 0,1) AS tbl;
  
  IF result IS NULL THEN
    RETURN 0;
  END IF;
  
  RETURN result;
END;//
delimiter ;

#
# Definition for the `pql_tasks_sim_create` function : 
#

DROP FUNCTION IF EXISTS `pql_tasks_sim_create`;

delimiter //
CREATE FUNCTION `pql_tasks_sim_create`(labelA TEXT, labelB TEXT, similarity DOUBLE)
    RETURNS int(11)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE labelAid INTEGER;
  DECLARE labelBid INTEGER;
  DECLARE taskID INTEGER;
  DECLARE result INTEGER;

  SET labelAid = jbpt_labels_create(labelA);
  SET labelBid = jbpt_labels_create(labelB);
  
  SET taskID = pql_tasks_create(labelA,similarity);

  SELECT `pql_tasks_sim`.`task_id` INTO result
  FROM `pql_tasks_sim`
  WHERE `pql_tasks_sim`.`task_id`=taskID AND `pql_tasks_sim`.`label_id`=labelBid;
  
  IF result IS NOT NULL THEN
    RETURN 0;
  END IF;
  
  INSERT INTO `pql_tasks_sim` (`pql_tasks_sim`.`task_id`,`pql_tasks_sim`.`label_id`)
  VALUES (taskID,labelBid);

  RETURN taskID;
END;//
delimiter ;

#
# Definition for the `pql_total_causal` function : 
#

DROP FUNCTION IF EXISTS `pql_total_causal`;

delimiter //
CREATE FUNCTION `pql_total_causal`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER)
    RETURNS tinyint(1)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result tinyint(1);

  SELECT `pql_total_causal`.`value` INTO result
  FROM `pql_total_causal` WHERE `pql_total_causal`.`net_id`=net_id AND `pql_total_causal`.`taskA_id`=taskA_id AND `pql_total_causal`.`taskB_id`=taskB_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;

  RETURN result;
END;//
delimiter ;

#
# Definition for the `pql_total_concur` function : 
#

DROP FUNCTION IF EXISTS `pql_total_concur`;

delimiter //
CREATE FUNCTION `pql_total_concur`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER)
    RETURNS tinyint(1)
    DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
  DECLARE result tinyint(1);

  SELECT `pql_total_concur`.`value` INTO result
  FROM `pql_total_concur` WHERE `pql_total_concur`.`net_id`=net_id AND `pql_total_concur`.`taskA_id`=taskA_id AND `pql_total_concur`.`taskB_id`=taskB_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;

  RETURN result;
END;//
delimiter ;

#
# Definition for the `jbpt_unused_labels` view : 
#

DROP VIEW IF EXISTS `jbpt_unused_labels`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `jbpt_unused_labels` AS 
  select 
    `jbpt_labels`.`id` AS `label_id` 
  from 
    `jbpt_labels` 
  where 
    (not(`jbpt_labels`.`id` in (
  select 
    distinct `jbpt_petri_nodes`.`label_id` 
  from 
    `jbpt_petri_nodes` 
  where 
    (`jbpt_petri_nodes`.`label_id` is not null))));

