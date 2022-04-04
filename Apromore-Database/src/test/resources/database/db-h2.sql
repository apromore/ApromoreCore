--
-- #%L
-- This file is part of "Apromore Core".
-- %%
-- Copyright (C) 2018 - 2022 Apromore Pty Ltd.
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Lesser General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Lesser Public License for more details.
--
-- You should have received a copy of the GNU General Lesser Public
-- License along with this program.  If not, see
-- <http://www.gnu.org/licenses/lgpl-3.0.html>.
-- #L%
--

CREATE USER apromore PASSWORD 'MAcri' ADMIN
;
CREATE TABLE batch_job_execution
(
   job_execution_id bigint PRIMARY KEY NOT NULL,
   version bigint,
   job_instance_id bigint NOT NULL,
   create_time timestamp NOT NULL,
   start_time timestamp,
   end_time timestamp,
   status varchar(10),
   exit_code varchar(20),
   exit_message text,
   last_updated timestamp
)
;
CREATE TABLE batch_job_execution_context
(
   job_execution_id bigint PRIMARY KEY NOT NULL,
   short_context text NOT NULL,
   serialized_context longtext
)
;
CREATE TABLE batch_job_execution_seq
(
   id bigint auto_increment NOT NULL
)
;
CREATE TABLE batch_job_instance
(
   job_instance_id bigint PRIMARY KEY NOT NULL,
   version bigint,
   job_name varchar(100) NOT NULL,
   job_key varchar(32)
)
;
CREATE TABLE batch_job_params
(
   job_instance_id bigint NOT NULL,
   type_cd varchar(6) NOT NULL,
   key_name varchar(100) NOT NULL,
   string_val varchar(250),
   date_val timestamp,
   long_val bigint,
   double_val double
)
;
CREATE TABLE batch_job_seq
(
   id bigint NOT NULL
)
;
CREATE TABLE batch_step_execution
(
   step_execution_id bigint PRIMARY KEY NOT NULL,
   version bigint NOT NULL,
   step_name varchar(100) NOT NULL,
   job_execution_id bigint NOT NULL,
   start_time timestamp NOT NULL,
   end_time timestamp,
   status varchar(10),
   commit_count bigint,
   read_count bigint,
   filter_count bigint,
   write_count bigint,
   read_skip_count bigint,
   write_skip_count bigint,
   process_skip_count bigint,
   rollback_count bigint,
   exit_code varchar(20),
   exit_message text,
   last_updated timestamp
)
;
CREATE TABLE batch_step_execution_context
(
   step_execution_id bigint PRIMARY KEY NOT NULL,
   short_context text NOT NULL,
   serialized_context longtext
)
;
CREATE TABLE batch_step_execution_seq
(
   id bigint NOT NULL
)
;
CREATE TABLE dashboard_layout
(
   id int auto_increment PRIMARY KEY NOT NULL,
   userId int NOT NULL,
   logId int NOT NULL,
   layout mediumtext,
)
;
CREATE TABLE edit_session
(
   id int auto_increment PRIMARY KEY NOT NULL,
   recordTime timestamp,
   userId int,
   processModelVersionId int NOT NULL,
   processId int,
   folderId int,
   original_branch_name varchar(40),
   new_branch_name varchar(40),
   version_number double,
   create_new_branch bit,
   nat_type varchar(20),
   annotation varchar(40),
   remove_fake_events bit,
   createDate varchar(40),
   lastUpdateDate varchar(40)
)
;
CREATE TABLE folder
(
   id int auto_increment PRIMARY KEY NOT NULL,
   parentId int,
   workspaceId int DEFAULT 1,
   folder_name varchar(100),
   folder_description text,
   creatorId int,
   date_created timestamp,
   modifiedById int,
   date_modified timestamp,
   ged_matrix_computation bit DEFAULT 0
)
;
CREATE TABLE folder_process
(
   folderId int DEFAULT 0 NOT NULL,
   processId int DEFAULT 0 NOT NULL,
   PRIMARY KEY (folderId,processId)
)
;
CREATE TABLE folder_subfolder
(
   parentId int NOT NULL,
   childId int NOT NULL,
   PRIMARY KEY (parentId,childId)
)
;
CREATE TABLE "group"
(
   id int auto_increment PRIMARY KEY NOT NULL,
   row_guid varchar(255) NOT NULL,
   name varchar(45) NOT NULL,
   type varchar(10) NOT NULL
)
;
CREATE TABLE group_folder
(
   id int auto_increment PRIMARY KEY NOT NULL,
   groupId int NOT NULL,
   folderId int NOT NULL,
   has_read bit DEFAULT 0 NOT NULL,
   has_write bit DEFAULT 0 NOT NULL,
   has_ownership bit DEFAULT 0 NOT NULL
)
;
CREATE TABLE group_process
(
   id int auto_increment PRIMARY KEY NOT NULL,
   groupId int NOT NULL,
   processId int NOT NULL,
   has_read bit DEFAULT 0 NOT NULL,
   has_write bit DEFAULT 0 NOT NULL,
   has_ownership bit DEFAULT 0 NOT NULL
)
;
CREATE TABLE group_log (
  id int auto_increment PRIMARY KEY NOT NULL,
  groupId int NOT NULL,
  logId int NOT NULL,
  has_read bit DEFAULT 0 NOT NULL,
  has_write bit DEFAULT 0 NOT NULL,
  has_ownership bit DEFAULT 0 NOT NULL
)
;
CREATE TABLE history_event (
  id int auto_increment PRIMARY KEY NOT NULL,
  status varchar(50) NOT NULL,
  type varchar(50) NOT NULL,
  occurDate timestamp DEFAULT NULL,
  userId int(11) DEFAULT NULL
)
;
CREATE TABLE membership
(
   id int auto_increment PRIMARY KEY NOT NULL,
   userId int NOT NULL,
   password varchar(100) NOT NULL,
   password_salt varchar(100) NOT NULL,
   mobile_pin varchar(100) DEFAULT NULL,
   email varchar(200) NOT NULL,
   password_question varchar(50) DEFAULT NULL,
   password_answer varchar(50) DEFAULT NULL,
   is_approved bit DEFAULT 1 NOT NULL,
   is_locked bit DEFAULT 1 NOT NULL,
   date_created timestamp NOT NULL,
   failed_password_attempts int DEFAULT 0 NOT NULL,
   failed_answer_attempts int DEFAULT 0 NOT NULL
)
;
CREATE TABLE native
(
   id int auto_increment PRIMARY KEY NOT NULL,
   content longtext,
   lastUpdateDate varchar(40),
   nativeTypeId int
)
;
CREATE TABLE native_type
(
   id int auto_increment PRIMARY KEY NOT NULL,
   nat_type varchar(20) NOT NULL,
   extension varchar(10)
)
;
CREATE TABLE permission
(
   id int auto_increment PRIMARY KEY NOT NULL,
   row_guid text NOT NULL,
   permission_name varchar(45) NOT NULL,
   permission_description varchar(45)
)
;
CREATE TABLE process
(
   id int auto_increment PRIMARY KEY NOT NULL,
   name varchar(100),
   domain varchar(40),
   owner int,
   nativeTypeId int,
   folderId int,
   ranking varchar(10),
   createDate varchar(40)
)
;
CREATE TABLE log (
  id int auto_increment PRIMARY KEY NOT NULL,
  folderId int DEFAULT NULL,
  name varchar(255) NOT NULL,
  file_path varchar(255) NOT NULL,
  domain varchar(255),
  ranking varchar(10),
  createDate varchar(40),
  owner int
)
;
CREATE TABLE process_branch
(
   id int auto_increment PRIMARY KEY NOT NULL,
   branch_name varchar(255),
   processId int,
   createDate varchar(40),
   lastUpdateDate varchar(40),
   sourceProcessModelVersion int,
   currentProcessModelVersion int
)
;
CREATE TABLE process_model_attribute
(
   id int auto_increment PRIMARY KEY NOT NULL,
   processModelVersionId int,
   name varchar(255),
   value longtext,
   any longtext
)
;
CREATE TABLE process_model_version
(
   id int auto_increment PRIMARY KEY NOT NULL,
   branchId int,
   rootFragmentVersionId int,
   nativeId int,
   canonicalId int,
   nativeTypeId int,
   originalId varchar(200),
   version_number double,
   change_propagation int,
   lock_status int,
   num_nodes int,
   num_edges int,
   createDate varchar(40),
   lastUpdateDate varchar(40)
)
;
CREATE TABLE role
(
   id int auto_increment PRIMARY KEY NOT NULL,
   row_guid text NOT NULL,
   role_name varchar(45) NOT NULL,
   description varchar(200)
)
;
CREATE TABLE role_permission
(
   roleId int NOT NULL,
   permissionId int NOT NULL,
   PRIMARY KEY (roleId,permissionId)
)
;
CREATE TABLE search_history
(
   id int auto_increment PRIMARY KEY NOT NULL,
   userId int,
   position int,
   search varchar(200)
)
;
CREATE TABLE user
(
   id int auto_increment PRIMARY KEY NOT NULL,
   row_guid varchar(255) NOT NULL,
   username varchar(45) NOT NULL,
   date_created timestamp NOT NULL,
   first_name varchar(45) NOT NULL,
   last_name varchar(45) NOT NULL,
   last_activity_date timestamp,
   groupId int NOT NULL,
   organization varchar(255),
   role varchar(255),
   country varchar(255),
   phone varchar(255),
   subscription varchar(255)
)
;
CREATE TABLE user_group
(
   userId int NOT NULL,
   groupId int NOT NULL,
   PRIMARY KEY (userId,groupId),
)
;
ALTER TABLE user_group
ADD CONSTRAINT fk_user_group_user FOREIGN KEY (userId) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE user_group
ADD CONSTRAINT fk_user_group_group FOREIGN KEY (groupId) REFERENCES "group"(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE TABLE user_role
(
   roleId int NOT NULL,
   userId int NOT NULL,
   PRIMARY KEY (roleId,userId)
)
;
CREATE TABLE workspace
(
   id int auto_increment PRIMARY KEY NOT NULL,
   workspace_name varchar(45) NOT NULL,
   workspace_description text,
   userId int NOT NULL,
   date_created timestamp NOT NULL
)
;
ALTER TABLE batch_job_execution
ADD CONSTRAINT job_instance_execution_fk
FOREIGN KEY (job_instance_id)
REFERENCES batch_job_instance(job_instance_id)
;
CREATE UNIQUE INDEX id_primary_batch_job_execution ON batch_job_execution(job_execution_id)
;
CREATE INDEX idx_job_execution ON batch_job_execution(job_instance_id)
;
ALTER TABLE batch_job_execution_context
ADD CONSTRAINT job_exec_ctx_fk
FOREIGN KEY (job_execution_id)
REFERENCES batch_job_execution(job_execution_id)
;
CREATE UNIQUE INDEX id_primary_batch_job_execution_context ON batch_job_execution_context(job_execution_id)
;
CREATE UNIQUE INDEX id_primary_batch_job_instance ON batch_job_instance(job_instance_id)
;
CREATE INDEX idx_job_instance ON batch_job_instance
(
  job_name,
  job_key
)
;
CREATE UNIQUE INDEX job_inst_un ON batch_job_instance
(
  job_name,
  job_key
)
;
ALTER TABLE batch_job_params
ADD CONSTRAINT job_instance_params_fk
FOREIGN KEY (job_instance_id)
REFERENCES batch_job_instance(job_instance_id)
;
CREATE INDEX job_instance_params_fk ON batch_job_params(job_instance_id)
;
ALTER TABLE batch_step_execution
ADD CONSTRAINT job_execution_step_fk
FOREIGN KEY (job_execution_id)
REFERENCES batch_job_execution(job_execution_id)
;
CREATE UNIQUE INDEX id_primary_batch_step_execution ON batch_step_execution(step_execution_id)
;
CREATE INDEX job_execution_step_fk ON batch_step_execution(job_execution_id)
;
CREATE INDEX idx_step_execution ON batch_step_execution
(
  step_name,
  job_execution_id
)
;
CREATE INDEX idx_step_execution_version ON batch_step_execution(version)
;
ALTER TABLE batch_step_execution_context
ADD CONSTRAINT step_exec_ctx_fk
FOREIGN KEY (step_execution_id)
REFERENCES batch_step_execution(step_execution_id)
;
CREATE UNIQUE INDEX id_primary_batch_step_execution_context ON batch_step_execution_context(step_execution_id)
;

ALTER TABLE dashboard_layout
ADD CONSTRAINT dashboard_layout_ibfk_1
FOREIGN KEY (userId)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE dashboard_layout
ADD CONSTRAINT dashboard_layout_ibfk_2
FOREIGN KEY (logId)
REFERENCES log(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX un_dashboard_layout_userId ON dashboard_layout(userId)
;
CREATE UNIQUE INDEX un_dashboard_layout_logId ON dashboard_layout(logId)
;

ALTER TABLE edit_session
ADD CONSTRAINT fk_edit_session
FOREIGN KEY (processModelVersionId)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE edit_session
ADD CONSTRAINT fk_edit_session1
FOREIGN KEY (userId)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE edit_session
ADD CONSTRAINT fk_edit_session_process
FOREIGN KEY (processId)
REFERENCES process(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE edit_session
ADD CONSTRAINT fk_edit_session_folder
FOREIGN KEY (folderId)
REFERENCES folder(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_edit_session_process ON edit_session(processId)
;
CREATE INDEX fk_edit_session ON edit_session(processModelVersionId)
;
CREATE INDEX fk_edit_session_folder ON edit_session(folderId)
;
CREATE UNIQUE INDEX id_primary_edit_session ON edit_session(id)
;
CREATE INDEX fk_edit_session1 ON edit_session(userId)
;
ALTER TABLE folder
ADD CONSTRAINT folder_folder
FOREIGN KEY (parentId)
REFERENCES folder(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE folder
ADD CONSTRAINT folder_creator
FOREIGN KEY (creatorId)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE folder
ADD CONSTRAINT folder_workspace
FOREIGN KEY (workspaceId)
REFERENCES workspace(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE folder
ADD CONSTRAINT folder_modified_by
FOREIGN KEY (modifiedById)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX folder_workspace ON folder(workspaceId)
;
CREATE UNIQUE INDEX id_primary_folder ON folder(id)
;
CREATE INDEX folder_modified_by ON folder(modifiedById)
;
CREATE INDEX folder_folder ON folder(parentId)
;
CREATE INDEX folder_creator ON folder(creatorId)
;
ALTER TABLE folder_process
ADD CONSTRAINT folder_process_process
FOREIGN KEY (processId)
REFERENCES process(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE folder_process
ADD CONSTRAINT folder_process_folder
FOREIGN KEY (folderId)
REFERENCES folder(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_folder_process ON folder_process
(
  folderId,
  processId
)
;
CREATE INDEX folder_process_process ON folder_process(processId)
;
CREATE INDEX folder_process_folder ON folder_process(folderId)
;
ALTER TABLE folder_subfolder
ADD CONSTRAINT folder_subfolder_child
FOREIGN KEY (childId)
REFERENCES folder(id)
;
ALTER TABLE folder_subfolder
ADD CONSTRAINT folder_subfolder_parent
FOREIGN KEY (parentId)
REFERENCES folder(id)
;
CREATE UNIQUE INDEX id_primary_folder_user1 ON folder_subfolder
(
  parentId,
  childId
)
;
CREATE INDEX folder_subfolder_child ON folder_subfolder(childId)
;
CREATE INDEX folder_subfolder_parent ON folder_subfolder(parentId)
;
ALTER TABLE membership
ADD CONSTRAINT FK_users
FOREIGN KEY (userId)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_membership ON membership(id)
;
CREATE UNIQUE INDEX un_userId ON membership(userId)
;
CREATE UNIQUE INDEX un_email ON membership(email)
;
ALTER TABLE native
ADD CONSTRAINT fk_native
FOREIGN KEY (nativeTypeId)
REFERENCES native_type(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_native ON native(id)
;
CREATE INDEX fk_native ON native(nativeTypeId)
;
CREATE UNIQUE INDEX id_primary_native_type ON native_type(id)
;
CREATE INDEX idx_native_type ON native_type
(
  nat_type,
  extension
)
;
CREATE UNIQUE INDEX id_primary_permission ON permission(id)
;
ALTER TABLE process
ADD CONSTRAINT fk_process2
FOREIGN KEY (nativeTypeId)
REFERENCES native_type(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process
ADD CONSTRAINT fk_process1
FOREIGN KEY (owner)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process
ADD CONSTRAINT fk_folder
FOREIGN KEY (folderId)
REFERENCES folder(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_process ON process(id)
;
CREATE INDEX fk_process2 ON process(nativeTypeId)
;
CREATE INDEX fk_users ON process(owner)
;
CREATE INDEX idx_process_name ON process(name)
;
CREATE INDEX fk_folder ON process(folderId)
;
ALTER TABLE process_branch
ADD CONSTRAINT fk_source_version
FOREIGN KEY (sourceProcessModelVersion)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process_branch
ADD CONSTRAINT fk_process_branch
FOREIGN KEY (processId)
REFERENCES process(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process_branch
ADD CONSTRAINT fk_current_version
FOREIGN KEY (currentProcessModelVersion)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX idx_branch_name ON process_branch(branch_name)
;
CREATE INDEX fk_process_branch ON process_branch(processId)
;
CREATE UNIQUE INDEX id_primary_process_branch ON process_branch(id)
;
CREATE INDEX fk_current_version ON process_branch(currentProcessModelVersion)
;
CREATE INDEX fk_source_version ON process_branch(sourceProcessModelVersion)
;
ALTER TABLE process_model_attribute
ADD CONSTRAINT fk_pmv_att_pmv
FOREIGN KEY (processModelVersionId)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_pmv_att_pmv ON process_model_attribute(processModelVersionId)
;
CREATE UNIQUE INDEX id_primary_process_model_attribute ON process_model_attribute(id)
;
ALTER TABLE process_model_version
ADD CONSTRAINT fk_process_native
FOREIGN KEY (nativeId)
REFERENCES native(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process_model_version
ADD CONSTRAINT fk_process_native_type
FOREIGN KEY (nativeTypeId)
REFERENCES native_type(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process_model_version
ADD CONSTRAINT fk_process_branch_model_version
FOREIGN KEY (branchId)
REFERENCES process_branch(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_process_native ON process_model_version(nativeId)
;
CREATE INDEX fk_process_branch_model_version1 ON process_model_version(rootFragmentVersionId)
;
CREATE UNIQUE INDEX id_primary_process_model_version ON process_model_version(id)
;
CREATE INDEX idx_pmv_lock ON process_model_version(lock_status)
;
CREATE INDEX fk_process_branch_model_version ON process_model_version(branchId)
;
CREATE INDEX fk_process_native_type ON process_model_version(nativeTypeId)
;
CREATE INDEX idx_pmv_version ON process_model_version(version_number)
;
CREATE INDEX fk_process_canonical ON process_model_version(canonicalId)
;
CREATE UNIQUE INDEX id_primary_role ON role(id)
;
ALTER TABLE role_permission
ADD CONSTRAINT FK_role_permission_permission
FOREIGN KEY (permissionId)
REFERENCES permission(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE role_permission
ADD CONSTRAINT FK_role_permission_role
FOREIGN KEY (roleId)
REFERENCES role(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX FK_role_permission_permission ON role_permission(permissionId)
;
CREATE UNIQUE INDEX id_primary_role_permission ON role_permission
(
  roleId,
  permissionId
)
;
CREATE INDEX FK_role_permission_role ON role_permission(roleId)
;
ALTER TABLE search_history
ADD CONSTRAINT fk_search
FOREIGN KEY (userId)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_search_history ON search_history(id)
;
CREATE UNIQUE INDEX un_search ON search_history
(
  userId,
  search
)
;
CREATE UNIQUE INDEX id_primary_user ON user(id)
;
CREATE UNIQUE INDEX row_guid_UNIQUE ON user(row_guid)
;
CREATE INDEX idx_user_username ON user(username)
;
CREATE UNIQUE INDEX username_UNIQUE ON user(username)
;
ALTER TABLE user_role
ADD CONSTRAINT FK_user_role_role
FOREIGN KEY (roleId)
REFERENCES role(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE user_role
ADD CONSTRAINT FK_user_role_users
FOREIGN KEY (userId)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX FK_user_role_role ON user_role(roleId)
;
CREATE UNIQUE INDEX id_primary_user_role ON user_role
(
  roleId,
  userId
)
;
CREATE INDEX FK_user_role_users ON user_role(userId)
;
ALTER TABLE workspace
ADD CONSTRAINT workspace_user
FOREIGN KEY (userId)
REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_workspace ON workspace(id)
;
CREATE INDEX workspace_user ON workspace(userId)
;

CREATE VIEW keywords AS
  SELECT CAST(process.id AS varchar(100)) AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId FROM process UNION
  SELECT process.name AS value,         'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId FROM process UNION
  SELECT process.domain AS value,       'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId FROM process UNION
  SELECT native_type.nat_type AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    FROM process join native_type ON (process.nativeTypeId = native_type.id) UNION
/*
  SELECT user.first_name AS value,      'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    FROM process join user ON (process.owner = user.username) UNION
  SELECT user.last_name AS value,       'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    FROM process join user ON (process.owner = user.username) UNION
*/
  SELECT process_branch.branch_name AS value, 'process' AS type, process_branch.processId AS processId, NULL AS logId, NULL AS folderId FROM process_branch UNION
  SELECT CAST(log.id AS varchar(100)) AS value, 'log' AS type, NULL AS processId, log.id AS logId, NULL AS folderId FROM log UNION
  SELECT log.name AS value,   'log' AS type, NULL AS processId, log.id AS logId, NULL AS folderId FROM log UNION
  SELECT log.domain AS value, 'log' AS type, NULL AS processId, log.id AS logId, NULL AS folderId FROM log UNION
  SELECT folder.folder_name AS value, 'folder' AS type, NULL AS processId, NULL AS logId, folder.id AS folderId FROM folder
;

CREATE TABLE process_publish
(
   publishid varchar(36) PRIMARY KEY NOT NULL,
   published bit,
   processId int,
)
;
