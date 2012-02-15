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
DROP TABLE IF EXISTS `non_pocket_vertex`;
DROP TABLE IF EXISTS `vertex`;
DROP TABLE IF EXISTS `edge`;
DROP TABLE IF EXISTS `fragment_version`;
DROP TABLE IF EXISTS `fragment`;
DROP TABLE IF EXISTS `content`;
DROP TABLE IF EXISTS `canonical`;

ALTER TABLE `native_type` DISABLE KEYS;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `native_type`;

ALTER TABLE `user` DISABLE KEYS;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `user`;

ALTER TABLE `process` DISABLE KEYS;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `process`;

ALTER TABLE `process_branch` DISABLE KEYS;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `process_branch`;

ALTER TABLE `process_model_version` DISABLE KEYS;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `process_model_version`;


CREATE TABLE `native_type` (
  `nat_type` varchar(20) NOT NULL DEFAULT '',
  `extension` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`nat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `user` (
  `lastname`        varchar(40) DEFAULT NULL,
  `firstname`       varchar(40) DEFAULT NULL,
  `email`           varchar(80) DEFAULT NULL,
  `username`        varchar(10) NOT NULL DEFAULT '',
  `passwd`          varchar(80) DEFAULT NULL,
  PRIMARY KEY (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;


CREATE TABLE `process` (
  `processId`       int(11) NOT NULL AUTO_INCREMENT,
  `name`            varchar(100) DEFAULT NULL,
  `domain`          varchar(40) DEFAULT NULL,
  `owner`           varchar(10) DEFAULT NULL,
  `original_type`   varchar(20) DEFAULT NULL,
  PRIMARY KEY (`processId`),
  CONSTRAINT `fk_process1` FOREIGN KEY (`owner`) REFERENCES `user` (`username`) ON UPDATE CASCADE,
  CONSTRAINT `fk_process2` FOREIGN KEY (`original_type`) REFERENCES `native_type` (`nat_type`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `canonical` (
  `uri`             varchar(40) NOT NULL DEFAULT '',
  `processId`       int(11) DEFAULT NULL,
  `version_name`    varchar(20) DEFAULT NULL,
  `author`          varchar(40) DEFAULT NULL,
  `creation_date`   varchar(35) DEFAULT NULL,
  `last_update`     varchar(35) DEFAULT NULL,
  `ranking`         varchar(10) DEFAULT NULL,
  `documentation`   text,
  `content`         longtext,
  PRIMARY KEY (`uri`),
  UNIQUE KEY `un_canonical` (`processId`, `version_name`),
  CONSTRAINT `fk_canonical` FOREIGN KEY (`processId`) REFERENCES `process` (`processId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `native` (
  `uri`             int(11) NOT NULL AUTO_INCREMENT,
  `content`         longtext,
  `nat_type`        varchar(20) DEFAULT NULL,
  `canonical`       varchar(40) DEFAULT NULL,
  PRIMARY KEY (`uri`),
  UNIQUE KEY `un_native` (`canonical`,`nat_type`),
  CONSTRAINT `fk_native` FOREIGN KEY (`nat_type`) REFERENCES `native_type` (`nat_type`),
  CONSTRAINT `fk_native3` FOREIGN KEY (`canonical`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `annotation` (
  `uri`             int(11) NOT NULL AUTO_INCREMENT,
  `native`          int(11) DEFAULT NULL,
  `canonical`       varchar(40) DEFAULT NULL,
  `name`            varchar(40) DEFAULT NULL,
  `content`         text,
  PRIMARY KEY (`uri`),
  UNIQUE KEY `un_annotation` (`canonical`,`name`),
  CONSTRAINT `fk_annotation1` FOREIGN KEY (`native`) REFERENCES `native` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_annotation2` FOREIGN KEY (`canonical`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `derived_version` (
  `uri_source_version`      varchar(40) NOT NULL DEFAULT '',
  `uri_derived_version`     varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`uri_source_version`,`uri_derived_version`),
  CONSTRAINT `fk_derived_version` FOREIGN KEY (`uri_source_version`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_derived_version1` FOREIGN KEY (`uri_derived_version`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `merged_version` (
  `uri_merged`      varchar(40) NOT NULL DEFAULT '',
  `uri_source`      varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`uri_merged`,`uri_source`),
  CONSTRAINT `fk_merged_version1` FOREIGN KEY (`uri_merged`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_merged_version2` FOREIGN KEY (`uri_source`) REFERENCES `canonical` (`uri`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `search_history` (
  `username`        varchar(10) DEFAULT NULL,
  `search`          varchar(200) DEFAULT NULL,
  `num`             int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`num`),
  UNIQUE KEY `un_search` (`username`,`search`),
  CONSTRAINT `fk_search` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


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
  `documentation`   text,
  `name`            varchar(40) DEFAULT NULL,
  `cpf`             longtext,
  `apf`             longtext,
  `npf`             longtext,
  PRIMARY KEY (`code`, `processId`, `new_version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `process_branch` (
    `branch_id`    			                varchar(40),
    `branch_name`    			              varchar(1000),
    `process_id`    			              int(11),
    `source_process_model_version_id`  	varchar(40) NULL,
    `current_process_model_version_id`  varchar(40) NULL,
    constraint `pk_process_branch` primary key (`branch_id`),
    constraint `fk_process_branch` foreign key (`process_id`) references `process` (`processId`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB;
   
   
CREATE TABLE `process_model_version` (
    `process_model_version_id`    	varchar(40),
    `branch_id`    			            varchar(40),
    `root_fragment_version_id`    	varchar(40),
    `version_number` 			          int,
    `version_name` 			            varchar(200),
    `change_propagation` 		        int,
    `lock_status`    			          int,
    `num_vertices`    			        int,
    `num_edges`    			            int,
    constraint `pk_process_model_version` primary key (`process_model_version_id`),
    constraint `fk_process_branch_model_version` foreign key (`branch_id`) references `process_branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB;


ALTER TABLE `process_branch` add constraint `fk_source_version` foreign key (`source_process_model_version_id`) references `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `process_branch` add constraint `fk_current_version` foreign key (`current_process_model_version_id`) references `process_model_version` (`process_model_version_id`) ON DELETE CASCADE ON UPDATE CASCADE;


CREATE TABLE `content` (
    `content_id`      			varchar(40),
    `boundary_s`      			varchar(40),
    `boundary_e`      			varchar(40),
    `content_hash`    			text,
    constraint `pk_content` primary key (`content_id`)
)  engine=InnoDB;    

 
CREATE TABLE `fragment` (
    `fragment_id`    			      varchar(40),
    `propagation_policy`    		int,
    constraint `pk_fragment` primary key (`fragment_id`)
)  engine=InnoDB;


CREATE TABLE `fragment_version` (
    `fragment_version_id`    	varchar(40),
    `fragment_id`    			    varchar(40),
    `content_id`    			    varchar(40),
    `child_mapping_code`    	varchar(20000),
    `lock_status`    			    int,
    `lock_count`    			    int,
    `derived_from_fragment`   varchar(40),
    `fragment_size`    			  int,
    `fragment_type`    			  varchar(10),
    `cluster_id`    			    varchar(40),
    `newest_neighbor`    			varchar(40),
    constraint `pk_fragment_version` primary key (`fragment_version_id`),
    constraint `fk_fragments_version` foreign key (`fragment_id`) references `fragment` (`fragment_id`),
    constraint `fk_contents_version` foreign key (`content_id`) references `content` (`content_id`)
)  engine=InnoDB;


CREATE TABLE `process_fragment_map` (
    `mapping_id`    			        int auto_increment,
    `process_model_version_id`   	varchar(40),
    `fragment_version_id`    		  varchar(40),
    constraint `pk_process_fragment_map` primary key (`mapping_id`),
    constraint `fk_process_model_versions_map` foreign key (`process_model_version_id`) references `process_model_version` (`process_model_version_id`),
    constraint `fk_fragment_versions_map` foreign key (`fragment_version_id`) references `fragment_version` (`fragment_version_id`)
)  engine=InnoDB;


CREATE TABLE `fragment_version_dag` (
    `fragment_version_id`    		    varchar(40),
    `child_fragment_version_id`   	varchar(40),
    `pocket_id`    			            varchar(40),
    constraint `pk_fragment_version_dag` primary key (`fragment_version_id`, `child_fragment_version_id`, `pocket_id`),
    constraint `fk_fragment_version_dag` foreign key (`fragment_version_id`) references `fragment_version` (`fragment_version_id`),
    constraint `fk_child_fragment_version_dag` foreign key (`fragment_version_id`) references `fragment_version` (`fragment_version_id`)
)  engine=InnoDB;


CREATE TABLE `vertex` (
    `vid`    				          varchar(40),
    `content_id` 			  	    varchar(40),
    `vname`    				        varchar(2000),
    `vtype`    				        varchar(100),
    `locator_preset`    			varchar(2000),
    `locator_postset`    			varchar(2000),
    constraint `pk_vertex` primary key (`vid`),
    constraint `fk_vertex_content` foreign key (`content_id`) references `content` (`content_id`)
)  engine=InnoDB;


CREATE TABLE `non_pocket_vertex` (
    `vid`    				          varchar(40),
    constraint `pk_non_pocket_vertex` primary key (`vid`)
)  engine=InnoDB;


CREATE TABLE `edge` (
    `edge_id`    				    int auto_increment,
    `content_id`    			  varchar(40),
    `source_vid`    			  varchar(40),
    `target_vid`    			  varchar(40),
    constraint `pk_edge` primary key (`edge_id`),
    constraint `fk_content_edge` foreign key (`content_id`) references `content` (`content_id`),
    constraint `fk_source_vertex` foreign key (`source_vid`) references `vertex` (`vid`),
    constraint `fk_target_vertex` foreign key (`target_vid`) references `vertex` (`vid`)
)  engine=InnoDB;


CREATE TABLE  `subcluster` (
  `fragment_version_id` varchar(40) NOT NULL DEFAULT '',
  `fragment_size` int(11) DEFAULT NULL,
  `parent_cluster_id` varchar(80) NOT NULL DEFAULT '',
  `subcluster_id` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`fragment_version_id`,`parent_cluster_id`),
  constraint fk_subcluster foreign key (fragment_version_id) references fragment_version (fragment_version_id)
) ENGINE=InnoDB;


DROP VIEW IF EXISTS `head_version0`;
DROP VIEW IF EXISTS `head_version`;
DROP VIEW IF EXISTS `keyword`;
DROP VIEW IF EXISTS `process_ranking`;


CREATE VIEW `head_version0` AS
  select `canonical`.`processId` AS `processId`,
         `canonical`.`version_name` AS `version`
  from `canonical`
  where ((`canonical`.`processId`, `canonical`.`version_name`) in
          (select `canonical`.`processId` AS `processId`, `canonical`.`version_name` AS `version`
           from `canonical`, `derived_version`
           where `canonical`.`uri` = `derived_version`.`uri_derived_version`)
  );


CREATE VIEW `head_version` AS
  select `head_version0`.`processid` AS `processId`,
         `head_version0`.`version` AS `version`
  from `head_version0`
  union
  select `canonical`.`processId` AS `processId`,
         `canonical`.`version_name` AS `version_name`
  from `canonical`
  where (not(`canonical`.`processId` in 
           (select `canonical`.`processId` AS `processId`
            from `canonical`, `derived_version`
            where `canonical`.uri = `derived_version`.`uri_derived_version`))
        );


CREATE VIEW `keyword` AS 
  select `process`.`processId` AS `processId`,`process`.`name` AS `word` 
  from `process` 
    union 
  select `process`.`processId` AS `processId`,`process`.`domain` AS `domain` 
  from `process` 
    union 
  select `process`.`processId` AS `processId`,`process`.`original_type` AS `original_type` 
  from `process` 
    union 
  select `process`.`processId` AS `processId`,`user`.`firstname` AS `firstname` 
  from (`process` join `user` on((`process`.`owner` = `user`.`username`))) 
    union 
  select `process`.`processId` AS `processId`,`user`.`lastname` AS `lastname` 
  from (`process` join `user` on((`process`.`owner` = `user`.`username`))) 
    union 
  select `canonical`.`processId` AS `processId`,`canonical`.`version_name` AS `version_name` 
  from `canonical`;


CREATE VIEW `process_ranking` AS 
  select `canonical`.`processId` AS `processId`, avg(`canonical`.`ranking`) AS `ranking` 
  from `canonical` 
  group by `canonical`.`processId`;