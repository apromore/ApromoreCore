SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE `apromore` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE USER 'apromore'@'localhost' IDENTIFIED BY 'MAcri';
CREATE USER 'apromore'@'%' IDENTIFIED BY 'MAcri';
GRANT ALL PRIVILEGES ON apromore.* TO 'apromore'@'localhost' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON apromore.* TO 'apromore'@'%' WITH GRANT OPTION;

USE `apromore`;

DROP TABLE IF EXISTS `merged_version`;
DROP TABLE IF EXISTS `derived_version`;
DROP TABLE IF EXISTS `search_history`;
DROP TABLE IF EXISTS `temp_version`;
DROP TABLE IF EXISTS `annotation`;
DROP TABLE IF EXISTS `native`;
DROP TABLE IF EXISTS `edit_session`;
DROP TABLE IF EXISTS `fragment_version_dag`;
DROP TABLE IF EXISTS `process_fragment_map`;
DROP TABLE IF EXISTS `non_pocket_node`;
DROP TABLE IF EXISTS `node`;
DROP TABLE IF EXISTS `edge`;
DROP TABLE IF EXISTS `fragment_version`;
DROP TABLE IF EXISTS `content`;
DROP TABLE IF EXISTS `native_type`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `process`;
DROP TABLE IF EXISTS `process_branch`;
DROP TABLE IF EXISTS `process_model_version`;

DROP TABLE IF EXISTS `cluster`;
DROP TABLE IF EXISTS `cluster_assignment`;
DROP TABLE IF EXISTS `fragment_distance`;

DROP TABLE IF EXISTS `process_model_attribute`;
DROP TABLE IF EXISTS `resource_type_attribute`;
DROP TABLE IF EXISTS `object_type_attribute`;
DROP TABLE IF EXISTS `node_attribute`;
DROP TABLE IF EXISTS `edge_attribute`;
DROP TABLE IF EXISTS `resource_ref_type`;
DROP TABLE IF EXISTS `resource_ref_type_attribute`;
DROP TABLE IF EXISTS `object_ref_type`;
DROP TABLE IF EXISTS `object_ref_type_attribute`;
DROP TABLE IF EXISTS `resource_type`;
DROP TABLE IF EXISTS `object_type`;



