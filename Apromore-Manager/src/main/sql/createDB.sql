SET FOREIGN_KEY_CHECKS=0;

USE `Apromore`;

DROP TABLE IF EXISTS `merged_version`;
DROP TABLE IF EXISTS `derived_version`;
DROP TABLE IF EXISTS `search_history`;
DROP TABLE IF EXISTS `temp_version`;
DROP TABLE IF EXISTS `annotation`;
DROP TABLE IF EXISTS `native`;
DROP TABLE IF EXISTS `edit_session`;
DROP TABLE IF EXISTS `fragment_version_dag`;
DROP TABLE IF EXISTS `process_fragment_map`;
DROP TABLE IF EXISTS `expression`;
DROP TABLE IF EXISTS `non_pocket_node`;
DROP TABLE IF EXISTS `node`;
DROP TABLE IF EXISTS `cancel_nodes`;
DROP TABLE IF EXISTS `edge`;
DROP TABLE IF EXISTS `fragment_version`;
DROP TABLE IF EXISTS `content`;
DROP TABLE IF EXISTS `native_type`;
DROP TABLE IF EXISTS `process`;
DROP TABLE IF EXISTS `process_branch`;
DROP TABLE IF EXISTS `process_model_version`;
DROP TABLE IF EXISTS `net`;

DROP TABLE IF EXISTS `folder`;
DROP TABLE IF EXISTS `folder_process`;
DROP TABLE IF EXISTS `folder_subfolder`;
DROP TABLE IF EXISTS `folder_user`;
DROP TABLE IF EXISTS `fragment`;
DROP TABLE IF EXISTS `fragment_user`;
DROP TABLE IF EXISTS `membership`;
DROP TABLE IF EXISTS `permission`;
DROP TABLE IF EXISTS `process_user`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `workspace`;

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



