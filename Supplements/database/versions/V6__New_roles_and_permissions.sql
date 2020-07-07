LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (4,'e0ae8c02-bfe0-11ea-992a-17a70e2c7d0f','ROLE_ANALYST','Allowed access to all tools except predictive monitoring training');
INSERT INTO `role` VALUES (5,'43fbd3c8-bfe1-11ea-9694-abeaa8ed1c0f','ROLE_OBSERVER','Allowed access to all tools in read-only mode');
INSERT INTO `role` VALUES (6,'4ff46cbc-bfe1-11ea-abec-aba9c1ea1178','ROLE_DESIGNER','Allowed access to the BPMN editor');
INSERT INTO `role` VALUES (7,'596fcfd4-bfe1-11ea-8564-4f9db22c941e','ROLE_DATA_SCIENTIST','Allowed access to all tools including predictive monitoring training');
INSERT INTO `role` VALUES (8,'64e734a6-bfe1-11ea-b4cf-2bed596c2920','ROLE_OPERATIONS','Allowed access to the predictive monitoring dashboard (runtime)');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;
