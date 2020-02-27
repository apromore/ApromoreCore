SET FOREIGN_KEY_CHECKS=0;

USE `apromore`;

DROP VIEW IF EXISTS `apromore`.`keywords`;

DROP TABLE IF EXISTS `search_history`;
DROP TABLE IF EXISTS `annotation`;
DROP TABLE IF EXISTS `canonical`;
DROP TABLE IF EXISTS `native`;
DROP TABLE IF EXISTS `fragment_version_dag`;
DROP TABLE IF EXISTS `process_fragment_map`;
DROP TABLE IF EXISTS `expression`;
DROP TABLE IF EXISTS `node`;
DROP TABLE IF EXISTS `node_mapping`;
DROP TABLE IF EXISTS `cancel_nodes`;
DROP TABLE IF EXISTS `edge`;
DROP TABLE IF EXISTS `edge_mapping`;
DROP TABLE IF EXISTS `fragment_version`;
DROP TABLE IF EXISTS `native_type`;
DROP TABLE IF EXISTS `process`;
DROP TABLE IF EXISTS `process_branch`;
DROP TABLE IF EXISTS `process_model_version`;
DROP TABLE IF EXISTS `folder`;
DROP TABLE IF EXISTS `fragment`;
DROP TABLE IF EXISTS `membership`;
DROP TABLE IF EXISTS `permission`;
DROP TABLE IF EXISTS `group`;
DROP TABLE IF EXISTS `user_group`;
DROP TABLE IF EXISTS `group_folder`;
DROP TABLE IF EXISTS `group_process`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `workspace`;
DROP TABLE IF EXISTS `edit_session`;
DROP TABLE IF EXISTS `process_model_attribute`;
DROP TABLE IF EXISTS `node_attribute`;
DROP TABLE IF EXISTS `edge_attribute`;
DROP TABLE IF EXISTS `expression`;
DROP TABLE IF EXISTS `resource`;
DROP TABLE IF EXISTS `resource_attribute`;
DROP TABLE IF EXISTS `resource_ref`;
DROP TABLE IF EXISTS `resource_ref_attribute`;
DROP TABLE IF EXISTS `resource_specialisations`;
DROP TABLE IF EXISTS `object`;
DROP TABLE IF EXISTS `object_attribute`;
DROP TABLE IF EXISTS `object_ref`;
DROP TABLE IF EXISTS `object_ref_attribute`;
DROP TABLE IF EXISTS `cluster`;
DROP TABLE IF EXISTS `cluster_assignment`;
DROP TABLE IF EXISTS `fragment_distance`;
DROP TABLE IF EXISTS `metric`;
DROP TABLE IF EXISTS `statistic`;

DROP TABLE IF EXISTS `history_event`;

DROP TABLE IF EXISTS `group_log`;
DROP TABLE IF EXISTS `log`;

# DROP TABLE IF EXISTS `qrtz_fired_triggers`;
# DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
# DROP TABLE IF EXISTS `qrtz_scheduler_state`;
# DROP TABLE IF EXISTS `qrtz_locks`;
# DROP TABLE IF EXISTS `qrtz_simple_triggers`;
# DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
# DROP TABLE IF EXISTS `qrtz_cron_triggers`;
# DROP TABLE IF EXISTS `qrtz_blob_triggers`;
# DROP TABLE IF EXISTS `qrtz_triggers`;
# DROP TABLE IF EXISTS `qrtz_job_details`;
# DROP TABLE IF EXISTS `qrtz_calendars`;


