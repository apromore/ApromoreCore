SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `merged_version`;
DROP TABLE IF EXISTS `derived_version`;
DROP TABLE IF EXISTS `search_history`;
DROP TABLE IF EXISTS `temp_version`;
DROP TABLE IF EXISTS `annotation`;
DROP TABLE IF EXISTS `native`;
DROP TABLE IF EXISTS `edit_session_mapping`;
DROP TABLE IF EXISTS `fragment_version_dag`;
DROP TABLE IF EXISTS `process_fragment_map`;
DROP TABLE IF EXISTS `subcluster`;
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
  `nat_type` varchar(20) NOT NULL DEFAULT '',
  `extension` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`nat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
  `lastname`        varchar(40) DEFAULT NULL,
  `firstname`       varchar(40) DEFAULT NULL,
  `email`           varchar(80) DEFAULT NULL,
  `username`        varchar(10) NOT NULL DEFAULT '',
  `passwd`          varchar(80) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process` (
  `processId`       int(11) NOT NULL AUTO_INCREMENT,
  `name`            varchar(100) DEFAULT NULL,
  `domain`          varchar(40) DEFAULT NULL,
  `owner`           varchar(10) DEFAULT NULL,
  `original_type`   varchar(20) DEFAULT NULL,
  PRIMARY KEY (`processId`),
  CONSTRAINT `fk_process1` FOREIGN KEY (`owner`) REFERENCES `user` (`username`) ON UPDATE CASCADE,
  CONSTRAINT `fk_process2` FOREIGN KEY (`original_type`) REFERENCES `native_type` (`nat_type`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `native` (
  `uri`                         int(11) NOT NULL AUTO_INCREMENT,
  `content`                     longtext,
  `nat_type`                    varchar(20) DEFAULT NULL,
  `process_model_version_id`    int(11) NULL,
  PRIMARY KEY (`uri`),
  UNIQUE KEY `un_native` (`process_model_version_id`,`nat_type`),
  CONSTRAINT `fk_native` FOREIGN KEY (`nat_type`) REFERENCES `native_type` (`nat_type`),
  CONSTRAINT `fk_native3` FOREIGN KEY (`process_model_version_id`) REFERENCES `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `derived_version` (
  `uri_source_version`      varchar(40) NOT NULL DEFAULT '',
  `uri_derived_version`     varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`uri_source_version`,`uri_derived_version`),
  CONSTRAINT `fk_derived_version` FOREIGN KEY (`uri_source_version`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_derived_version1` FOREIGN KEY (`uri_derived_version`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `edit_session_mapping` (
  `code`                int(11) NOT NULL AUTO_INCREMENT,
  `recordTime`          datetime DEFAULT NULL,
  `username`            varchar(10) DEFAULT NULL,
  `uri`                 varchar(40) NOT NULL DEFAULT '',
  `processId`           int(11) DEFAULT NULL,
  `version_name`        varchar(40) DEFAULT NULL,
  `nat_type`            varchar(20) DEFAULT NULL,
  `annotation`          varchar(40) DEFAULT NULL,
  `remove_fake_events`  tinyint(1) DEFAULT NULL,
  `creation_date`       varchar(35) DEFAULT NULL,
  `last_update`         varchar(35) DEFAULT NULL,
  PRIMARY KEY (`code`),
  CONSTRAINT `fk_edit_session_mapping1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edit_session_mapping2` FOREIGN KEY (`uri`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_edit_session_mapping3` FOREIGN KEY (`processId`) REFERENCES `process` (`processId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `merged_version` (
  `uri_merged`      varchar(40) NOT NULL DEFAULT '',
  `uri_source`      varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`uri_merged`,`uri_source`),
  CONSTRAINT `fk_merged_version1` FOREIGN KEY (`uri_merged`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_merged_version2` FOREIGN KEY (`uri_source`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `search_history` (
  `username`        varchar(10) DEFAULT NULL,
  `search`          varchar(200) DEFAULT NULL,
  `num`             int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`num`),
  UNIQUE KEY `un_search` (`username`,`search`),
  CONSTRAINT `fk_search` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `temp_version` (
  `code`            int(11) NOT NULL DEFAULT '0',
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
  PRIMARY KEY (`code`, `processId`, `new_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process_branch` (
  `branch_id`                         int(11) NOT NULL AUTO_INCREMENT,
  `branch_name`                       varchar(1000),
  `process_id`                        int(11),
  `creation_date`                     varchar(35) DEFAULT NULL,
  `last_update`                       varchar(35) DEFAULT NULL,
  `ranking`                           varchar(10) DEFAULT NULL,
  `source_process_model_version_id`   int(11) NULL,
  `current_process_model_version_id`  int(11) NULL,
  constraint `pk_process_branch` primary key (`branch_id`),
  constraint `fk_process_branch` foreign key (`process_id`) references `process` (`processId`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process_model_version` (
    `process_model_version_id`       int(11) NOT NULL AUTO_INCREMENT,
    `branch_id`                      int(11),
    `root_fragment_version_id`       varchar(40),
    `version_number`                 int,
    `version_name`                   varchar(200),
    `change_propagation`             int,
    `lock_status`                    int,
    `num_nodes`                      int,
    `num_edges`                      int,
    CONSTRAINT `pk_process_model_version` primary key (`process_model_version_id`),
    CONSTRAINT `fk_process_branch_model_version` FOREIGN KEY (`branch_id`)     REFERENCES `process_branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `process_branch` add constraint `fk_source_version` foreign key (`source_process_model_version_id`) references `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `process_branch` add constraint `fk_current_version` foreign key (`current_process_model_version_id`) references `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE;


CREATE TABLE `annotation` (
  `uri`                         int(11) NOT NULL AUTO_INCREMENT,
  `native`                      int(11) DEFAULT NULL,
  `process_model_version_id`    int(11) NULL,
  `name`                        varchar(40) DEFAULT NULL,
  `content`                     longtext,
  PRIMARY KEY (`uri`),
  UNIQUE KEY `un_annotation` (`process_model_version_id`,`name`),
  CONSTRAINT `fk_annotation1` FOREIGN KEY (`native`) REFERENCES `native` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_annotation2` FOREIGN KEY (`process_model_version_id`) REFERENCES `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `content` (
    `content_id`                  varchar(40),
    `boundary_s`                  varchar(40),
    `boundary_e`                  varchar(40),
    `code`                        longtext,
    constraint `pk_content` primary key (`content_id`)
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `fragment_version` (
    `fragment_version_id`         varchar(40),
    `content_id`                  varchar(40),
    `child_mapping_code`          varchar(20000),
    `lock_status`                 int,
    `lock_count`                  int,
    `derived_from_fragment`       varchar(40),
    `fragment_size`               int,
    `fragment_type`               varchar(10),
    `cluster_id`                  varchar(40),
    `newest_neighbor`             varchar(40),
    constraint `pk_fragment_version` primary key (`fragment_version_id`),
    constraint `fk_contents_version` foreign key (`content_id`) references `content` (`content_id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process_fragment_map` (
    `mapping_id`                 int(11) NOT NULL AUTO_INCREMENT,
    `process_model_version_id`   int(11),
    `fragment_version_id`        varchar(40),
    constraint `pk_process_fragment_map` primary key (`mapping_id`),
    constraint `fk_process_model_versions_map` foreign key (`process_model_version_id`) references `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_fragment_versions_map` foreign key (`fragment_version_id`) references `fragment_version` (`fragment_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `fragment_version_dag` (
    `fragment_version_id`        varchar(40),
    `child_fragment_version_id`  varchar(40),
    `pocket_id`                  varchar(40),
    constraint `pk_fragment_version_dag` primary key (`fragment_version_id`, `child_fragment_version_id`, `pocket_id`),
    constraint `fk_fragment_version_dag` foreign key (`fragment_version_id`) references `fragment_version` (`fragment_version_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_child_fragment_version_dag` foreign key (`child_fragment_version_id`) references `fragment_version` (`fragment_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `node` (
    `vid`                       int(11) NOT NULL AUTO_INCREMENT,
    `content_id`                varchar(40),
    `sub_version_id`            int(11),
    `vname`                     varchar(2000),
    `vtype`                     varchar(100),
    `ctype`                     varchar(40),
    `configuration`             varchar(1) DEFAULT '0',
    `original_Id`               varchar(40),
    constraint `pk_node` primary key (`vid`),
    constraint `fk_node_content` foreign key (`content_id`) references `content` (`content_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_node_subversion` foreign key (`sub_version_id`) references `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `non_pocket_node` (
    `vid`                       varchar(40),
    constraint `pk_non_pocket_node` primary key (`vid`)
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `edge` (
    `edge_id`                   int(11) NOT NULL AUTO_INCREMENT,
    `content_id`                varchar(40),
    `source_vid`                int(11),
    `target_vid`                int(11),
    `original_id`               varchar(40),
    `cond`                      varchar(2000) NULL,
    `def`                       varchar(1) DEFAULT '0',
    constraint `pk_edge` primary key (`edge_id`),
    constraint `fk_content_edge` foreign key (`content_id`) references `content` (`content_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_source_node` foreign key (`source_vid`) references `node` (`vid`) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_target_node` foreign key (`target_vid`) references `node` (`vid`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  `subcluster` (
    `fragment_version_id`      varchar(40) NOT NULL DEFAULT '',
    `fragment_size`            int(11) DEFAULT NULL,
    `parent_cluster_id`        varchar(80) NOT NULL DEFAULT '',
    `subcluster_id`            varchar(80) DEFAULT NULL,
    constraint `pk_subcluster` PRIMARY KEY (`fragment_version_id`,`parent_cluster_id`),
    constraint `fk_subcluster` foreign key (fragment_version_id) references fragment_version (fragment_version_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `object_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `process_model_version_id` int(11),
    `name`                     varchar(255),
    `configurable`             varchar(1) default '0',
    CONSTRAINT `pk_objtyp` PRIMARY KEY (`id`),
    CONSTRAINT `fk_objtyp_pmv` FOREIGN KEY (`process_model_version_id`) REFERENCES `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `object_type_id`           int(11),
    `name`                     varchar(255),
    `value`                    varchar(255),
    CONSTRAINT `pk_obj_type_att` PRIMARY KEY (`id`),
    CONSTRAINT `fk_obj_type_att_obj` FOREIGN KEY (`object_type_id`) REFERENCES `object_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `resource_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `process_model_version_id` int(11),
    `original_id`              varchar(40),
    `name`                     varchar(255),
    `configurable`             varchar(1) default '0',
    CONSTRAINT `pk_restyp` PRIMARY KEY (`id`),
    CONSTRAINT `fk_restyp_pmv` FOREIGN KEY (`process_model_version_id`) REFERENCES `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `resource_type_id`         int(11),
    `name`                     varchar(255),
    `value`                    varchar(255),
    CONSTRAINT `pk_res_typ_att` PRIMARY KEY (`id`),
    CONSTRAINT `fk_res_type_att_res` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `object_ref_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `object_type_id`           int(11),
    `node_id`                  int(11),
    `type`                     varchar(255),
    `optional`                 varchar(1) default '0',
    `consumed`                 varchar(1) default '0',
    `original_id`              varchar(40),
    CONSTRAINT `pk_objreftyp` PRIMARY KEY (`id`),
    CONSTRAINT `fk_objreftypobj_pmv` FOREIGN KEY (`object_type_id`) REFERENCES `object_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_objreftyp_node` FOREIGN KEY (`node_id`) REFERENCES `node` (`vid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_ref_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `object_ref_type_id`       int(11),
    `name`                     varchar(255),
    `value`                    varchar(255),
    CONSTRAINT `pk_objreftypeatt` PRIMARY KEY (`id`),
    CONSTRAINT `fk_objreftyp_att` FOREIGN KEY (`object_ref_type_id`) REFERENCES `object_ref_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `resource_ref_type` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `resource_type_id`         int(11),
    `node_id`                  int(11),
    `optional`                 varchar(1) default '0',
    `qualifier`                varchar(255),
    CONSTRAINT `pk_resreftyp` PRIMARY KEY (`id`),
    CONSTRAINT `fk_resreftyp_pmv` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_resreftyp_node` FOREIGN KEY (`node_id`) REFERENCES `node` (`vid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_ref_type_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `resource_ref_type_id`     int(11),
    `name`                     varchar(255),
    `value`                    varchar(255),
    CONSTRAINT `pk_resreftypeatt` PRIMARY KEY (`id`),
    CONSTRAINT `fk_resreftype_att` FOREIGN KEY (`resource_ref_type_id`) REFERENCES `resource_ref_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `node_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `node_id`                  int(11),
    `name`                     varchar(255),
    `value`                    varchar(255),
    CONSTRAINT `pk_node_resource_attrib` PRIMARY KEY (`id`),
    CONSTRAINT `fk_node_attributes` FOREIGN KEY (`node_id`) REFERENCES `node` (`vid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edge_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `edge_id`                  int(11),
    `name`                     varchar(255),
    `value`                    varchar(255),
    CONSTRAINT `pk_edge_resource_attrib` PRIMARY KEY (`id`),
    CONSTRAINT `fk_edge_attributes` FOREIGN KEY (`edge_id`) REFERENCES `edge` (`edge_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_model_attribute` (
    `id`                       int(11) NOT NULL AUTO_INCREMENT,
    `process_model_version_id` int(11),
    `name`                     varchar(255),
    `value`                    varchar(255),
    CONSTRAINT `pk_pmv_att` PRIMARY KEY (`id`),
    CONSTRAINT `fk_pmv_att_pmv` FOREIGN KEY (`process_model_version_id`) REFERENCES `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


SET FOREIGN_KEY_CHECKS=1;
