LOCK TABLES `native_type` WRITE;
/*!40000 ALTER TABLE `native_type` DISABLE KEYS */;
INSERT INTO `native_type` VALUES ('EPML 2.0','epml');
INSERT INTO `native_type` VALUES ('XPDL 2.1','xpdl');
INSERT INTO `native_type` VALUES ('PNML 1.3.2', 'pnml');
/*!40000 ALTER TABLE `native_type` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('Alshareef','Abdul','aah.shareef@gmail.com','abdul','');
INSERT INTO `user` VALUES (NULL,'Anne',NULL,'anne','');
INSERT INTO `user` VALUES ('Ter Hofstede','Arthur','arthur@yawlfoundation.org','arthur','');
INSERT INTO `user` VALUES (NULL,'Barbara',NULL,'barbara','');
INSERT INTO `user` VALUES ('Ekanayake','Chathura',NULL,'chathura',NULL);
INSERT INTO `user` VALUES ('Alrashed','Fahad','fahadakr@gmail.com','fahad',NULL);
INSERT INTO `user` VALUES ('Fauvet','Marie','marie-christine.fauvet@qut.edu.au','fauvet','');
INSERT INTO `user` VALUES (NULL,'guest',NULL,'guest','');
INSERT INTO `user` VALUES (NULL,'Hajo',NULL,'hajo','');
INSERT INTO `user` VALUES (NULL,'icsoc 2010',NULL,'icsoc','Macri');
INSERT INTO `user` VALUES ('James','Cameron','cam.james@gmail.com','james','');
INSERT INTO `user` VALUES ('La Rosa','Marcello','m.larosa@qut.edu.au','larosa','');
INSERT INTO `user` VALUES ('Garcia-Banuelos','Luciano','lgbanuelos@gmail.com','luciano','');
INSERT INTO `user` VALUES ('','Mehrad',NULL,'mehrad','');
INSERT INTO `user` VALUES (NULL,'Public',NULL,'public','');
INSERT INTO `user` VALUES (NULL,'Reina',NULL,'reina','');
INSERT INTO `user` VALUES ('Dijkman','Remco','R.M.Dijkman@tue.nl','remco','');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `search_history` WRITE;
/*!40000 ALTER TABLE `search_history` DISABLE KEYS */;
INSERT INTO `search_history` VALUES ('larosa','airport',13);
INSERT INTO `search_history` VALUES ('mehrad','gold coast',11);
INSERT INTO `search_history` VALUES ('mehrad','goldcoast',12);
/*!40000 ALTER TABLE `search_history` ENABLE KEYS */;
UNLOCK TABLES;
