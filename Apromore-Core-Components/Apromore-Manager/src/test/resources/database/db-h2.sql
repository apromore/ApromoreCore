CREATE USER apromore PASSWORD 'MAcri' ADMIN
;
CREATE TABLE annotation
(
   id int auto_increment PRIMARY KEY NOT NULL,
   nativeId int,
   processModelVersionId int,
   name varchar(40),
   content longtext,
   lastUpdateDate varchar(40)
)
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
CREATE TABLE cancel_nodes
(
   nodeId int NOT NULL,
   cancelNodeId int NOT NULL,
   PRIMARY KEY (nodeId,cancelNodeId)
)
;
CREATE TABLE canonical
(
   id int auto_increment PRIMARY KEY NOT NULL,
   content longtext,
   lastUpdateDate varchar(40)
)
;
CREATE TABLE cluster
(
   id int auto_increment PRIMARY KEY NOT NULL,
   size int,
   avg_fragment_size real,
   medoid_id varchar(40),
   benifit_cost_ratio double,
   std_effort double,
   refactoring_gain int
)
;
CREATE TABLE cluster_assignment
(
   id int auto_increment PRIMARY KEY NOT NULL,
   clusterId int NOT NULL,
   fragmentVersionId int NOT NULL,
   clone_id varchar(40),
   maximal bit,
   core_object_nb int
)
;
CREATE TABLE edge
(
   id int auto_increment PRIMARY KEY NOT NULL,
   uri varchar(256),
   sourceNodeId int,
   targetNodeId int,
   cancelNodeId int,
   originalId varchar(200),
   conditionExpressionId int,
   def bit DEFAULT 0
)
;
CREATE TABLE edge_attribute
(
   id int auto_increment PRIMARY KEY NOT NULL,
   edgeId int,
   name varchar(255),
   value longtext,
   any longtext
)
;
CREATE TABLE edge_mapping
(
   id int auto_increment PRIMARY KEY NOT NULL,
   fragmentVersionId int,
   edgeId int
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
CREATE TABLE expression
(
   id int auto_increment PRIMARY KEY NOT NULL,
   inputNodeId int,
   outputNodeId int,
   description varchar(255),
   language varchar(255),
   expression text,
   returnType varchar(255)
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
CREATE TABLE fragment
(
   id int auto_increment PRIMARY KEY NOT NULL,
   propagation_policy int
)
;
CREATE TABLE fragment_distance
(
   id int auto_increment PRIMARY KEY NOT NULL,
   fragmentVersionId1 int NOT NULL,
   fragmentVersionId2 int NOT NULL,
   ged double
)
;
CREATE TABLE fragment_version
(
   id int auto_increment PRIMARY KEY NOT NULL,
   uri varchar(40),
   fragmentId int,
   clusterId int,
   entryNodeId int,
   exitNodeId int,
   child_mapping_code text,
   derived_from_fragment int,
   lock_status int,
   lock_count int,
   fragment_size int,
   fragment_type varchar(10),
   newest_neighbor varchar(40)
)
;
CREATE TABLE fragment_version_dag
(
   id int auto_increment PRIMARY KEY NOT NULL,
   fragmentVersionId int,
   childFragmentVersionId int,
   pocketId varchar(40)
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
CREATE TABLE node
(
   id int auto_increment PRIMARY KEY NOT NULL,
   uri varchar(256),
   subVersionId int,
   originalId varchar(200),
   netId varchar(200),
   name text,
   graphType varchar(50),
   nodeType varchar(50),
   configuration bit DEFAULT 0,
   teamWork bit DEFAULT 0,
   allocation varchar(40),
   resourceDataExpressionId int,
   resourceRunExpressionId int,
   timerExpressionId int,
   timeDate timestamp,
   timeDuration varchar(100),
   messageDirection varchar(10)
)
;
CREATE TABLE node_attribute
(
   id int auto_increment PRIMARY KEY NOT NULL,
   nodeId int,
   name varchar(255),
   value longtext,
   any longtext
)
;
CREATE TABLE node_mapping
(
   id int auto_increment PRIMARY KEY NOT NULL,
   fragmentVersionId int,
   nodeId int
)
;
CREATE TABLE object
(
   id int auto_increment PRIMARY KEY NOT NULL,
   processModelVersionId int NOT NULL,
   uri varchar(40),
   netId varchar(40),
   name varchar(255),
   configurable bit DEFAULT 0 NOT NULL,
   type varchar(30),
   softType varchar(255)
)
;
CREATE TABLE object_attribute
(
   id int auto_increment PRIMARY KEY NOT NULL,
   objectId int,
   name varchar(255),
   value longtext,
   any longtext
)
;
CREATE TABLE object_ref
(
   id int auto_increment PRIMARY KEY NOT NULL,
   objectId int,
   nodeId int,
   optional bit DEFAULT 0 NOT NULL,
   consumed bit DEFAULT 0 NOT NULL,
   type varchar(30)
)
;
CREATE TABLE object_ref_attribute
(
   id int auto_increment PRIMARY KEY NOT NULL,
   objectRefId int,
   name varchar(255),
   value longtext,
   any longtext
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
CREATE TABLE process_fragment_map
(
   processModelVersionId int NOT NULL,
   fragmentVersionId int NOT NULL,
   PRIMARY KEY (processModelVersionId,fragmentVersionId)
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
CREATE TABLE resource
(
   id int auto_increment PRIMARY KEY NOT NULL,
   processModelVersionId int,
   uri varchar(40),
   originalId varchar(40),
   name varchar(255),
   configurable bit DEFAULT 0 NOT NULL,
   type varchar(30),
   typeName varchar(255)
)
;
CREATE TABLE resource_attribute
(
   id int auto_increment PRIMARY KEY NOT NULL,
   resourceId int,
   name varchar(255),
   value longtext,
   any longtext
)
;
CREATE TABLE resource_ref
(
   id int auto_increment PRIMARY KEY NOT NULL,
   resourceId int,
   nodeId int,
   qualifier varchar(255)
)
;
CREATE TABLE resource_ref_attribute
(
   id int auto_increment PRIMARY KEY NOT NULL,
   resourceRefId int,
   name varchar(255),
   value longtext,
   any longtext
)
;
CREATE TABLE resource_specialisations
(
   resourceId int NOT NULL,
   specialisationId int NOT NULL,
   PRIMARY KEY (resourceId,specialisationId)
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
CREATE TABLE statistic (
  count bigint(20) auto_increment PRIMARY KEY NOT NULL,
  id varbinary(16) NOT NULL,
  logid int(11) DEFAULT NULL,
  pid varbinary(16) DEFAULT NULL,
  stat_key varchar(1023) DEFAULT NULL,
  stat_value varchar(1023) DEFAULT NULL
);
CREATE INDEX idx_logid ON statistic(logid);
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
ALTER TABLE annotation
ADD CONSTRAINT fk_annotation2
FOREIGN KEY (processModelVersionId)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE annotation
ADD CONSTRAINT fk_annotation1
FOREIGN KEY (nativeId)
REFERENCES native(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_annotation ON annotation(id)
;
CREATE INDEX fk_annotation1 ON annotation(nativeId)
;
CREATE UNIQUE INDEX un_annotation ON annotation
(
  processModelVersionId,
  name
)
;
CREATE INDEX idx_annotation_name ON annotation(name)
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
ALTER TABLE cancel_nodes
ADD CONSTRAINT fk_cancel_child
FOREIGN KEY (cancelNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE cancel_nodes
ADD CONSTRAINT fk_cancel_parent
FOREIGN KEY (nodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_cancel_nodes ON cancel_nodes
(
  nodeId,
  cancelNodeId
)
;
CREATE INDEX fk_cancel_child ON cancel_nodes(cancelNodeId)
;
CREATE UNIQUE INDEX id_primary_canonical ON canonical(id)
;
CREATE UNIQUE INDEX id_primary_cluster ON cluster(id)
;
CREATE INDEX idx_cluster ON cluster
(
  size,
  avg_fragment_size,
  benifit_cost_ratio
)
;
ALTER TABLE cluster_assignment
ADD CONSTRAINT fk_cluster_assignment
FOREIGN KEY (clusterId)
REFERENCES cluster(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE cluster_assignment
ADD CONSTRAINT fk_frag_version_assignment
FOREIGN KEY (fragmentVersionId)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_cluster_assignment ON cluster_assignment(id)
;
CREATE INDEX fk_cluster_assignment ON cluster_assignment(clusterId)
;
CREATE UNIQUE INDEX un_cluster_assignments ON cluster_assignment
(
  fragmentVersionId,
  clusterId
)
;
ALTER TABLE edge
ADD CONSTRAINT fk_target_node
FOREIGN KEY (targetNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE edge
ADD CONSTRAINT fk_source_node
FOREIGN KEY (sourceNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE edge
ADD CONSTRAINT fk_cond_expr
FOREIGN KEY (conditionExpressionId)
REFERENCES expression(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE edge
ADD CONSTRAINT fk_cancel_node
FOREIGN KEY (cancelNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_edge ON edge(id)
;
CREATE INDEX fk_cancel_node ON edge(cancelNodeId)
;
CREATE INDEX fk_target_node ON edge(targetNodeId)
;
CREATE INDEX fk_source_node ON edge(sourceNodeId)
;
CREATE INDEX fk_cond_expr ON edge(conditionExpressionId)
;
ALTER TABLE edge_attribute
ADD CONSTRAINT fk_edge_attributes
FOREIGN KEY (edgeId)
REFERENCES edge(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_edge_attribute ON edge_attribute(id)
;
CREATE INDEX fk_edge_attributes ON edge_attribute(edgeId)
;
ALTER TABLE edge_mapping
ADD CONSTRAINT fk_em_edge
FOREIGN KEY (edgeId)
REFERENCES edge(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE edge_mapping
ADD CONSTRAINT fk_em_fragment_version
FOREIGN KEY (fragmentVersionId)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_em_edge ON edge_mapping(edgeId)
;
CREATE UNIQUE INDEX id_primary_edge_mapping ON edge_mapping(id)
;
CREATE UNIQUE INDEX un_edge_mapping ON edge_mapping
(
  fragmentVersionId,
  edgeId
)
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
ALTER TABLE expression
ADD CONSTRAINT fk_node_outexpr
FOREIGN KEY (outputNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE expression
ADD CONSTRAINT fk_node_inexpr
FOREIGN KEY (inputNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_expression ON expression(id)
;
CREATE INDEX fk_node_inexpr ON expression(inputNodeId)
;
CREATE INDEX fk_node_outexpr ON expression(outputNodeId)
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
CREATE UNIQUE INDEX id_primary_fragment ON fragment(id)
;
ALTER TABLE fragment_distance
ADD CONSTRAINT fk_frag_version_2
FOREIGN KEY (fragmentVersionId2)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE fragment_distance
ADD CONSTRAINT fk_frag_version_1
FOREIGN KEY (fragmentVersionId1)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX idx_fragment_distance ON fragment_distance(ged)
;
CREATE UNIQUE INDEX id_primary_fragment_distance ON fragment_distance(id)
;
CREATE INDEX fk_frag_version_2 ON fragment_distance(fragmentVersionId2)
;
CREATE UNIQUE INDEX un_geds ON fragment_distance
(
  fragmentVersionId1,
  fragmentVersionId2
)
;
ALTER TABLE fragment_version
ADD CONSTRAINT fk_fragments_version
FOREIGN KEY (fragmentId)
REFERENCES fragment(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE fragment_version
ADD CONSTRAINT fk_cluster_version
FOREIGN KEY (clusterId)
REFERENCES cluster(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE fragment_version
ADD CONSTRAINT fk_entry_node
FOREIGN KEY (entryNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE fragment_version
ADD CONSTRAINT fk_exit_node
FOREIGN KEY (exitNodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_cluster_version ON fragment_version(clusterId)
;
CREATE INDEX fk_fragments_version ON fragment_version(fragmentId)
;
CREATE UNIQUE INDEX id_primary_fragment_version ON fragment_version(id)
;
CREATE INDEX idx_fv_lock ON fragment_version(lock_status)
;
CREATE INDEX idx_fv_sizetype ON fragment_version
(
  fragment_size,
  fragment_type
)
;
ALTER TABLE fragment_version_dag
ADD CONSTRAINT fk_fragment_version_dag
FOREIGN KEY (fragmentVersionId)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE fragment_version_dag
ADD CONSTRAINT fk_child_fragment_version_dag
FOREIGN KEY (childFragmentVersionId)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_child_fragment_version_dag ON fragment_version_dag(childFragmentVersionId)
;
CREATE UNIQUE INDEX un_fragment_version_dag ON fragment_version_dag
(
  fragmentVersionId,
  childFragmentVersionId,
  pocketId
)
;
CREATE UNIQUE INDEX id_primary_fragment_version_dag ON fragment_version_dag(id)
;
CREATE INDEX idx_fvd_pocket ON fragment_version_dag(pocketId)
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
ALTER TABLE node
ADD CONSTRAINT fk_node_timer_expr
FOREIGN KEY (timerExpressionId)
REFERENCES expression(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE node
ADD CONSTRAINT fk_node_run_expr
FOREIGN KEY (resourceRunExpressionId)
REFERENCES expression(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE node
ADD CONSTRAINT fk_node_data_expr
FOREIGN KEY (resourceDataExpressionId)
REFERENCES expression(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE node
ADD CONSTRAINT fk_node_subversion
FOREIGN KEY (subVersionId)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_node_timer_expr ON node(timerExpressionId)
;
CREATE UNIQUE INDEX id_primary_node ON node(id)
;
CREATE INDEX fk_node_subversion ON node(subVersionId)
;
CREATE INDEX fk_node_run_expr ON node(resourceRunExpressionId)
;
CREATE INDEX fk_node_data_expr ON node(resourceDataExpressionId)
;
ALTER TABLE node_attribute
ADD CONSTRAINT fk_node_attributes
FOREIGN KEY (nodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_node_attribute ON node_attribute(id)
;
CREATE INDEX fk_node_attributes ON node_attribute(nodeId)
;
ALTER TABLE node_mapping
ADD CONSTRAINT fk_nm_node
FOREIGN KEY (nodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE node_mapping
ADD CONSTRAINT fk_nm_fragment_version
FOREIGN KEY (fragmentVersionId)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX un_node_mapping ON node_mapping
(
  fragmentVersionId,
  nodeId
)
;
CREATE UNIQUE INDEX id_primary_node_mapping ON node_mapping(id)
;
CREATE INDEX fk_nm_node ON node_mapping(nodeId)
;
ALTER TABLE object
ADD CONSTRAINT fk_obj_pmv
FOREIGN KEY (processModelVersionId)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_object ON object(id)
;
CREATE INDEX fk_obj_pmv ON object(processModelVersionId)
;
ALTER TABLE object_attribute
ADD CONSTRAINT fk_obj_att_obj
FOREIGN KEY (objectId)
REFERENCES object(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_object_attribute ON object_attribute(id)
;
CREATE INDEX fk_obj_att_obj ON object_attribute(objectId)
;
ALTER TABLE object_ref
ADD CONSTRAINT fk_objrefobj_pmv
FOREIGN KEY (objectId)
REFERENCES object(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE object_ref
ADD CONSTRAINT fk_objref_node
FOREIGN KEY (nodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_objref_node ON object_ref(nodeId)
;
CREATE UNIQUE INDEX id_primary_object_ref ON object_ref(id)
;
CREATE INDEX fk_objrefobj_pmv ON object_ref(objectId)
;
ALTER TABLE object_ref_attribute
ADD CONSTRAINT fk_objref_att
FOREIGN KEY (objectRefId)
REFERENCES object_ref(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_object_ref_attribute ON object_ref_attribute(id)
;
CREATE INDEX fk_objref_att ON object_ref_attribute(objectRefId)
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
ALTER TABLE process_fragment_map
ADD CONSTRAINT fk_fragment_versions_map
FOREIGN KEY (fragmentVersionId)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process_fragment_map
ADD CONSTRAINT fk_process_model_versions_map
FOREIGN KEY (processModelVersionId)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_process_fragment_map ON process_fragment_map
(
  processModelVersionId,
  fragmentVersionId
)
;
CREATE INDEX fk_fragment_versions_map ON process_fragment_map(fragmentVersionId)
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
ALTER TABLE process_model_version
ADD CONSTRAINT fk_process_canonical
FOREIGN KEY (canonicalId)
REFERENCES canonical(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE process_model_version
ADD CONSTRAINT fk_process_branch_model_version1
FOREIGN KEY (rootFragmentVersionId)
REFERENCES fragment_version(id) ON DELETE CASCADE ON UPDATE CASCADE
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
ALTER TABLE resource
ADD CONSTRAINT fk_res_pmv
FOREIGN KEY (processModelVersionId)
REFERENCES process_model_version(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE INDEX fk_res_pmv ON resource(processModelVersionId)
;
CREATE UNIQUE INDEX id_primary_resource ON resource(id)
;
ALTER TABLE resource_attribute
ADD CONSTRAINT fk_res_att_res
FOREIGN KEY (resourceId)
REFERENCES resource(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_resource_attribute ON resource_attribute(id)
;
CREATE INDEX fk_res_att_res ON resource_attribute(resourceId)
;
ALTER TABLE resource_ref
ADD CONSTRAINT fk_resref_pmv
FOREIGN KEY (resourceId)
REFERENCES resource(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE resource_ref
ADD CONSTRAINT fk_resref_node
FOREIGN KEY (nodeId)
REFERENCES node(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_resource_ref ON resource_ref(id)
;
CREATE INDEX fk_resref_pmv ON resource_ref(resourceId)
;
CREATE INDEX fk_resref_node ON resource_ref(nodeId)
;
ALTER TABLE resource_ref_attribute
ADD CONSTRAINT fk_resref_att
FOREIGN KEY (resourceRefId)
REFERENCES resource_ref(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_resource_ref_attribute ON resource_ref_attribute(id)
;
CREATE INDEX fk_resref_att ON resource_ref_attribute(resourceRefId)
;
ALTER TABLE resource_specialisations
ADD CONSTRAINT fk_special
FOREIGN KEY (specialisationId)
REFERENCES resource(id) ON DELETE CASCADE ON UPDATE CASCADE
;
ALTER TABLE resource_specialisations
ADD CONSTRAINT fk_resource
FOREIGN KEY (resourceId)
REFERENCES resource(id) ON DELETE CASCADE ON UPDATE CASCADE
;
CREATE UNIQUE INDEX id_primary_resource_specialisations ON resource_specialisations
(
  resourceId,
  specialisationId
)
;
CREATE INDEX fk_special ON resource_specialisations(specialisationId)
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
  SELECT process.id AS processId, process.id AS value
  FROM process
  UNION 
  SELECT process.id AS processId, process.name AS word
  FROM process
  UNION 
  SELECT process.id AS processId, process.domain AS domain
  FROM process
  UNION 
  SELECT process.id AS processId, native_type.nat_type AS original_type
  FROM process JOIN native_type ON (process.nativeTypeId = native_type.id)
  UNION 
  SELECT process.id AS processId, user.first_name AS firstname
  FROM process JOIN user ON (process.owner = user.username)
  UNION 
  SELECT process.id AS processId, user.last_name AS lastname
  FROM process JOIN user ON (process.owner = user.username)
  UNION 
  SELECT process_branch.processId AS processId, process_branch.branch_name AS branch_name
  FROM process_branch;