-- Construct the DB
CREATE TABLE `history_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL,
  `occurDate` datetime DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_history_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `search_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `search` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_search` (`userId` , `search`),
  CONSTRAINT `fk_search` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `native_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nat_type` varchar(20) NOT NULL DEFAULT '',
  `extension` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `domain` varchar(40) DEFAULT NULL,
  `owner` int(11) DEFAULT NULL,
  `nativeTypeId` int(11) DEFAULT NULL,
  `folderId` int(11) DEFAULT NULL,
  `ranking` varchar(10) DEFAULT NULL,
  `createDate` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_process2` (`nativeTypeId`),
  KEY `fk_users` (`owner`),
  KEY `fk_folder` (`folderId`),
  CONSTRAINT `fk_folder` FOREIGN KEY (`folderId`)
  REFERENCES `folder` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process1` FOREIGN KEY (`owner`)
  REFERENCES `user` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process2` FOREIGN KEY (`nativeTypeId`)
  REFERENCES `native_type` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folderId` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `ranking` varchar(10) DEFAULT NULL,
  `createDate` varchar(40) DEFAULT NULL,
  `owner` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_users` (`owner`),
  KEY `fk_folder` (`folderId`),
  CONSTRAINT `fk_log_folder` FOREIGN KEY (`folderId`)
  REFERENCES `folder` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_log1` FOREIGN KEY (`owner`)
  REFERENCES `user` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `native` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `lastUpdateDate` varchar(40) DEFAULT NULL,
  `nativeTypeId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_native_type` FOREIGN KEY (`nativeTypeId`)
  REFERENCES `native_type` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_branch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `branch_name` varchar(255),
  `processId` int(11) DEFAULT NULL,
  `createDate` varchar(40) DEFAULT NULL,
  `lastUpdateDate` varchar(40) DEFAULT NULL,
  `sourceProcessModelVersion` int(11) NULL,
  `currentProcessModelVersion` int(11) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_process_branch` FOREIGN KEY (`processId`)
  REFERENCES `process` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_source_version` FOREIGN KEY (`sourceProcessModelVersion`)
  REFERENCES `process_model_version` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_current_version` FOREIGN KEY (`currentProcessModelVersion`)
  REFERENCES `process_model_version` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_model_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `branchId` int(11) DEFAULT NULL,
  `rootFragmentVersionId` int(11) DEFAULT NULL,
  `nativeId` int(11) DEFAULT NULL,
  `canonicalId` int(11) DEFAULT NULL,
  `nativeTypeId` int(11) DEFAULT NULL,
  `originalId` varchar(200),
  `version_number` varchar(15),
  `change_propagation` int,
  `lock_status` int,
  `num_nodes` int,
  `num_edges` int,
  `createDate` varchar(40) DEFAULT NULL,
  `lastUpdateDate` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `fk_process_branch_model_version` FOREIGN KEY (`branchId`)
  REFERENCES `process_branch` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process_branch_model_version1` FOREIGN KEY (`rootFragmentVersionId`)
  REFERENCES `fragment_version` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process_native` FOREIGN KEY (`nativeId`)
  REFERENCES `native` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process_canonical` FOREIGN KEY (`canonicalId`)
  REFERENCES `canonical` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_process_native_type` FOREIGN KEY (`nativeTypeId`)
  REFERENCES `native_type` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `annotation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nativeId` int(11) DEFAULT NULL,
  `processModelVersionId` int(11) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `content` longtext,
  `lastUpdateDate` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_annotation` (`processModelVersionId` , `name`),
  CONSTRAINT `fk_annotation1` FOREIGN KEY (`nativeId`)
  REFERENCES `native` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_annotation2` FOREIGN KEY (`processModelVersionId`)
  REFERENCES `process_model_version` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `canonical` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `lastUpdateDate` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(256),
  `fragmentId` int(11) DEFAULT NULL,
  `clusterId` int(11) DEFAULT NULL,
  `child_mapping_code` varchar(20000),
  `derived_from_fragment` int(11),
  `lock_status` int,
  `lock_count` int,
  `fragment_size` int,
  `fragment_type` varchar(10),
  `newest_neighbor` varchar(40),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_fragments_version` FOREIGN KEY (`fragmentId`) REFERENCES `fragment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cluster_version` FOREIGN KEY (`clusterId`) REFERENCES `cluster` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_fragment_map` (
  `processModelVersionId` int(11) NOT NULL,
  `fragmentVersionId` int(11) NOT NULL,
  PRIMARY KEY (`processModelVersionId` , `fragmentVersionId`),
  CONSTRAINT `fk_process_model_versions_map` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_fragment_versions_map` FOREIGN KEY (`fragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment_version_dag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fragmentVersionId` int(11),
  `childFragmentVersionId` int(11),
  `pocketId` varchar(40),
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_fragment_version_dag` (`fragmentVersionId` , `childFragmentVersionId` , `pocketId`),
  CONSTRAINT `fk_fragment_version_dag` FOREIGN KEY (`fragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_child_fragment_version_dag` FOREIGN KEY (`childFragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(256),
  `subVersionId` int(11) DEFAULT NULL,
  `originalId` varchar(200) DEFAULT NULL,
  `netId` varchar(200) DEFAULT NULL,
  `name` varchar(500),
  `graphType` varchar(50),
  `nodeType` varchar(50),
  `configuration` varchar(1) NULL DEFAULT '0',
  `teamWork` varchar(1) NULL DEFAULT '0',
  `allocation` varchar(40) DEFAULT NULL,
  `resourceDataExpressionId` int(11) DEFAULT NULL,
  `resourceRunExpressionId` int(11) DEFAULT NULL,
  `timerExpressionId` int(11) DEFAULT NULL,
  `timeDate` datetime DEFAULT NULL,
  `timeDuration` varchar(100) NULL,
  `messageDirection` varchar(10) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_node_subversion` FOREIGN KEY (`subVersionId`)
  REFERENCES `process_model_version` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_node_data_expr` FOREIGN KEY (`resourceDataExpressionId`)
  REFERENCES `expression` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_node_run_expr` FOREIGN KEY (`resourceRunExpressionId`)
  REFERENCES `expression` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_node_timer_expr` FOREIGN KEY (`timerExpressionId`)
  REFERENCES `expression` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `node_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fragmentVersionId` int(11),
  `nodeId` int(11),
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_node_mapping` (`fragmentVersionId` , `nodeId`),
  CONSTRAINT `fk_nm_fragment_version` FOREIGN KEY (`fragmentVersionId`)
  REFERENCES `fragment_version` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_nm_node` FOREIGN KEY (`nodeId`)
  REFERENCES `node` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `cancel_nodes` (
  `nodeId` int(11) NOT NULL,
  `cancelNodeId` int(11) NOT NULL,
  PRIMARY KEY (`nodeId` , `cancelNodeId`),
  CONSTRAINT `fk_cancel_parent` FOREIGN KEY (`nodeId`)
  REFERENCES `node` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cancel_child` FOREIGN KEY (`cancelNodeId`)
  REFERENCES `node` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edge` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(256),
  `sourceNodeId` int(11) DEFAULT NULL,
  `targetNodeId` int(11) DEFAULT NULL,
  `cancelNodeId` int(11) DEFAULT NULL,
  `originalId` varchar(200) DEFAULT NULL,
  `conditionExpressionId` int(11) DEFAULT NULL,
  `def` varchar(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_source_node` FOREIGN KEY (`sourceNodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_target_node` FOREIGN KEY (`targetNodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cancel_node` FOREIGN KEY (`cancelNodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cond_expr` FOREIGN KEY (`conditionExpressionId`) REFERENCES `expression` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edge_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fragmentVersionId` int(11),
  `edgeId` int(11),
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_edge_mapping` (`fragmentVersionId` , `edgeId`),
  CONSTRAINT `fk_em_fragment_version` FOREIGN KEY (`fragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_em_edge` FOREIGN KEY (`edgeId`) REFERENCES `edge` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `expression` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inputNodeId` int(11) DEFAULT NULL,
  `outputNodeId` int(11) DEFAULT NULL,
  `description` varchar(255),
  `language` varchar(255),
  `expression` varchar(1000),
  `returnType` varchar(255),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_node_inexpr` FOREIGN KEY (`inputNodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_node_outexpr` FOREIGN KEY (`outputNodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `folder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parentId` int(11) DEFAULT NULL,
  `workspaceId` int(11) NULL DEFAULT '1',
  `folder_name` varchar(100) DEFAULT NULL,
  `folder_description` varchar(1000) DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  `date_created` datetime DEFAULT NULL,
  `modifiedById` int(11) DEFAULT NULL,
  `date_modified` datetime DEFAULT NULL,
  `ged_matrix_computation` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `folder_creator` (`creatorId`),
  KEY `folder_modified_by` (`modifiedById`),
  KEY `folder_workspace` (`workspaceId`),
  KEY `folder_folder` (`parentId`),
  CONSTRAINT `folder_creator` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `folder_folder` FOREIGN KEY (`parentId`) REFERENCES `folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `folder_modified_by` FOREIGN KEY (`modifiedById`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `folder_workspace` FOREIGN KEY (`workspaceId`) REFERENCES `workspace` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `propagation_policy` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `membership` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `password` varchar(100) NOT NULL,
  `password_salt` varchar(100) NOT NULL,
  `mobile_pin` varchar(100) DEFAULT NULL,
  `email` varchar(200) NOT NULL,
  `password_question` varchar(50) NULL DEFAULT NULL,
  `password_answer` varchar(50) NULL DEFAULT NULL,
  `is_approved` boolean not null default 1,
  `is_locked` boolean not null default 1,
  `date_created` datetime NOT NULL,
  `failed_password_attempts` int(11) NOT NULL DEFAULT '0',
  `failed_answer_attempts` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY (`userId`) USING BTREE,
  UNIQUE KEY (`email`) USING BTREE,
  CONSTRAINT `FK_users` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `row_guid` varchar(256) NOT NULL,
  `permission_name` varchar(45) NOT NULL,
  `permission_description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `row_guid` varchar(255) NOT NULL,
  `name` varchar(45) NOT NULL,
  `type` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_row_guid_UNIQUE` (`row_guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_group` (
  `userId` int(11) NOT NULL,
  `groupId` int(11) NOT NULL,
  PRIMARY KEY (`userId` , `groupId`),
  KEY `fk_user_group_user` (`userId`),
  KEY `fk_user_group_group` (`groupId`),
  CONSTRAINT `fk_user_group_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_group_group` FOREIGN KEY (`groupId`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `group_folder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupId` int(11) NOT NULL,
  `folderId` int(11) NOT NULL,
  `has_read` tinyint(1) NOT NULL DEFAULT '0',
  `has_write` tinyint(1) NOT NULL DEFAULT '0',
  `has_ownership` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_group_folder_folder` (`folderId`),
  KEY `fk_group_folder_group` (`groupId`),
  CONSTRAINT `fk_group_folder_folder` FOREIGN KEY (`folderId`) REFERENCES `folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_group_folder_group` FOREIGN KEY (`groupId`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `group_process` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupId` int(11) NOT NULL,
  `processId` int(11) NOT NULL,
  `has_read` tinyint(1) NOT NULL DEFAULT '0',
  `has_write` tinyint(1) NOT NULL DEFAULT '0',
  `has_ownership` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_group_process_process` (`processId`),
  KEY `fk_group_process_group` (`groupId`),
  CONSTRAINT `fk_group_process_process` FOREIGN KEY (`processId`) REFERENCES `process` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_group_process_group` FOREIGN KEY (`groupId`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `group_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupId` int(11) NOT NULL,
  `logId` int(11) NOT NULL,
  `has_read` tinyint(1) NOT NULL DEFAULT '0',
  `has_write` tinyint(1) NOT NULL DEFAULT '0',
  `has_ownership` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_group_log_log` (`logId`),
  KEY `fk_group_log_group` (`groupId`),
  CONSTRAINT `fk_group_log_log` FOREIGN KEY (`logId`) REFERENCES `log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_group_log_group` FOREIGN KEY (`groupId`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `row_guid` varchar(256) NOT NULL,
  `role_name` varchar(45) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `role_permission` (
  `roleId` int(11) NOT NULL,
  `permissionId` int(11) NOT NULL,
  PRIMARY KEY (`roleId` , `permissionId`),
  KEY `FK_role_permission_role` (`roleId`),
  KEY `FK_role_permission_permission` (`permissionId`),
  CONSTRAINT `FK_role_permission_permission` FOREIGN KEY (`permissionId`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_role_permission_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `row_guid` varchar(255) NOT NULL,
  `username` varchar(45) NOT NULL,
  `date_created` datetime NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `last_activity_date` datetime NOT NULL,
  `groupId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_row_guid_UNIQUE` (`row_guid`),
  UNIQUE KEY `user_username_UNIQUE` (`username`),
  KEY (`groupId`),
  CONSTRAINT `fk_user_group` FOREIGN KEY (`groupId`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_role` (
  `roleId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  PRIMARY KEY (`roleId` , `userId`),
  KEY `FK_user_role_users` (`userId`),
  KEY `FK_user_role_role` (`roleId`),
  CONSTRAINT `FK_user_role_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_role_users` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `workspace` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `workspace_name` varchar(45) NOT NULL,
  `workspace_description` varchar(1000) DEFAULT NULL,
  `userId` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `workspace_user` (`userId`),
  CONSTRAINT `workspace_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `object` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `processModelVersionId` int(11) NOT NULL,
  `uri` varchar(256),
  `netId` varchar(40),
  `name` varchar(255),
  `configurable` boolean not null default 0,
  `type` varchar(30),
  `softType` varchar(255),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_obj_pmv` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `objectId` int(11) DEFAULT NULL,
  `name` varchar(255),
  `value` longtext,
  `any` longtext NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_obj_att_obj` FOREIGN KEY (`objectId`) REFERENCES `object` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_ref` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `objectId` int(11) DEFAULT NULL,
  `nodeId` int(11) DEFAULT NULL,
  `optional` boolean not null default 0,
  `consumed` boolean not null default 0,
  `type` varchar(30),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_objrefobj_pmv` FOREIGN KEY (`objectId`) REFERENCES `object` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_objref_node` FOREIGN KEY (`nodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_ref_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `objectRefId` int(11) DEFAULT NULL,
  `name` varchar(255),
  `value` longtext,
  `any` longtext NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_objref_att` FOREIGN KEY (`objectRefId`) REFERENCES `object_ref` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `processModelVersionId` int(11) DEFAULT NULL,
  `uri` varchar(256),
  `originalId` varchar(200),
  `name` varchar(255),
  `configurable` boolean not null default 0,
  `type` varchar(30),
  `typeName` varchar(255) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_res_pmv` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_specialisations` (
  `resourceId` int(11) NOT NULL,
  `specialisationId` int(11) NOT NULL,
  PRIMARY KEY (`resourceId` , `specialisationId`),
  CONSTRAINT `fk_resource` FOREIGN KEY (`resourceId`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_special` FOREIGN KEY (`specialisationId`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resourceId` int(11) DEFAULT NULL,
  `name` varchar(255),
  `value` longtext,
  `any` longtext NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_res_att_res` FOREIGN KEY (`resourceId`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_ref` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resourceId` int(11) DEFAULT NULL,
  `nodeId` int(11) DEFAULT NULL,
  `qualifier` varchar(255),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_resref_pmv` FOREIGN KEY (`resourceId`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_resref_node` FOREIGN KEY (`nodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_ref_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resourceRefId` int(11) DEFAULT NULL,
  `name` varchar(255),
  `value` longtext,
  `any` longtext NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_resref_att` FOREIGN KEY (`resourceRefId`) REFERENCES `resource_ref` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `node_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nodeId` int(11) DEFAULT NULL,
  `name` varchar(255),
  `value` longtext,
  `any` longtext NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_node_attributes` FOREIGN KEY (`nodeId`) REFERENCES `node` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edge_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `edgeId` int(11) DEFAULT NULL,
  `name` varchar(255),
  `value` longtext,
  `any` longtext NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_edge_attributes` FOREIGN KEY (`edgeId`) REFERENCES `edge` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_model_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `processModelVersionId` int(11) DEFAULT NULL,
  `name` varchar(255),
  `value` longtext,
  `any` longtext NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_pmv_att_pmv` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `cluster` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
-- `folderId` int(11) NOT NULL,
  `size` int,
  `avg_fragment_size` float,
  `medoid_id` varchar(40),
  `benifit_cost_ratio` double,
  `std_effort` double,
  `refactoring_gain` int,
  PRIMARY KEY (`id`)
-- CONSTRAINT `fk_clusterfolder` FOREIGN KEY (`folderId`) REFERENCES `folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment_distance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fragmentVersionId1` int(11) NOT NULL,
  `fragmentVersionId2` int(11) NOT NULL,
  `ged` double,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_geds` (`fragmentVersionId1` , `fragmentVersionId2`),
  CONSTRAINT `fk_frag_version_1` FOREIGN KEY (`fragmentVersionId1`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_frag_version_2` FOREIGN KEY (`fragmentVersionId2`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `cluster_assignment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `clusterId` int(11) NOT NULL,
  `fragmentVersionId` int(11) NOT NULL,
  `clone_id` varchar(40),
  `maximal` boolean,
  `core_object_nb` int,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_cluster_assignments` (`fragmentVersionId` , `clusterId`),
  CONSTRAINT `fk_frag_version_assignment` FOREIGN KEY (`fragmentVersionId`) REFERENCES `fragment_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cluster_assignment` FOREIGN KEY (`clusterId`) REFERENCES `cluster` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `metric` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `processModelVersionId` int(11) NOT NULL,
  `name` varchar(40) NOT NULL,
  `value` double NOT NULL,
  `lastUpdateDate` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_metric` (`processModelVersionId` , `name`),
  CONSTRAINT `fk_metric1` FOREIGN KEY (`processModelVersionId`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `statistic` (
  `count` bigint(20) NOT NULL AUTO_INCREMENT,
  `id` varbinary(16) NOT NULL,
  `logid` int(11) DEFAULT NULL,
  `pid` varbinary(16) DEFAULT NULL,
  `stat_key` varchar(1023) DEFAULT NULL,
  `stat_value` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`count`),
  KEY `idx_logid` (`logid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Scheduler tables

# CREATE TABLE `qrtz_job_details` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   JOB_NAME  VARCHAR(200) NOT NULL,
#   JOB_GROUP VARCHAR(200) NOT NULL,
#   DESCRIPTION VARCHAR(250) NULL,
#   JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
#   IS_DURABLE VARCHAR(1) NOT NULL,
#   IS_NONCONCURRENT VARCHAR(1) NOT NULL,
#   IS_UPDATE_DATA VARCHAR(1) NOT NULL,
#   REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
#   JOB_DATA BLOB NULL,
#   PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_triggers` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   TRIGGER_NAME VARCHAR(200) NOT NULL,
#   TRIGGER_GROUP VARCHAR(200) NOT NULL,
#   JOB_NAME  VARCHAR(200) NOT NULL,
#   JOB_GROUP VARCHAR(200) NOT NULL,
#   DESCRIPTION VARCHAR(250) NULL,
#   NEXT_FIRE_TIME BIGINT(13) NULL,
#   PREV_FIRE_TIME BIGINT(13) NULL,
#   PRIORITY INTEGER NULL,
#   TRIGGER_STATE VARCHAR(16) NOT NULL,
#   TRIGGER_TYPE VARCHAR(8) NOT NULL,
#   START_TIME BIGINT(13) NOT NULL,
#   END_TIME BIGINT(13) NULL,
#   CALENDAR_NAME VARCHAR(200) NULL,
#   MISFIRE_INSTR SMALLINT(2) NULL,
#   JOB_DATA BLOB NULL,
#   PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
#   FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
#   REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_simple_triggers` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   TRIGGER_NAME VARCHAR(200) NOT NULL,
#   TRIGGER_GROUP VARCHAR(200) NOT NULL,
#   REPEAT_COUNT BIGINT(7) NOT NULL,
#   REPEAT_INTERVAL BIGINT(12) NOT NULL,
#   TIMES_TRIGGERED BIGINT(10) NOT NULL,
#   PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
#   FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
#   REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_cron_triggers` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   TRIGGER_NAME VARCHAR(200) NOT NULL,
#   TRIGGER_GROUP VARCHAR(200) NOT NULL,
#   CRON_EXPRESSION VARCHAR(200) NOT NULL,
#   TIME_ZONE_ID VARCHAR(80),
#   PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
#   FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
#   REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_simprop_triggers` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   TRIGGER_NAME VARCHAR(200) NOT NULL,
#   TRIGGER_GROUP VARCHAR(200) NOT NULL,
#   STR_PROP_1 VARCHAR(512) NULL,
#   STR_PROP_2 VARCHAR(512) NULL,
#   STR_PROP_3 VARCHAR(512) NULL,
#   INT_PROP_1 INT NULL,
#   INT_PROP_2 INT NULL,
#   LONG_PROP_1 BIGINT NULL,
#   LONG_PROP_2 BIGINT NULL,
#   DEC_PROP_1 NUMERIC(13,4) NULL,
#   DEC_PROP_2 NUMERIC(13,4) NULL,
#   BOOL_PROP_1 VARCHAR(1) NULL,
#   BOOL_PROP_2 VARCHAR(1) NULL,
#   PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
#   FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
#   REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_blob_triggers` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   TRIGGER_NAME VARCHAR(200) NOT NULL,
#   TRIGGER_GROUP VARCHAR(200) NOT NULL,
#   BLOB_DATA BLOB NULL,
#   PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
#   FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
#   REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_calendars` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   CALENDAR_NAME  VARCHAR(200) NOT NULL,
#   CALENDAR BLOB NOT NULL,
#   PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_paused_trigger_grps` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   TRIGGER_GROUP  VARCHAR(200) NOT NULL,
#   PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_fired_triggers` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   ENTRY_ID VARCHAR(95) NOT NULL,
#   TRIGGER_NAME VARCHAR(200) NOT NULL,
#   TRIGGER_GROUP VARCHAR(200) NOT NULL,
#   INSTANCE_NAME VARCHAR(200) NOT NULL,
#   FIRED_TIME BIGINT(13) NOT NULL,
#   SCHED_TIME BIGINT(13) NOT NULL,
#   PRIORITY INTEGER NOT NULL,
#   STATE VARCHAR(16) NOT NULL,
#   JOB_NAME VARCHAR(200) NULL,
#   JOB_GROUP VARCHAR(200) NULL,
#   IS_NONCONCURRENT VARCHAR(1) NULL,
#   REQUESTS_RECOVERY VARCHAR(1) NULL,
#   PRIMARY KEY (SCHED_NAME,ENTRY_ID)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_scheduler_state` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   INSTANCE_NAME VARCHAR(200) NOT NULL,
#   LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
#   CHECKIN_INTERVAL BIGINT(13) NOT NULL,
#   PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#
# CREATE TABLE `qrtz_locks` (
#   SCHED_NAME VARCHAR(120) NOT NULL,
#   LOCK_NAME  VARCHAR(40) NOT NULL,
#   PRIMARY KEY (SCHED_NAME,LOCK_NAME)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- Create indexes for the tables

CREATE INDEX `idx_search_history` ON `search_history` (`position`, `search`) USING BTREE;
CREATE INDEX `idx_native_type` ON `native_type` (`nat_type`, `extension`) USING BTREE;
CREATE INDEX `idx_user_username` ON `user` (`username`) USING BTREE;
CREATE INDEX `idx_process_name` ON `process` (`name`, `folderId`) USING BTREE;
CREATE INDEX `idx_branch_name` ON `process_branch` (`branch_name`) USING BTREE;
CREATE INDEX `idx_pmv_version` ON `process_model_version` (`version_number`) USING BTREE;
CREATE INDEX `idx_pmv_lock` ON `process_model_version` (`lock_status`) USING BTREE;
CREATE INDEX `idx_annotation_name` ON `annotation` (`name`) USING BTREE;
CREATE INDEX `idx_fv_lock` ON `fragment_version` (`lock_status`) USING BTREE;
CREATE INDEX `idx_fv_sizetype` ON `fragment_version` (`fragment_size`, `fragment_type`) USING BTREE;
CREATE INDEX `idx_fvd_pocket` ON `fragment_version_dag` (`pocketId`) USING BTREE;
CREATE INDEX `idx_cluster` ON `cluster` (`size`, `avg_fragment_size`, `benifit_cost_ratio`) USING BTREE;
CREATE INDEX `idx_fragment_distance` ON `fragment_distance` (`ged`) USING BTREE;


-- Add the basic data used by the system.

LOCK TABLES `native_type` WRITE;
/*!40000 ALTER TABLE `native_type` DISABLE KEYS */;
/*INSERT INTO `native_type` VALUES (1,'EPML 2.0','epml');*/
/*INSERT INTO `native_type` VALUES (2,'XPDL 2.2','xpdl');*/
/*INSERT INTO `native_type` VALUES (3,'PNML 1.3.2', 'pnml');*/
/*INSERT INTO `native_type` VALUES (4,'YAWL 2.2', 'yawl');*/
INSERT INTO `native_type` VALUES (5,'BPMN 2.0', 'bpmn');
/*INSERT INTO `native_type` VALUES (6,'AML fragment', 'aml');*/
/*!40000 ALTER TABLE `native_type` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'80da507e-cdd7-40f4-a9f8-b2d2edb12856','ROLE_ADMIN','Testing description 2');
INSERT INTO `role` VALUES (2,'0ecd70b4-a204-41cd-a246-e3fcef88f6fe','ROLE_USER','Testing');
INSERT INTO `role` VALUES (3,'72503ce0-d7cd-47b3-a33c-1b741d7599a1','ROLE_MANAGER','Middle role');
INSERT INTO `role` VALUES (4,'f8b91579-f061-47e8-9f73-e2691884058f','ROLE_MANAGER','Middle role');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'75f4a46a-bd32-4fbb-ba7a-c50d06414fac','james','2012-05-23 11:52:48','Cameron','James','2012-05-23 11:52:48',1);
INSERT INTO `user` VALUES (2,'aaf24d0d-58f2-43b1-8dcc-bf99717b708f','chathura','2012-05-23 11:59:51','Chathura','Ekanayake','2012-05-23 11:59:51',2);
INSERT INTO `user` VALUES (3,'a393f9c2-e2ee-49ed-9b6a-a1269811764c','arthur','2012-05-23 12:07:24','Arthur','Ter Hofstede','2012-05-23 12:07:24',3);
INSERT INTO `user` VALUES (4,'b6701ee5-227b-493e-9b01-85aa33acd53b','Macri','2012-05-23 20:08:03','Marie','Fauvet','2012-05-23 20:08:03',4);
INSERT INTO `user` VALUES (5,'c81da91f-facc-4eff-b648-bdc1a2a5ebbe','larosa','2012-05-23 20:24:37','Marcello','La Rosa','2012-05-23 20:24:37',5);
INSERT INTO `user` VALUES (6,'c03eff4d-3672-4c91-bfea-36c67e2423f5','felix','2012-05-23 20:37:44','Felix','Mannhardt','2012-05-23 20:37:44',6);
INSERT INTO `user` VALUES (7,'fbcd5a9a-a224-40cb-8ab9-b12436d92835','raboczi','2012-05-23 20:40:26','Simon','Raboczi','2012-05-23 20:40:26',7);
INSERT INTO `user` VALUES (8,'ad1f7b60-1143-4399-b331-b887585a0f30','admin','2012-05-28 16:51:05','Test','User','2012-05-28 16:51:05',8);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `membership` WRITE;
/*!40000 ALTER TABLE `membership` DISABLE KEYS */;
INSERT INTO `membership` VALUES (1,1,'5f4dcc3b5aa765d61d8327deb882cf99','username','','cam.james@gmail.com','Test question','test',1,0,'2012-05-23 20:37:44',0,0);
INSERT INTO `membership` VALUES (2,2,'5f4dcc3b5aa765d61d8327deb882cf99','username','','c.ekanayake@qut.edu.au','Test question','test',1,0,'2012-05-28 16:51:05',0,0);
INSERT INTO `membership` VALUES (3,3,'5f4dcc3b5aa765d61d8327deb882cf99','username','','arthur@yawlfoundation.org','Test question','test',1,0,'2012-06-16 11:43:16',0,0);
INSERT INTO `membership` VALUES (4,4,'5f4dcc3b5aa765d61d8327deb882cf99','username','','marie-christine.fauvet@qut.edu.au','Test question','test',1,0,'2012-06-16 11:56:00',0,0);
INSERT INTO `membership` VALUES (5,5,'5f4dcc3b5aa765d61d8327deb882cf99','username','','m.larosa@qut.edu.au','Test question','test',1,0,'2012-06-16 12:01:35',0,0);
INSERT INTO `membership` VALUES (6,6,'5f4dcc3b5aa765d61d8327deb882cf99','username','','felix.mannhardt@smail.wir.h-brs.de','Test question','test',1,0,'2012-06-16 12:08:50',0,0);
INSERT INTO `membership` VALUES (7,7,'5f4dcc3b5aa765d61d8327deb882cf99','username','','raboczi@gmail.com','Test question','test',1,0,'2012-06-16 12:35:25',0,0);
INSERT INTO `membership` VALUES (8,8,'5f4dcc3b5aa765d61d8327deb882cf99','username','','admin','Test question','test',1,0,'2012-06-16 14:10:14',0,0);
/*!40000 ALTER TABLE `membership` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `group` WRITE;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
INSERT INTO `group` VALUES (1,'uuid1-james',   'james',   'USER');
INSERT INTO `group` VALUES (2,'uuid2-chathura','chathura','USER');
INSERT INTO `group` VALUES (3,'uuid3-arthur',  'arthur',  'USER');
INSERT INTO `group` VALUES (4,'uuid4-Macri',   'Macri',   'USER');
INSERT INTO `group` VALUES (5,'uuid5-larosa',  'larosa',  'USER');
INSERT INTO `group` VALUES (6,'uuid6-felix',   'felix',   'USER');
INSERT INTO `group` VALUES (7,'uuid7-raboczi', 'raboczi', 'USER');
INSERT INTO `group` VALUES (8,'uuid8-admin',   'admin',   'USER');
INSERT INTO `group` VALUES (9,'uuid9-public',  'public',  'PUBLIC');
/*!40000 ALTER TABLE `group` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
INSERT INTO `user_group` VALUES (1,1),(1,9);  # every user is explicitly a member of the public group
INSERT INTO `user_group` VALUES (2,2),(2,9);
INSERT INTO `user_group` VALUES (3,3),(3,9);
INSERT INTO `user_group` VALUES (4,4),(4,9);
INSERT INTO `user_group` VALUES (5,5),(5,9);
INSERT INTO `user_group` VALUES (6,6),(6,9);
INSERT INTO `user_group` VALUES (7,7),(7,9);
INSERT INTO `user_group` VALUES (8,8),(8,9);
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1);
INSERT INTO `user_role` VALUES (2,1);
INSERT INTO `user_role` VALUES (1,2);
INSERT INTO `user_role` VALUES (2,2);
INSERT INTO `user_role` VALUES (1,3);
INSERT INTO `user_role` VALUES (2,3);
INSERT INTO `user_role` VALUES (1,4);
INSERT INTO `user_role` VALUES (2,4);
INSERT INTO `user_role` VALUES (1,5);
INSERT INTO `user_role` VALUES (2,5);
INSERT INTO `user_role` VALUES (1,6);
INSERT INTO `user_role` VALUES (2,6);
INSERT INTO `user_role` VALUES (1,7);
INSERT INTO `user_role` VALUES (2,7);
INSERT INTO `user_role` VALUES (1,8);
INSERT INTO `user_role` VALUES (2,8);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,'dff60714-1d61-4544-8884-0d8b852ba41e','Manage users','Admin role');
INSERT INTO `permission` VALUES (2,'2e884153-feb2-4842-b291-769370c86e44','Manage records','Admin role');
INSERT INTO `permission` VALUES (3,'d9ade57c-14c7-4e43-87e5-6a9127380b1b','Manage records','Admin role');
INSERT INTO `permission` VALUES (4,'ea31a607-212f-447e-8c45-78f1e59b1dde','Manage records 2','Admin role');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
INSERT INTO `role_permission` VALUES (1,1);
INSERT INTO `role_permission` VALUES (1,2);
INSERT INTO `role_permission` VALUES (2,1);
INSERT INTO `role_permission` VALUES (2,2);
INSERT INTO `role_permission` VALUES (2,3);
INSERT INTO `role_permission` VALUES (3,1);
INSERT INTO `role_permission` VALUES (3,2);
INSERT INTO `role_permission` VALUES (3,3);
INSERT INTO `role_permission` VALUES (3,4);
INSERT INTO `role_permission` VALUES (4,1);
INSERT INTO `role_permission` VALUES (4,3);
INSERT INTO `role_permission` VALUES (4,4);
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `search_history` WRITE;
/*!40000 ALTER TABLE `search_history` DISABLE KEYS */;
INSERT INTO `search_history` VALUES (1,1,8,'airport');
INSERT INTO `search_history` VALUES (2,2,8,'gold coast');
INSERT INTO `search_history` VALUES (3,3,8,'goldcoast');
/*!40000 ALTER TABLE `search_history` ENABLE KEYS */;
UNLOCK TABLES;


CREATE VIEW `apromore`.`keywords` AS
  select `process`.`id` AS `processId`, `process`.`id` AS `value`
  from `process`
  union
  select `process`.`id` AS `processId`, `process`.`name` AS `word`
  from `process`
  union
  select `process`.`id` AS `processId`, `process`.`domain` AS `domain`
  from `process`
  union
  select `process`.`id` AS `processId`, `native_type`.`nat_type` AS `original_type`
  from `process` join `native_type` ON (`process`.`nativeTypeId` = `native_type`.`id`)
  union
  select `process`.`id` AS `processId`, `user`.`first_name` AS `firstname`
  from `process` join `user` ON (`process`.`owner` = `user`.`username`)
  union
  select `process`.`id` AS `processId`, `user`.`last_name` AS `lastname`
  from `process` join `user` ON (`process`.`owner` = `user`.`username`)
  union
  select `process_branch`.`processId` AS `processId`, `process_branch`.`branch_name` AS `branch_name`
  from `process_branch`;


SET FOREIGN_KEY_CHECKS=1;
