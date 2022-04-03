INSERT INTO `native_type` VALUES (1,'EPML 2.0','epml');
INSERT INTO `native_type` VALUES (2,'XPDL 2.2','xpdl');
INSERT INTO `native_type` VALUES (3,'PNML 1.3.2', 'pnml');
INSERT INTO `native_type` VALUES (4,'YAWL 2.2', 'yawl');
INSERT INTO `native_type` VALUES (5,'BPMN 2.0', 'bpmn');
INSERT INTO `native_type` VALUES (6,'AML fragment', 'aml');


INSERT INTO `role` VALUES (1,'80da507e-cdd7-40f4-a9f8-b2d2edb12856','ROLE_ADMIN','Ultimate power to administer other users');
INSERT INTO `role` VALUES (2,'0ecd70b4-a204-41cd-a246-e3fcef88f6fe','ROLE_USER','Allowed to log in');
INSERT INTO `role` VALUES (3,'72503ce0-d7cd-47b3-a33c-1b741d7599a1','ROLE_MANAGER','Allowed to administer folders, models and event logs');
INSERT INTO `role` VALUES (4,'e0ae8c02-bfe0-11ea-992a-17a70e2c7d0f','ROLE_ANALYST','Allowed access to all tools except predictive monitoring training');
INSERT INTO `role` VALUES (5,'43fbd3c8-bfe1-11ea-9694-abeaa8ed1c0f','ROLE_OBSERVER','Allowed access to all tools in read-only mode');
INSERT INTO `role` VALUES (6,'4ff46cbc-bfe1-11ea-abec-aba9c1ea1178','ROLE_DESIGNER','Allowed access to the BPMN editor');
INSERT INTO `role` VALUES (7,'596fcfd4-bfe1-11ea-8564-4f9db22c941e','ROLE_DATA_SCIENTIST','Allowed access to all tools including predictive monitoring training');
INSERT INTO `role` VALUES (8,'64e734a6-bfe1-11ea-b4cf-2bed596c2920','ROLE_OPERATIONS','Allowed access to the predictive monitoring dashboard (runtime)');


INSERT INTO `user` VALUES (8,'ad1f7b60-1143-4399-b331-b887585a0f30','admin',   '2012-05-28 16:51:05','Test',    'User',        NULL,8,NULL,NULL,NULL,NULL,NULL);

INSERT INTO `membership` VALUES (8,8,'5f4dcc3b5aa765d61d8327deb882cf99','username','','admin','Test question','test',1,0,'2012-06-16 14:10:14',0,0);

INSERT INTO `group` VALUES (8,'uuid8-admin',   'admin',   'USER');
INSERT INTO `group` VALUES (9,'uuid9-public',  'public',  'PUBLIC');

INSERT INTO `user_group` VALUES (8,8),(8,9);

INSERT INTO `user_role` VALUES (1,8);
INSERT INTO `user_role` VALUES (2,8);

INSERT INTO `permission` VALUES (1,'dff60714-1d61-4544-8884-0d8b852ba41e','View users','View other user data');
INSERT INTO `permission` VALUES (2,'2e884153-feb2-4842-b291-769370c86e44','Edit users','Modify other user data');
INSERT INTO `permission` VALUES (3,'d9ade57c-14c7-4e43-87e5-6a9127380b1b','Edit groups','Create, populate and delete groups');
INSERT INTO `permission` VALUES (4,'ea31a607-212f-447e-8c45-78f1e59b1dde','Edit roles','Modify other user roles');

INSERT INTO `role_permission` VALUES (1,1);
INSERT INTO `role_permission` VALUES (1,2);
INSERT INTO `role_permission` VALUES (1,3);
INSERT INTO `role_permission` VALUES (1,4);
INSERT INTO `role_permission` VALUES (2,1);
INSERT INTO `role_permission` VALUES (3,3);