CREATE TABLE `native_type` (
  `id`         int(11) NOT NULL AUTO_INCREMENT,
  `nat_type`   varchar(20) NOT NULL DEFAULT '',
  `extension`  varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
  `id`              int(11) NOT NULL AUTO_INCREMENT,
  `lastname`        varchar(40) DEFAULT NULL,
  `firstname`       varchar(40) DEFAULT NULL,
  `email`           varchar(80) DEFAULT NULL,
  `username`        varchar(10) NOT NULL DEFAULT '',
  `passwd`          varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process` (
  `id`              int(11) NOT NULL AUTO_INCREMENT,
  `name`            varchar(100) DEFAULT NULL,
  `domain`          varchar(40) DEFAULT NULL,
  `owner`           int(11) DEFAULT NULL,
  `original_type`   int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_process1` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)  ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process2` FOREIGN KEY (`original_type`) REFERENCES `native_type` (`id`)  ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `native` (
  `id`                       int(11) NOT NULL AUTO_INCREMENT,
  `content`                  longtext,
  `nat_type`                 int(11) DEFAULT NULL,
  `processModelVersionId`    int(11) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_native` (`processModelVersionId`,`nat_type`),
  CONSTRAINT `fk_native` FOREIGN KEY (`nat_type`) REFERENCES `native_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_native3` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `edit_session` (
  `id`                       int(11) NOT NULL AUTO_INCREMENT,
  `recordTime`               datetime DEFAULT NULL,
  `userId`                   int(11) DEFAULT NULL,
  `processModelVersionId`    int(11) NOT NULL,
  `processId`                int(11) DEFAULT NULL,
  `version_name`             varchar(40) DEFAULT NULL,
  `nat_type`                 varchar(20) DEFAULT NULL,
  `annotation`               varchar(40) DEFAULT NULL,
  `remove_fake_events`       tinyint(1) DEFAULT NULL,
  `creation_date`            varchar(35) DEFAULT NULL,
  `last_update`              varchar(35) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_edit_session1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edit_session2` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edit_session3` FOREIGN KEY (`processId`) REFERENCES `process` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `search_history` (
  `id`            int(11) NOT NULL AUTO_INCREMENT,
  `userId`        int(11) DEFAULT NULL,
  `search`        varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_search` (`userId`,`search`),
  CONSTRAINT `fk_search` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `temp_version` (
  `id`              int(11) NOT NULL DEFAULT '0',
  `recordTime`      datetime DEFAULT NULL,
  `processId`       int(11) NOT NULL DEFAULT '0',
  `pre_version`     varchar(40) DEFAULT NULL,
  `new_version`     varchar(40) NOT NULL DEFAULT '',
  `nat_type`        varchar(20) DEFAULT NULL,
  `creation_date`   varchar(35) DEFAULT NULL,
  `last_update`     varchar(35) DEFAULT NULL,
  `ranking`         varchar(10) DEFAULT NULL,
  `documentation`   longtext,
  `name`            varchar(40) DEFAULT NULL,
  `cpf`             longtext,
  `apf`             longtext,
  `npf`             longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tmp_version` (`id`, `processId`, `new_version`),
  CONSTRAINT `fk_tmp_version1` FOREIGN KEY (`processId`) REFERENCES `process` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process_branch` (
  `id`                                int(11) NOT NULL AUTO_INCREMENT,
  `branch_name`                       varchar(1000),
  `processId`                         int(11) NOT NULL,
  `creation_date`                     varchar(35) DEFAULT NULL,
  `last_update`                       varchar(35) DEFAULT NULL,
  `ranking`                           varchar(10) DEFAULT NULL,
  `sourceProcessModelVersionId`       int(11) NULL,
  `currentProcessModelVersionId`      int(11) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_process_branch` FOREIGN KEY (`processId`) REFERENCES `process` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process_branch2` FOREIGN KEY (`sourceProcessModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process_branch3` FOREIGN KEY (`currentProcessModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process_model_version` (
    `id`                             int(11) NOT NULL AUTO_INCREMENT,
    `branchId`                       int(11),
    `rootFragmentVersionId`          int(11),
    `version_number`                 int,
    `version_name`                   varchar(200),
    `change_propagation`             int,
    `lock_status`                    int,
    `num_nodes`                      int,
    `num_edges`                      int,
    CONSTRAINT `pk_process_model_version` primary key (`id`),
    CONSTRAINT `fk_process_branch_model_version` FOREIGN KEY (`branchId`) REFERENCES `process_branch` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_process_branch_model_version1` FOREIGN KEY (`rootFragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `process_branch` ADD CONSTRAINT `fk_source_version` FOREIGN KEY (`sourceProcessModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `process_branch` ADD CONSTRAINT `fk_current_version` FOREIGN KEY (`currentProcessModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE `annotation` (
  `id`                          int(11) NOT NULL AUTO_INCREMENT,
  `native`                      int(11) DEFAULT NULL,
  `processModelVersionId`       int(11) NULL,
  `name`                        varchar(40) DEFAULT NULL,
  `content`                     longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_annotation` (`processModelVersionId`,`name`),
  CONSTRAINT `fk_annotation1` FOREIGN KEY (`native`) REFERENCES `native` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_annotation2` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `content` (
    `id`                  int(11) NOT NULL AUTO_INCREMENT,
    `boundary_s`          varchar(40),
    `boundary_e`          varchar(40),
    `code`                longtext,
    PRIMARY KEY (`id`)
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `fragment_version` (
    `id`                          int(11) NOT NULL AUTO_INCREMENT,
    `uri`                         varchar(40),
    `contentId`                   int(11),
    `clusterId`                   int(11),
    `child_mapping_code`          varchar(20000),
    `derived_from_fragment`       int(11),
    `lock_status`                 int,
    `lock_count`                  int,
    `fragment_size`               int,
    `fragment_type`               varchar(10),
    `newest_neighbor`             varchar(40),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_contents_version` FOREIGN KEY (`contentId`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_fragment_map` (
    `id`                         int(11) NOT NULL AUTO_INCREMENT,
    `processModelVersionId`      int(11),
    `fragmentVersionId`          int(11),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_process_model_versions_map` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_fragment_versions_map` FOREIGN KEY (`fragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `fragment_version_dag` (
    `id`                         int(11) NOT NULL AUTO_INCREMENT,
    `fragmentVersionId`          int(11),
    `childFragmentVersionId`     int(11),
    `pocketId`                   varchar(40),
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_fragment_version_dag` (`fragmentVersionId`, `childFragmentVersionId`, `pocketId`),
    CONSTRAINT `fk_fragment_version_dag` FOREIGN KEY (`fragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_child_fragment_version_dag` FOREIGN KEY (`childFragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `node` (
    `id`                        int(11) NOT NULL AUTO_INCREMENT,
    `uri`                       varchar(40),
    `contentId`                 int(11),
    `subVersionId`              int(11),
    `name`                      varchar(2000),
    `type`                      varchar(100),
    `ctype`                     varchar(40),
    `configuration`             varchar(1) DEFAULT '0',
    `original_Id`               varchar(40),
    `locator_preset`            varchar(2000),
    `locator_postset`           varchar(2000),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_node_content` FOREIGN KEY (`contentId`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_node_subversion` FOREIGN KEY (`subVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `non_pocket_node` (
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `nodeId`     int(11) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_non_pocket_node` FOREIGN KEY (`nodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `edge` (
    `id`                        int(11) NOT NULL AUTO_INCREMENT,
    `uri`                       varchar(40),
    `contentId`                 int(11) NOT NULL,
    `sourceNodeId`              int(11) NOT NULL,
    `targetNodeId`              int(11) NOT NULL,
    `originalId`                varchar(40),
    `cond`                      varchar(2000) NULL,
    `def`                       varchar(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_content_edge` FOREIGN KEY (`contentId`) REFERENCES `content` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_source_node` FOREIGN KEY (`sourceNodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_target_node` FOREIGN KEY (`targetNodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `object_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `processModelVersionId`    int(11) NOT NULL,
    `name`                     varchar(255),
    `configurable`             varchar(1) default '0',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_objtyp_pmv` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `objectTypeId`             int(11) NOT NULL,
    `name`                     varchar(255),
    `value`                    varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_obj_type_att_obj` FOREIGN KEY (`objectTypeId`) REFERENCES `object_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `resource_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `processModelVersionId`    int(11) NOT NULL,
    `originalId`               varchar(40),
    `name`                     varchar(255),
    `configurable`             varchar(1) default '0',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_restyp_pmv` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `resourceTypeId`           int(11) NOT NULL,
    `name`                     varchar(255),
    `value`                    varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_res_type_att_res` FOREIGN KEY (`resourceTypeId`) REFERENCES `resource_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `object_ref_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `objectTypeId`             int(11) NOT NULL,
    `nodeId`                   int(11) NOT NULL,
    `type`                     varchar(255),
    `optional`                 varchar(1) default '0',
    `consumed`                 varchar(1) default '0',
    `originalId`               varchar(40),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_objreftypobj_pmv` FOREIGN KEY (`objectTypeId`) REFERENCES `object_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_objreftyp_node` FOREIGN KEY (`nodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_ref_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `objectRefTypeId`          int(11) NOT NULL,
    `name`                     varchar(255),
    `value`                    varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_objreftyp_att` FOREIGN KEY (`objectRefTypeId`) REFERENCES `object_ref_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `resource_ref_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `resourceTypeId`           int(11) NOT NULL,
    `nodeId`                   int(11) NOT NULL,
    `optional`                 varchar(1) default '0',
    `qualifier`                varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_resreftyp_pmv` FOREIGN KEY (`resourceTypeId`) REFERENCES `resource_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_resreftyp_node` FOREIGN KEY (`nodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_ref_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `resourceRefTypeId`        int(11) NOT NULL,
    `name`                     varchar(255),
    `value`                    varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_resreftype_att` FOREIGN KEY (`resourceRefTypeId`) REFERENCES `resource_ref_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `node_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `nodeId`                   int(11) NOT NULL,
    `name`                     varchar(255),
    `value`                    varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_node_attributes` FOREIGN KEY (`nodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edge_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `edgeId`                   int(11) NOT NULL,
    `name`                     varchar(255),
    `value`                    varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_edge_attributes` FOREIGN KEY (`edgeId`) REFERENCES `edge` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_model_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `processModelVersionId`    int(11) NOT NULL,
    `name`                     varchar(255),
    `value`                    varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_pmv_att_pmv` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `cluster` (
    `id`                 int(11) NOT NULL AUTO_INCREMENT,
    `size`               int,
    `avg_fragment_size`  float,
    `medoid_id`          varchar(40),
    `benifit_cost_ratio` double,
    `std_effort`         double,
    `refactoring_gain`   int,
    PRIMARY KEY (`id`)
) engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `fragment_version` ADD CONSTRAINT `fk_cluster_version` FOREIGN KEY (`clusterId`) REFERENCES `cluster` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE `fragment_distance` (
    `id`                 int(11) NOT NULL AUTO_INCREMENT,
    `fragmentVersionId1` int(11) NOT NULL,
    `fragmentVersionId2` int(11) NOT NULL,
    `ged`                double,
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_geds` (`fragmentVersionId1`, `fragmentVersionId2`),
    CONSTRAINT `fk_frag_version_1` FOREIGN KEY (`fragmentVersionId1`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_frag_version_2` FOREIGN KEY (`fragmentVersionId2`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `cluster_assignment` (
    `id`                   int(11) NOT NULL AUTO_INCREMENT,
    `clusterId`            int(11) NOT NULL,
    `fragmentVersionId`    int(11) NOT NULL,
    `clone_id`             varchar(40),
    `maximal`              boolean,
    `core_object_nb`       int,
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_cluster_assignments` (`fragmentVersionId`, `clusterId`),
    CONSTRAINT `fk_frag_version_assignment` FOREIGN KEY (`fragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_cluster_assignment` FOREIGN KEY (`clusterId`) REFERENCES `cluster` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) engine=InnoDB DEFAULT CHARSET=utf8;


CREATE INDEX `id_native_type` ON `native_type` (`nat_type`, `extension`) USING BTREE;
CREATE INDEX `id_user_username` ON `user` (`username`) USING BTREE;
CREATE INDEX `id_process_name` ON `process` (`name`) USING BTREE;
CREATE INDEX `id_branch_name` ON `process_branch` (`branch_name`) USING BTREE;
CREATE INDEX `id_pmv_version` ON `process_model_version` (`version_number`) USING BTREE;
CREATE INDEX `id_pmv_name` ON `process_model_version` (`version_name`) USING BTREE;
CREATE INDEX `id_pmv_lock` ON `process_model_version` (`lock_status`) USING BTREE;
CREATE INDEX `id_annotation_name` ON `annotation` (`name`) USING BTREE;
CREATE INDEX `id_fv_lock` ON `fragment_version` (`lock_status`) USING BTREE;
CREATE INDEX `id_fv_sizetype` ON `fragment_version` (`fragment_size`, `fragment_type`) USING BTREE;
CREATE INDEX `id_fvd_pocket` ON `fragment_version_dag` (`pocketId`) USING BTREE;
CREATE INDEX `id_cluster` ON `cluster` (`size`, `avg_fragment_size`, `benifit_cost_ratio`) USING BTREE;
CREATE INDEX `id_fragment_distance` ON `fragment_distance` (`ged`) USING BTREE;


LOCK TABLES `native_type` WRITE;
/*!40000 ALTER TABLE `native_type` DISABLE KEYS */;
INSERT INTO `native_type` VALUES (1,'EPML 2.0','epml');
INSERT INTO `native_type` VALUES (2,'XPDL 2.1','xpdl');
INSERT INTO `native_type` VALUES (3,'PNML 1.3.2', 'pnml');
/*!40000 ALTER TABLE `native_type` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Alshareef','Abdul','aah.shareef@gmail.com','abdul','');
INSERT INTO `user` VALUES (2,NULL,'Anne',NULL,'anne','');
INSERT INTO `user` VALUES (3,'Ter Hofstede','Arthur','arthur@yawlfoundation.org','arthur','');
INSERT INTO `user` VALUES (4,NULL,'Barbara',NULL,'barbara','');
INSERT INTO `user` VALUES (5,'Ekanayake','Chathura',NULL,'chathura',NULL);
INSERT INTO `user` VALUES (6,'Alrashed','Fahad','fahadakr@gmail.com','fahad',NULL);
INSERT INTO `user` VALUES (7,'Fauvet','Marie','marie-christine.fauvet@qut.edu.au','fauvet','');
INSERT INTO `user` VALUES (8,NULL,'guest',NULL,'guest','');
INSERT INTO `user` VALUES (9,NULL,'Hajo',NULL,'hajo','');
INSERT INTO `user` VALUES (10,NULL,'icsoc 2010',NULL,'icsoc','Macri');
INSERT INTO `user` VALUES (11,'James','Cameron','cam.james@gmail.com','james','');
INSERT INTO `user` VALUES (12,'La Rosa','Marcello','m.larosa@qut.edu.au','larosa','');
INSERT INTO `user` VALUES (13,'Garcia-Banuelos','Luciano','lgbanuelos@gmail.com','luciano','');
INSERT INTO `user` VALUES (14,'','Mehrad',NULL,'mehrad','');
INSERT INTO `user` VALUES (15,NULL,'Public',NULL,'public','');
INSERT INTO `user` VALUES (16,NULL,'Reina',NULL,'reina','');
INSERT INTO `user` VALUES (17,'Dijkman','Remco','R.M.Dijkman@tue.nl','remco','');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `search_history` WRITE;
/*!40000 ALTER TABLE `search_history` DISABLE KEYS */;
INSERT INTO `search_history` VALUES (1,12,'airport');
INSERT INTO `search_history` VALUES (2,14,'gold coast');
INSERT INTO `search_history` VALUES (3,14,'goldcoast');
/*!40000 ALTER TABLE `search_history` ENABLE KEYS */;
UNLOCK TABLES;


SET FOREIGN_KEY_CHECKS=1;