CREATE TABLE `native_type` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `nat_type` varchar(20) NOT NULL DEFAULT '',
    `extension` varchar(10) DEFAULT NULL,
    PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(100) DEFAULT NULL,
    `domain` varchar(40) DEFAULT NULL,
    `owner` int(11) DEFAULT NULL,
    `original_type` int(11) DEFAULT NULL,
    `folderId` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_process2` (`original_type`),
    KEY `fk_users` (`owner`),
    KEY `fk_folder` (`folderId`),
    CONSTRAINT `fk_folder` FOREIGN KEY (`folderId`)
        REFERENCES `folder` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_process1` FOREIGN KEY (`owner`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_process2` FOREIGN KEY (`original_type`)
        REFERENCES `native_type` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `native` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `content` longtext,
    `nat_type` int(11) DEFAULT NULL,
    `processModelVersionId` int(11) NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_native` (`processModelVersionId` , `nat_type`),
    CONSTRAINT `fk_native` FOREIGN KEY (`nat_type`)
        REFERENCES `native_type` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_native3` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edit_session` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `recordTime` datetime DEFAULT NULL,
    `userId` int(11) DEFAULT NULL,
    `processModelVersionId` int(11) NOT NULL,
    `processId` int(11) DEFAULT NULL,
    `version_name` varchar(40) DEFAULT NULL,
    `nat_type` varchar(20) DEFAULT NULL,
    `annotation` varchar(40) DEFAULT NULL,
    `remove_fake_events` tinyint(1) DEFAULT NULL,
    `creation_date` varchar(35) DEFAULT NULL,
    `last_update` varchar(35) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_edit_session1` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_edit_session2` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_edit_session3` FOREIGN KEY (`processId`)
        REFERENCES `process` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `search_history` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `userId` int(11) DEFAULT NULL,
    `search` varchar(200) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_search` (`userId` , `search`),
    CONSTRAINT `fk_search` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `temp_version` (
    `id` int(11) NOT NULL DEFAULT '0',
    `recordTime` datetime DEFAULT NULL,
    `processId` int(11) NOT NULL DEFAULT '0',
    `pre_version` varchar(40) DEFAULT NULL,
    `new_version` varchar(40) NOT NULL DEFAULT '',
    `nat_type` varchar(20) DEFAULT NULL,
    `creation_date` varchar(35) DEFAULT NULL,
    `last_update` varchar(35) DEFAULT NULL,
    `ranking` varchar(10) DEFAULT NULL,
    `documentation` longtext,
    `name` varchar(40) DEFAULT NULL,
    `cpf` longtext,
    `apf` longtext,
    `npf` longtext,
    PRIMARY KEY (`id`),
    UNIQUE KEY `tmp_version` (`id` , `processId` , `new_version`),
    CONSTRAINT `fk_tmp_version1` FOREIGN KEY (`processId`)
        REFERENCES `process` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_branch` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `branch_name` varchar(1000),
    `processId` int(11) DEFAULT NULL,
    `creation_date` varchar(35) DEFAULT NULL,
    `last_update` varchar(35) DEFAULT NULL,
    `ranking` varchar(10) DEFAULT NULL,
    `sourceProcessModelVersion` int(11) NULL,
    `currentProcessModelVersion` int(11) NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_process_branch` FOREIGN KEY (`processId`)
        REFERENCES `process` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_model_version` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `branchId` int(11) DEFAULT NULL,
    `rootFragmentVersionId` int(11) DEFAULT NULL,
    `originalId` varchar(200),
    `version_number` double,
    `version_name` varchar(200),
    `change_propagation` int,
    `lock_status` int,
    `num_nodes` int,
    `num_edges` int,
    CONSTRAINT `pk_process_model_version` primary key (`id`),
    CONSTRAINT `fk_process_branch_model_version` FOREIGN KEY (`branchId`)
        REFERENCES `process_branch` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_process_branch_model_version1` FOREIGN KEY (`rootFragmentVersionId`)
        REFERENCES `fragment_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `net` (
    `processModelVersionId` int(11) NOT NULL AUTO_INCREMENT,
    `processId` int(11) DEFAULT NULL,
    `netId` varchar(200),
    PRIMARY KEY (`processModelVersionId`) USING BTREE,
    KEY (`processId`) USING BTREE,
    CONSTRAINT `FK_processModelVersions` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_process_net` FOREIGN KEY (`processId`)
        REFERENCES `process` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `process_branch` ADD CONSTRAINT `fk_source_version` FOREIGN KEY (`sourceProcessModelVersion`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `process_branch` ADD CONSTRAINT `fk_current_version` FOREIGN KEY (`currentProcessModelVersion`) REFERENCES `process_model_version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE `annotation` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `native` int(11) DEFAULT NULL,
    `processModelVersionId` int(11) DEFAULT NULL,
    `name` varchar(40) DEFAULT NULL,
    `content` longtext,
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_annotation` (`processModelVersionId` , `name`),
    CONSTRAINT `fk_annotation1` FOREIGN KEY (`native`)
        REFERENCES `native` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_annotation2` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `content` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `boundary_s` int(11) DEFAULT NULL,
    `boundary_e` int(11) DEFAULT NULL,
    `code` longtext,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_start_edge` FOREIGN KEY (`boundary_s`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_end_edge` FOREIGN KEY (`boundary_e`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment_version` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `uri` varchar(40),
    `fragmentId` int(11) DEFAULT NULL,
    `contentId` int(11) DEFAULT NULL,
    `clusterId` int(11) DEFAULT NULL,
    `child_mapping_code` varchar(20000),
    `derived_from_fragment` int(11),
    `lock_status` int,
    `lock_count` int,
    `fragment_size` int,
    `fragment_type` varchar(10),
    `newest_neighbor` varchar(40),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_contents_version` FOREIGN KEY (`contentId`)
        REFERENCES `content` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_fragments_version` FOREIGN KEY (`fragmentId`)
        REFERENCES `fragment` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_fragment_map` (
    `processModelVersionId` int(11) NOT NULL,
    `fragmentVersionId` int(11) NOT NULL,
    PRIMARY KEY (`processModelVersionId` , `fragmentVersionId`),
    CONSTRAINT `fk_process_model_versions_map` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_fragment_versions_map` FOREIGN KEY (`fragmentVersionId`)
        REFERENCES `fragment_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment_version_dag` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `fragmentVersionId` int(11),
    `childFragmentVersionId` int(11),
    `pocketId` varchar(40),
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_fragment_version_dag` (`fragmentVersionId` , `childFragmentVersionId` , `pocketId`),
    CONSTRAINT `fk_fragment_version_dag` FOREIGN KEY (`fragmentVersionId`)
        REFERENCES `fragment_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_child_fragment_version_dag` FOREIGN KEY (`childFragmentVersionId`)
        REFERENCES `fragment_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `node` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `uri` varchar(40),
    `contentId` int(11) DEFAULT NULL,
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
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_node_content` FOREIGN KEY (`contentId`)
        REFERENCES `content` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_node_subversion` FOREIGN KEY (`subVersionId`)
        REFERENCES `process_model_version` (`id`)
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
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edge` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `uri` varchar(40),
    `contentId` int(11) DEFAULT NULL,
    `sourceNodeId` int(11) DEFAULT NULL,
    `targetNodeId` int(11) DEFAULT NULL,
    `cancelNodeId` int(11) DEFAULT NULL,
    `originalId` varchar(40) DEFAULT NULL,
    `conditionExpressionId` int(11) DEFAULT NULL,
    `def` varchar(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_content_edge` FOREIGN KEY (`contentId`)
        REFERENCES `content` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_source_node` FOREIGN KEY (`sourceNodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_target_node` FOREIGN KEY (`targetNodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_cancel_node` FOREIGN KEY (`cancelNodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `non_pocket_node` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `nodeId` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_non_pocket_node` FOREIGN KEY (`nodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `expression` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `inputNodeId` int(11) DEFAULT NULL,
    `outputNodeId` int(11) DEFAULT NULL,
    `description` varchar(255),
    `language` varchar(255),
    `expression` varchar(1000),
    `returnType` varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_node_inexpr` FOREIGN KEY (`inputNodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_node_outexpr` FOREIGN KEY (`outputNodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `node` ADD CONSTRAINT `fk_node_data_expr` FOREIGN KEY (`resourceDataExpressionId`) REFERENCES `expression` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `node` ADD CONSTRAINT `fk_node_run_expr` FOREIGN KEY (`resourceRunExpressionId`) REFERENCES `expression` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `node` ADD CONSTRAINT `fk_node_timer_expr` FOREIGN KEY (`timerExpressionId`) REFERENCES `expression` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `edge` ADD CONSTRAINT `fk_cond_expr` FOREIGN KEY (`conditionExpressionId`) REFERENCES `expression` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;



CREATE TABLE `folder` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `parentId` int(11) DEFAULT NULL,
    `workspaceId` int(11) NULL DEFAULT '1',
    `folder_name` varchar(40) DEFAULT NULL,
    `folder_description` varchar(1000) DEFAULT NULL,
    `creatorId` int(11) DEFAULT NULL,
    `date_created` datetime DEFAULT NULL,
    `modifiedById` int(11) DEFAULT NULL,
    `date_modified` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `folder_creator` (`creatorId`),
    KEY `folder_modified_by` (`modifiedById`),
    KEY `folder_workspace` (`workspaceId`),
    KEY `folder_folder` (`parentId`),
    CONSTRAINT `folder_creator` FOREIGN KEY (`creatorId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `folder_folder` FOREIGN KEY (`parentId`)
        REFERENCES `folder` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `folder_modified_by` FOREIGN KEY (`modifiedById`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `folder_workspace` FOREIGN KEY (`workspaceId`)
        REFERENCES `workspace` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `folder_process` (
    `folderId` int(11) DEFAULT NULL,
    `processId` int(11) DEFAULT NULL,
    PRIMARY KEY (`folderId` , `processId`),
    KEY `folder_process_folder` (`folderId`),
    KEY `folder_process_process` (`processId`),
    CONSTRAINT `folder_process_folder` FOREIGN KEY (`folderId`)
        REFERENCES `folder` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `folder_process_process` FOREIGN KEY (`processId`)
        REFERENCES `process` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `folder_subfolder` (
    `parentId` int(11) NOT NULL,
    `childId` int(11) NOT NULL,
    PRIMARY KEY (`parentId` , `childId`),
    KEY `folder_subfolder_parent` (`parentId`),
    KEY `folder_subfolder_child` (`childId`),
    CONSTRAINT `folder_subfolder_child` FOREIGN KEY (`childId`)
        REFERENCES `folder` (`id`)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `folder_subfolder_parent` FOREIGN KEY (`parentId`)
        REFERENCES `folder` (`id`)
        ON DELETE NO ACTION ON UPDATE NO ACTION
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `folder_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `folderId` int(11) NOT NULL,
    `userId` int(11) NOT NULL,
    `has_read` boolean not null default 1,
    `has_write` boolean not null default 1,
    `has_ownership` boolean not null default 1,
    PRIMARY KEY (`id`),
    KEY `fk_folder_user_folder` (`folderId`),
    KEY `fk_folder_user_user` (`userId`),
    CONSTRAINT `fk_folder_user_folder` FOREIGN KEY (`folderId`)
        REFERENCES `folder` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_folder_user_user` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `propagation_policy` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fragment_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `fragmentId` int(11) NOT NULL,
    `userId` int(11) NOT NULL,
    `has_read` boolean not null default 1,
    `has_write` boolean not null default 1,
    `has_ownership` boolean not null default 1,
    PRIMARY KEY (`id`),
    KEY `fragment_user_fragment` (`fragmentId`),
    KEY `fragment_user_user` (`userId`),
    CONSTRAINT `fragment_user_fragment` FOREIGN KEY (`fragmentId`)
        REFERENCES `fragment` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fragment_user_user` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `membership` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `userId` int(11) NOT NULL,
    `password` varchar(100) NOT NULL,
    `password_salt` varchar(100) NOT NULL,
    `mobile_pin` varchar(100) DEFAULT NULL,
    `email` varchar(200) NOT NULL,
    `password_question` varchar(50) NOT NULL,
    `password_answer` varchar(50) NOT NULL,
    `is_approved` boolean not null default 1,
    `is_locked` boolean not null default 1,
    `date_created` datetime NOT NULL,
    `failed_password_attempts` int(11) NOT NULL DEFAULT '0',
    `failed_answer_attempts` int(11) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`) USING BTREE,
    KEY (`userId`) USING BTREE,
    CONSTRAINT `FK_users` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `permission` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `row_guid` varchar(256) NOT NULL,
    `permission_name` varchar(45) NOT NULL,
    `permission_description` varchar(45) DEFAULT NULL,
    PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `processId` int(11) NOT NULL,
    `userId` int(11) NOT NULL,
    `has_read` tinyint(1) NOT NULL DEFAULT '0',
    `has_write` tinyint(1) NOT NULL DEFAULT '0',
    `has_ownership` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `fk_process_user_process` (`processId`),
    KEY `fk_process_user_users` (`userId`),
    CONSTRAINT `fk_process_user_process` FOREIGN KEY (`processId`)
        REFERENCES `process` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_process_user_users` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
    CONSTRAINT `FK_role_permission_permission` FOREIGN KEY (`permissionId`)
        REFERENCES `permission` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_role_permission_role` FOREIGN KEY (`roleId`)
        REFERENCES `role` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `row_guid` varchar(255) NOT NULL,
    `username` varchar(45) NOT NULL,
    `date_created` datetime NOT NULL,
    `first_name` varchar(45) NOT NULL,
    `last_name` varchar(45) NOT NULL,
    `last_activity_date` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `row_guid_UNIQUE` (`row_guid`),
    UNIQUE KEY `username_UNIQUE` (`username`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_role` (
    `roleId` int(11) NOT NULL,
    `userId` int(11) NOT NULL,
    PRIMARY KEY (`roleId` , `userId`),
    KEY `FK_user_role_users` (`userId`),
    KEY `FK_user_role_role` (`roleId`),
    CONSTRAINT `FK_user_role_role` FOREIGN KEY (`roleId`)
        REFERENCES `role` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_user_role_users` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `workspace` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `workspace_name` varchar(45) NOT NULL,
    `workspace_description` varchar(1000) DEFAULT NULL,
    `userId` int(11) NOT NULL,
    `date_created` datetime NOT NULL,
    PRIMARY KEY (`id`),
    KEY `workspace_user` (`userId`),
    CONSTRAINT `workspace_user` FOREIGN KEY (`userId`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `object` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `processModelVersionId` int(11) NOT NULL,
    `uri` varchar(40),
    `netId` varchar(40),
    `name` varchar(255),
    `configurable` boolean not null default 0,
    `type` varchar(4),
    `softType` varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_obj_pmv` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_attribute` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `objectId` int(11) DEFAULT NULL,
    `name` varchar(255),
    `value` longtext,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_obj_att_obj` FOREIGN KEY (`objectId`)
        REFERENCES `object` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_ref` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `objectId` int(11) DEFAULT NULL,
    `nodeId` int(11) DEFAULT NULL,
    `optional` boolean not null default 0,
    `consumed` boolean not null default 0,
    `type` varchar(6),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_objrefobj_pmv` FOREIGN KEY (`objectId`)
        REFERENCES `object` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_objref_node` FOREIGN KEY (`nodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `object_ref_attribute` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `objectRefId` int(11) DEFAULT NULL,
    `name` varchar(255),
    `value` longtext,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_objref_att` FOREIGN KEY (`objectRefId`)
        REFERENCES `object_ref` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `processModelVersionId` int(11) DEFAULT NULL,
    `uri` varchar(40),
    `originalId` varchar(40),
    `name` varchar(255),
    `configurable` boolean not null default 0,
    `type` varchar(8),
    `typeName` varchar(255) NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_res_pmv` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_specialisations` (
    `resourceId` int(11) NOT NULL,
    `specialisationId` int(11) NOT NULL,
    PRIMARY KEY (`resourceId` , `specialisationId`),
    CONSTRAINT `fk_resource` FOREIGN KEY (`resourceId`)
        REFERENCES `resource` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_special` FOREIGN KEY (`specialisationId`)
        REFERENCES `resource` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_attribute` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `resourceId` int(11) DEFAULT NULL,
    `name` varchar(255),
    `value` longtext,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_res_att_res` FOREIGN KEY (`resourceId`)
        REFERENCES `resource` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_ref` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `resourceId` int(11) DEFAULT NULL,
    `nodeId` int(11) DEFAULT NULL,
    `qualifier` varchar(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_resref_pmv` FOREIGN KEY (`resourceId`)
        REFERENCES `resource` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_resref_node` FOREIGN KEY (`nodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_ref_attribute` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `resourceRefId` int(11) DEFAULT NULL,
    `name` varchar(255),
    `value` longtext,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_resref_att` FOREIGN KEY (`resourceRefId`)
        REFERENCES `resource_ref` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `node_attribute` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `nodeId` int(11) DEFAULT NULL,
    `name` varchar(255),
    `value` longtext,
    `any` longtext NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_node_attributes` FOREIGN KEY (`nodeId`)
        REFERENCES `node` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `edge_attribute` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `edgeId` int(11) DEFAULT NULL,
    `name` varchar(255),
    `value` longtext,
    `any` longtext NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_edge_attributes` FOREIGN KEY (`edgeId`)
        REFERENCES `edge` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `process_model_attribute` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `processModelVersionId` int(11) DEFAULT NULL,
    `name` varchar(255),
    `value` longtext,
    `any` longtext NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_pmv_att_pmv` FOREIGN KEY (`processModelVersionId`)
        REFERENCES `process_model_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `cluster` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `size` int,
    `avg_fragment_size` float,
    `medoid_id` varchar(40),
    `benifit_cost_ratio` double,
    `std_effort` double,
    `refactoring_gain` int,
    PRIMARY KEY (`id`)
)  engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `fragment_version` ADD CONSTRAINT `fk_cluster_version` FOREIGN KEY (`clusterId`) REFERENCES `cluster` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE `fragment_distance` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `fragmentVersionId1` int(11) NOT NULL,
    `fragmentVersionId2` int(11) NOT NULL,
    `ged` double,
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_geds` (`fragmentVersionId1` , `fragmentVersionId2`),
    CONSTRAINT `fk_frag_version_1` FOREIGN KEY (`fragmentVersionId1`)
        REFERENCES `fragment_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_frag_version_2` FOREIGN KEY (`fragmentVersionId2`)
        REFERENCES `fragment_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `cluster_assignment` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `clusterId` int(11) NOT NULL,
    `fragmentVersionId` int(11) NOT NULL,
    `clone_id` varchar(40),
    `maximal` boolean,
    `core_object_nb` int,
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_cluster_assignments` (`fragmentVersionId` , `clusterId`),
    CONSTRAINT `fk_frag_version_assignment` FOREIGN KEY (`fragmentVersionId`)
        REFERENCES `fragment_version` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_cluster_assignment` FOREIGN KEY (`clusterId`)
        REFERENCES `cluster` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
)  engine=InnoDB DEFAULT CHARSET=utf8;



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
INSERT INTO `native_type` VALUES (4,'YAWL 2.2', 'yawl');
INSERT INTO `native_type` VALUES (5,'BPMN 2.0', 'bpmn');
/*!40000 ALTER TABLE `native_type` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'80da507e-cdd7-40f4-a9f8-b2d2edb12856','Administrator','Testing description 2');
INSERT INTO `role` VALUES (2,'0ecd70b4-a204-41cd-a246-e3fcef88f6fe','User','Testing');
INSERT INTO `role` VALUES (3,'72503ce0-d7cd-47b3-a33c-1b741d7599a1','Manaager','Middle role');
INSERT INTO `role` VALUES (4,'f8b91579-f061-47e8-9f73-e2691884058f','Manager','Middle role');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'75f4a46a-bd32-4fbb-ba7a-c50d06414fac','james','2012-05-23 11:52:48','Cameron','James','2012-05-23 11:52:48');
INSERT INTO `user` VALUES (2,'aaf24d0d-58f2-43b1-8dcc-bf99717b708f','chathura','2012-05-23 11:59:51','Chathura','Ekanayake','2012-05-23 11:59:51');
INSERT INTO `user` VALUES (3,'a393f9c2-e2ee-49ed-9b6a-a1269811764c','arthur','2012-05-23 12:07:24','Arthur','Ter Hofstede','2012-05-23 12:07:24');
INSERT INTO `user` VALUES (4,'b6701ee5-227b-493e-9b01-85aa33acd53b','Macri','2012-05-23 20:08:03','Marie','Fauvet','2012-05-23 20:08:03');
INSERT INTO `user` VALUES (5,'c81da91f-facc-4eff-b648-bdc1a2a5ebbe','larosa','2012-05-23 20:24:37','Marcello','La Rosa','2012-05-23 20:24:37');
INSERT INTO `user` VALUES (6,'c03eff4d-3672-4c91-bfea-36c67e2423f5','felix','2012-05-23 20:37:44','Felix','Mannhardt','2012-05-23 20:37:44');
INSERT INTO `user` VALUES (7,'fbcd5a9a-a224-40cb-8ab9-b12436d92835','raboczi','2012-05-23 20:40:26','Simon','Raboczi','2012-05-23 20:40:26');
INSERT INTO `user` VALUES (8,'ad1f7b60-1143-4399-b331-b887585a0f30','test3348@test.com','2012-05-28 16:51:05','Test','User','2012-05-28 16:51:05');
INSERT INTO `user` VALUES (9,'98cd118b-cda8-4920-9f9c-82441e4c0739','test5268@test.com','2012-06-16 11:43:16','Test','User','2012-06-16 11:43:16');
INSERT INTO `user` VALUES (10,'88d530d9-ce90-4d20-95a1-84fc7e98cc30','test3636@test.com','2012-06-16 11:56:00','Test','User','2012-06-16 11:56:00');
INSERT INTO `user` VALUES (11,'490281f5-c85c-4cfe-a31c-738da759ce1d','test1804@test.com','2012-06-16 12:01:35','Test','User','2012-06-16 12:01:35');
INSERT INTO `user` VALUES (12,'3a4ac7bf-ab4e-44a2-8848-65f4139b2dd1','test4631@test.com','2012-06-16 12:08:50','Test','User','2012-06-16 12:08:50');
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
INSERT INTO `membership` VALUES (8,8,'98f6bcd4621d373cade4e832627b4f6','username','','test3348@test.com','Test question','test',1,0,'2012-06-16 14:10:14',0,0);
INSERT INTO `membership` VALUES (9,9,'98f6bcd4621d373cade4e832627b4f6','username','','test5268@test.com','Test question','test',1,0,'2012-06-16 15:25:09',0,0);
INSERT INTO `membership` VALUES (10,10,'98f6bcd4621d373cade4e832627b4f6','username','','test3636@test.com','Test question','test',1,0,'2012-06-16 15:47:03',0,0);
INSERT INTO `membership` VALUES (11,11,'98f6bcd4621d373cade4e832627b4f6','username','','test1804@test.com','Test question','test',1,0,'2012-06-16 15:55:41',0,0);
INSERT INTO `membership` VALUES (12,12,'98f6bcd4621d373cade4e832627b4f6','username','','test4631@test.com','Test question','test',1,0,'2012-06-16 15:58:46',0,0);
/*!40000 ALTER TABLE `membership` ENABLE KEYS */;
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
INSERT INTO `user_role` VALUES (1,9);
INSERT INTO `user_role` VALUES (2,9);
INSERT INTO `user_role` VALUES (1,10);
INSERT INTO `user_role` VALUES (2,10);
INSERT INTO `user_role` VALUES (1,11);
INSERT INTO `user_role` VALUES (2,12);
INSERT INTO `user_role` VALUES (1,12);
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
INSERT INTO `search_history` VALUES (1,12,'airport');
INSERT INTO `search_history` VALUES (2,14,'gold coast');
INSERT INTO `search_history` VALUES (3,14,'goldcoast');
/*!40000 ALTER TABLE `search_history` ENABLE KEYS */;
UNLOCK TABLES;


SET FOREIGN_KEY_CHECKS=1;