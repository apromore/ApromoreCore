-- MySQL dump 10.13  Distrib 5.7.11, for Win64 (x86_64)
--
-- Host: localhost    Database: pql
-- ------------------------------------------------------
-- Server version	5.7.11-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `jbpt_labels`
--

DROP TABLE IF EXISTS `jbpt_labels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jbpt_labels` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `label` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `label` (`label`(5))
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jbpt_labels`
--

LOCK TABLES `jbpt_labels` WRITE;
/*!40000 ALTER TABLE `jbpt_labels` DISABLE KEYS */;
INSERT INTO `jbpt_labels` VALUES (1,'assign asset to property  if required'),(2,'assets to be shut down'),(3,'depreciation should be posted regularly'),(4,'depreciation terms should be changed'),(5,'asset shutdown'),(6,'retirements to be entered'),(7,'creation of group asset'),(8,'group asset to be created'),(9,'asset master record does not exist'),(10,'asset shutdown posted'),(11,'acquisitions to be entered'),(12,'asset master record change'),(13,'fixed asset created'),(14,'fixed asset is changed'),(15,'leased asset was bought'),(16,'asset master record is incomplete'),(17,'fixed asset was bought w/o purchase order'),(18,'worklist was created'),(19,'an asset master record should be changed'),(20,'revaluation should be carr out f first time'),(21,'new master record is necessary'),(22,'fixed asset was given'),(23,'mass change'),(24,'claim for support exists'),(25,'cost center plan was changed'),(26,'mass changes were made'),(27,'revaluation is necessary due to post capi  talization'),(28,'creation of master record for tangible assets'),(29,'several asset master records should be changed'),(30,'fixed asset was found'),(31,'post capitalization to be posted'),(32,'assign asset to building  if required'),(33,'fixed asset was bought w\\/o purchase order');
/*!40000 ALTER TABLE `jbpt_labels` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `jbpt_labels_before_del_tr` BEFORE DELETE ON `jbpt_labels`
  FOR EACH ROW
BEGIN
  DELETE FROM pql_tasks_sim WHERE pql_tasks_sim.label_id = OLD.id;
  DELETE FROM pql_tasks WHERE pql_tasks.label_id = OLD.id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `jbpt_petri_flow`
--

DROP TABLE IF EXISTS `jbpt_petri_flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jbpt_petri_flow` (
  `source` int(11) unsigned NOT NULL,
  `target` int(11) unsigned NOT NULL,
  `name` text,
  `description` text,
  PRIMARY KEY (`source`,`target`),
  KEY `source` (`source`),
  KEY `target` (`target`),
  CONSTRAINT `jbpt_flow_fk_source` FOREIGN KEY (`source`) REFERENCES `jbpt_petri_nodes` (`id`),
  CONSTRAINT `jbpt_flow_fk_target` FOREIGN KEY (`target`) REFERENCES `jbpt_petri_nodes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jbpt_petri_flow`
--

LOCK TABLES `jbpt_petri_flow` WRITE;
/*!40000 ALTER TABLE `jbpt_petri_flow` DISABLE KEYS */;
INSERT INTO `jbpt_petri_flow` VALUES (1,94,'',''),(2,138,'',''),(3,142,'',''),(4,110,'',''),(5,111,'',''),(6,152,'',''),(7,104,'',''),(8,105,'',''),(9,114,'',''),(10,157,'',''),(11,112,'',''),(12,78,'',''),(12,81,'',''),(12,90,'',''),(12,97,'',''),(12,115,'',''),(12,158,'',''),(13,121,'',''),(14,76,'',''),(14,133,'',''),(15,153,'',''),(16,128,'',''),(17,122,'',''),(18,146,'',''),(19,131,'',''),(20,101,'',''),(21,110,'',''),(22,123,'',''),(23,88,'',''),(24,156,'',''),(25,116,'',''),(26,99,'',''),(27,107,'',''),(28,140,'',''),(29,91,'',''),(30,106,'',''),(31,77,'',''),(32,92,'',''),(33,135,'',''),(34,95,'',''),(34,130,'',''),(35,148,'',''),(36,124,'',''),(37,125,'',''),(38,117,'',''),(39,119,'',''),(40,87,'',''),(41,147,'',''),(42,75,'',''),(43,124,'',''),(44,96,'',''),(45,86,'',''),(45,139,'',''),(46,126,'',''),(47,100,'',''),(48,89,'',''),(49,82,'',''),(51,102,'',''),(52,153,'',''),(53,84,'',''),(54,108,'',''),(55,85,'',''),(55,118,'',''),(55,120,'',''),(55,132,'',''),(55,137,'',''),(55,141,'',''),(55,149,'',''),(55,150,'',''),(55,155,'',''),(56,80,'',''),(56,98,'',''),(57,154,'',''),(58,143,'',''),(59,134,'',''),(60,113,'',''),(61,127,'',''),(62,151,'',''),(63,79,'',''),(64,160,'',''),(65,144,'',''),(66,136,'',''),(67,83,'',''),(68,159,'',''),(69,153,'',''),(70,145,'',''),(71,129,'',''),(72,103,'',''),(73,109,'',''),(74,93,'',''),(75,24,'',''),(76,42,'',''),(77,74,'',''),(78,61,'',''),(79,9,'',''),(79,14,'',''),(80,12,'',''),(81,39,'',''),(82,72,'',''),(83,73,'',''),(84,11,'',''),(85,27,'',''),(86,13,'',''),(87,11,'',''),(88,73,'',''),(89,15,'',''),(90,8,'',''),(91,32,'',''),(92,4,'',''),(93,30,'',''),(94,25,'',''),(95,37,'',''),(96,35,'',''),(97,17,'',''),(98,51,'',''),(99,35,'',''),(100,45,'',''),(101,56,'',''),(102,47,'',''),(103,73,'',''),(104,20,'',''),(105,18,'',''),(106,58,'',''),(107,67,'',''),(108,66,'',''),(109,21,'',''),(110,54,'',''),(111,35,'',''),(112,38,'',''),(113,2,'',''),(113,22,'',''),(113,48,'',''),(113,62,'',''),(114,65,'',''),(115,6,'',''),(116,73,'',''),(117,68,'',''),(118,33,'',''),(119,71,'',''),(120,31,'',''),(121,19,'',''),(122,28,'',''),(123,36,'',''),(124,73,'',''),(125,40,'',''),(126,70,'',''),(127,44,'',''),(128,5,'',''),(129,35,'',''),(130,41,'',''),(131,54,'',''),(132,34,'',''),(133,64,'',''),(134,3,'',''),(135,10,'',''),(136,50,'',''),(137,60,'',''),(138,52,'',''),(139,63,'',''),(140,35,'',''),(141,57,'',''),(142,43,'',''),(143,73,'',''),(144,55,'',''),(145,73,'',''),(146,35,'',''),(147,53,'',''),(148,45,'',''),(149,49,'',''),(150,46,'',''),(151,69,'',''),(152,26,'',''),(153,59,'',''),(154,23,'',''),(155,1,'',''),(156,32,'',''),(157,73,'',''),(158,16,'',''),(159,73,'',''),(160,29,'','');
/*!40000 ALTER TABLE `jbpt_petri_flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jbpt_petri_markings`
--

DROP TABLE IF EXISTS `jbpt_petri_markings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jbpt_petri_markings` (
  `place_id` int(11) unsigned NOT NULL,
  `tokens` int(11) unsigned NOT NULL,
  PRIMARY KEY (`place_id`),
  CONSTRAINT `jbpt_petri_markings_fk` FOREIGN KEY (`place_id`) REFERENCES `jbpt_petri_nodes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jbpt_petri_markings`
--

LOCK TABLES `jbpt_petri_markings` WRITE;
/*!40000 ALTER TABLE `jbpt_petri_markings` DISABLE KEYS */;
/*!40000 ALTER TABLE `jbpt_petri_markings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jbpt_petri_nets`
--

DROP TABLE IF EXISTS `jbpt_petri_nets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jbpt_petri_nets` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) NOT NULL,
  `name` text,
  `description` text,
  `external_id` text,
  `pnml_content` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`(20)),
  UNIQUE KEY `external_id` (`external_id`(20))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jbpt_petri_nets`
--

LOCK TABLES `jbpt_petri_nets` WRITE;
/*!40000 ALTER TABLE `jbpt_petri_nets` DISABLE KEYS */;
INSERT INTO `jbpt_petri_nets` VALUES (1,'082d8424-e6a3-4e7d-a1f5-48592ed25ffe','','','1An_kazo.pnml','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><pnml xmlns=\"http://www.pnml.org/version-2009/grammar/pnml\"><net id=\"570fd593-1b0e-4d8e-87c8-6323324fa007\" type=\"http://www.pnml.org/version-2009/grammar/ptnet\"><name><text/></name><page id=\"page0\"><place id=\"81f71686-56ca-4a7a-a569-09bb072005e3\"><name><text>p70</text></name></place><place id=\"31d53bed-63b8-4584-86bb-d7bfc3ad6b2a\"><name><text>p42</text></name></place><place id=\"e6129bb4-936f-4e33-981a-1e34fb34cdb3\"><name><text>p16</text></name></place><place id=\"b01a2183-b9ef-408c-b3bf-0ba75c9c9fc9\"><name><text>p48</text></name></place><place id=\"2162d892-6215-491e-954e-0d29d043a652\"><name><text>p30</text></name></place><place id=\"4a9c0d39-ce98-4dba-b712-70201ecbc590\"><name><text>p2</text></name></place><place id=\"919de476-1796-4a7b-8f87-84b21f9b7005\"><name><text>p33</text></name></place><place id=\"caac5932-41fe-4e17-ba53-85eda5b4a4e6\"><name><text>p62</text></name></place><place id=\"7af861a4-17fb-441f-a7d5-5672d060b0c5\"><name><text>p54</text></name></place><place id=\"0c5299ca-ce4b-43de-8c2d-3732138b7394\"><name><text>p73</text></name></place><place id=\"ab11ce4d-9df3-4cc9-8401-f12e4a3b333f\"><name><text>p41</text></name></place><place id=\"aacb7235-c484-48d8-80a3-3d579ba77c01\"><name><text>p11</text></name></place><place id=\"d5dee855-7524-4095-b065-4f96e379aa46\"><name><text>p55</text></name></place><place id=\"dbbbb762-64d8-4d44-be8a-2d7772b4d134\"><name><text>p28</text></name></place><place id=\"ca9926cd-7a8a-4ed9-b70f-71557be2f344\"><name><text>p65</text></name></place><place id=\"a6d83837-9256-4656-9df3-79be5cd170d5\"><name><text>p56</text></name></place><place id=\"46df1f8c-321b-4aec-9d00-09a67a8e29cf\"><name><text>p1</text></name></place><place id=\"583cb6e3-a574-4775-8781-dc62d1e739b9\"><name><text>p66</text></name></place><place id=\"65479e5c-968d-4814-a58e-644623b966f5\"><name><text>p6</text></name></place><place id=\"369f4d9a-6807-42ac-ab80-67c2fbc71188\"><name><text>p14</text></name></place><place id=\"769bdb06-6e57-4aa8-a4bb-fa01dfa67f76\"><name><text>p17</text></name></place><place id=\"02e49a40-d7e1-4c6e-a703-a34604543136\"><name><text>p24</text></name></place><place id=\"50fb3081-a79f-4309-873f-dc0f0213b91d\"><name><text>p59</text></name></place><place id=\"60e0b174-d585-4faa-83b9-e1ea5e7098ed\"><name><text>p57</text></name></place><place id=\"61ed8d25-c067-4f0a-a1ce-54818e12e8e8\"><name><text>p43</text></name></place><place id=\"a3dc698b-f458-4033-a5e1-6124d831afe4\"><name><text>p25</text></name></place><place id=\"f2f53ffb-20b2-4079-b1b0-05ee58d91122\"><name><text>p9</text></name></place><place id=\"f4b8cea0-0753-45cb-873a-73330154bdab\"><name><text>p69</text></name></place><place id=\"cfe567f7-bf65-4afd-a5ef-e99ce9b6f931\"><name><text>p72</text></name></place><place id=\"b6ddeffe-5fda-4cc1-9449-9f655fdf44bb\"><name><text>p47</text></name></place><place id=\"9380bf0e-2ac4-4e79-b000-e525f3d8191f\"><name><text>p8</text></name></place><place id=\"54ca1ed6-b0bc-42a6-becf-2089db031b33\"><name><text>p26</text></name></place><place id=\"eb6bf3d0-ff08-4c26-b3b8-8a56fc90856e\"><name><text>p44</text></name></place><place id=\"471dcd81-b1b7-48f7-9790-c5732618accf\"><name><text>p7</text></name></place><place id=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\"><name><text>p29</text></name></place><place id=\"48fa4b72-8884-439f-8371-313a942a3b05\"><name><text>p67</text></name></place><place id=\"2780b794-ddc6-453b-a413-a474cf3ee2f1\"><name><text>p37</text></name></place><place id=\"fdef60ba-d0be-43cf-a8da-563d73ddf77b\"><name><text>p63</text></name></place><place id=\"909a382e-a2e5-4451-bde3-c6b684afdd8c\"><name><text>p12</text></name></place><place id=\"d2aa7cd4-91cd-45a6-a9a4-d77d51c47cf3\"><name><text>p22</text></name></place><place id=\"4d257a12-fa78-4b50-a773-4cc2d18ceed5\"><name><text>p18</text></name></place><place id=\"842c5a9a-0dce-4941-9568-c6bad9a8892b\"><name><text>p21</text></name></place><place id=\"047dd1bf-6981-4fd7-9b09-40f7745f38cc\"><name><text>p3</text></name></place><place id=\"58cfb1a8-08a0-4e4c-a89c-cc0f7fb40224\"><name><text>p68</text></name></place><place id=\"ed426528-fe4d-4baf-8680-3811d2781ff9\"><name><text>p19</text></name></place><place id=\"f5347f5e-1100-44b4-9427-ec5958a70d65\"><name><text>p64</text></name></place><place id=\"f10f7326-1014-4583-bed2-f3f0b4752382\"><name><text>p13</text></name></place><place id=\"a4bad625-8c2d-4817-b991-bf611094ab1b\"><name><text>p5</text></name></place><place id=\"8638efad-0dce-4764-99e7-075f760f15c6\"><name><text>p38</text></name></place><place id=\"a36deaf0-aa3d-418a-abe3-1eab5063750b\"><name><text>p10</text></name></place><place id=\"63a6916e-1f60-4db7-bfa4-be6976c4df4a\"><name><text>p23</text></name></place><place id=\"383f5a40-0735-43a3-9bf3-574b1cb97baa\"><name><text>p58</text></name></place><place id=\"8eb7b087-9409-4f22-96a1-6a168eae0451\"><name><text>p34</text></name></place><place id=\"3ce65202-2483-42cb-bcbd-0b336dadad95\"><name><text>p20</text></name></place><place id=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\"><name><text>p74</text></name></place><place id=\"d5e12359-9767-4f05-a30f-5771f1696db4\"><name><text>p45</text></name></place><place id=\"1960718a-94c0-498c-b6a9-457df6ac3955\"><name><text>p4</text></name></place><place id=\"ded883b4-bdee-4433-9ba6-312b93fea943\"><name><text>p31</text></name></place><place id=\"cf0d809f-8c78-45e9-8aec-a73e750067a8\"><name><text>p32</text></name></place><place id=\"d0b3c45b-2215-4b43-82af-8f8e66554b81\"><name><text>p27</text></name></place><place id=\"cf0033b4-21c5-4c07-89ab-f4ad60278642\"><name><text>p50</text></name></place><place id=\"d23defa0-438b-4083-a5df-e12fb1f681f7\"><name><text>p39</text></name></place><place id=\"456422e2-d029-4538-8ebb-4b125ad259c4\"><name><text>p51</text></name></place><place id=\"fc7267d2-df24-4ab2-84ed-f76c53a1295f\"><name><text>p53</text></name></place><place id=\"c0400c82-05de-475b-8bfb-642a8842855c\"><name><text>p49</text></name></place><place id=\"fbba774e-3764-43ab-b240-82f1a685c05e\"><name><text>p71</text></name></place><place id=\"90eff012-be2c-4400-8eac-c6152ee11b78\"><name><text>p36</text></name></place><place id=\"a571f921-7dfb-4ce8-9b0a-461c3a2e1527\"><name><text>p46</text></name></place><place id=\"0e346604-1e56-4b45-86ad-7c6f4e18cae6\"><name><text>p40</text></name></place><place id=\"d5f92aa5-c5d9-492f-9a90-4db14d91f4ac\"><name><text>p35</text></name></place><place id=\"a001db33-4aae-472d-9058-a1864297dae7\"><name><text>p15</text></name></place><place id=\"77202fb2-67e2-4129-b7ec-16a73ca8efaa\"><name><text>p52</text></name></place><place id=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"><name><text>p60</text></name></place><place id=\"3d455578-8ee0-4b3c-a3f7-b2fd0624269f\"><name><text>p61</text></name></place><transition id=\"4300640c-329f-4fe4-9d97-efad7add169d\"><name><text>assign asset to property  if required</text></name></transition><transition id=\"5d013664-e2c6-4ed5-b0c8-defac1a91134\"/><transition id=\"fac8835d-9a6c-4745-b423-289f468b5311\"><name><text>assets to be shut down</text></name></transition><transition id=\"af595107-a9e3-453e-8004-388600dbdc4a\"/><transition id=\"73c5d1b9-3faf-44a7-9db0-433133ef8b48\"/><transition id=\"68340ee5-f95d-419d-a855-d1cde8f06a05\"/><transition id=\"a78f3b36-e91d-4151-a046-d715cf178de2\"/><transition id=\"609d83f5-790f-4148-bde7-09f8ec32be69\"><name><text>depreciation should be posted regularly</text></name></transition><transition id=\"d8bc5e04-771d-457c-9e89-f201b2e8f076\"/><transition id=\"4a2ea91f-1b73-4edd-a79f-0b44537255f1\"/><transition id=\"f6a5b57e-2da1-47d7-9c9d-d5d16854cd92\"/><transition id=\"a21acbff-fe77-4d30-8b6a-62291b0a18e0\"/><transition id=\"2c3ce0cd-32c8-4627-b1f5-c3d62bb064b3\"/><transition id=\"8628dfce-b75c-4791-a326-831ecbc7d960\"/><transition id=\"be9a9d92-1d2c-47e8-9711-79d76c80c9ac\"><name><text>depreciation terms should be changed</text></name></transition><transition id=\"dacc2f3f-87e6-4c2b-9c7e-2ab93f59d9c0\"/><transition id=\"2b435378-f2f4-4bf1-8949-eca9a89b45e0\"/><transition id=\"eb444ae2-27ca-40ae-a377-cdf04a4d8bc2\"/><transition id=\"eca8c4ac-2e50-4b70-b6eb-b5df56310146\"><name><text>asset shutdown</text></name></transition><transition id=\"d63d69fe-ec08-4ad3-afa5-c3fd40fe0280\"><name><text>retirements to be entered</text></name></transition><transition id=\"ec2e1beb-cabf-4ae3-b7de-40f612df107a\"/><transition id=\"1ea773b8-992e-4585-9c7d-ef740a4f5484\"/><transition id=\"01691390-3901-467d-966e-9e261f211865\"/><transition id=\"903e30c7-d1d0-4a6d-a70a-2bbf1383a310\"/><transition id=\"3ff10844-faf3-4693-98b0-61a6afe5d228\"/><transition id=\"13d209e9-cea1-48c2-ae59-c05302560d0a\"><name><text>creation of group asset</text></name></transition><transition id=\"99631d87-3e38-4f4a-9644-861015ec2495\"/><transition id=\"1dec93f1-3c1f-4048-84f0-82703a1b9135\"><name><text>group asset to be created</text></name></transition><transition id=\"1e9f5cdd-2a7e-426d-85d8-9dc6b6afe5e6\"/><transition id=\"df6f83c5-2df7-4aca-b918-ed9a0a92bb78\"/><transition id=\"3a04248f-a715-4411-81d8-20e967c315c6\"><name><text>asset master record does not exist</text></name></transition><transition id=\"c0c325e0-d6f4-4ff4-87e5-d8bafe9100ca\"><name><text>asset shutdown posted</text></name></transition><transition id=\"f6b11751-aab1-4256-b19c-7fd6509a3744\"><name><text>acquisitions to be entered</text></name></transition><transition id=\"13a3035a-2ef3-4320-9ef5-622fe3ce8226\"/><transition id=\"2b11f68f-b524-404b-9e2a-a4a677125f1b\"/><transition id=\"e21fb736-7a68-4980-a576-208d27377086\"/><transition id=\"88788edf-6081-4176-80da-8306fabb6940\"/><transition id=\"d8c0d90d-732f-40d0-b888-5f35759a5c5b\"><name><text>asset master record change</text></name></transition><transition id=\"61c40946-c18d-4500-abe8-8cdb382965ec\"/><transition id=\"f56a65de-f0ad-4a1e-bcb5-bb589f1f15d3\"><name><text>fixed asset created</text></name></transition><transition id=\"9242f595-c83d-4b39-82f0-4392054f4690\"/><transition id=\"ef647550-ef00-4eaf-9cbf-c24c41864d99\"/><transition id=\"5e8062cf-6e07-4306-bddf-1d5aeabbcfcf\"><name><text>fixed asset is changed</text></name></transition><transition id=\"36e60dce-dcb3-4d3b-b008-a4b2d49494df\"/><transition id=\"9c6b1056-ee28-4ff4-a325-83cb46fed56a\"><name><text>leased asset was bought</text></name></transition><transition id=\"b591de18-3723-426f-8c6a-b38eb6018bfa\"/><transition id=\"c13179b8-8a3e-4ced-bb3d-77e1a9aee127\"><name><text>asset master record is incomplete</text></name></transition><transition id=\"b0e79469-29e6-4ae8-8c6f-7333a535d65a\"><name><text>fixed asset was bought w/o purchase order</text></name></transition><transition id=\"a6a77119-62bc-4c8a-a7ee-683121ffe69a\"><name><text>worklist was created</text></name></transition><transition id=\"fc006fdf-f7bb-4e83-b69c-cad4e8a7e9ff\"/><transition id=\"0e8022c1-6c09-4129-973a-2fad5e642314\"><name><text>an asset master record should be changed</text></name></transition><transition id=\"68e64763-a6f5-4bd2-afb2-7f19cde7bebe\"><name><text>revaluation should be carr out f first time</text></name></transition><transition id=\"f6eadeb5-11da-40f1-9372-f549801beab4\"><name><text>new master record is necessary</text></name></transition><transition id=\"0ba695fd-6e65-45b5-91e9-78d8b2801a09\"><name><text>fixed asset was given</text></name></transition><transition id=\"2ad4ac3d-4da1-4ebd-bfdf-d7e02e21c1ca\"/><transition id=\"577486c2-bfdb-480d-8014-fae477371e7e\"/><transition id=\"af88e0a2-e53e-4cb8-a172-a628b08465d0\"/><transition id=\"49be4485-cbd8-4cb5-9054-dda79e3443b7\"/><transition id=\"6dc83cd4-4175-427c-811a-391a3f871719\"/><transition id=\"002c9965-97e1-4e46-b4b2-1a732b96b357\"><name><text>mass change</text></name></transition><transition id=\"66eef4ac-d62a-47fe-a7c7-552830dd113a\"><name><text>claim for support exists</text></name></transition><transition id=\"278f94fa-56f0-4191-b0d4-13ccda66ddbb\"/><transition id=\"d91205b8-5d43-486c-98bc-a9c003eb699b\"/><transition id=\"79d5a257-e167-44bf-9cc3-a45351b6c4bf\"><name><text>cost center plan was changed</text></name></transition><transition id=\"81fcdf8e-5595-4f16-8718-d8a848c5a340\"/><transition id=\"c9efc280-56d1-47b2-9213-d7bb56a2aa41\"/><transition id=\"1bd08b9c-53b7-4383-8f0c-28b3c2fded5a\"/><transition id=\"25b259c0-c776-448b-bdca-3df4d9a98347\"><name><text>mass changes were made</text></name></transition><transition id=\"b63d1898-65ba-426e-adca-bb1cc2fb9fae\"/><transition id=\"29702e9e-9c6a-47bd-b1bf-39038b24a442\"/><transition id=\"b6e00576-d34a-4a48-bc98-e25a05b23fca\"/><transition id=\"3f0a06d6-7783-44e0-a6a0-ba8283845c04\"/><transition id=\"2d8830f9-c97b-480d-a3f7-e5b2d8b61f0b\"><name><text>revaluation is necessary due to post capi  talization</text></name></transition><transition id=\"397b0d19-0593-4824-ac0b-7787ac293f90\"><name><text>creation of master record for tangible assets</text></name></transition><transition id=\"47560cdd-04f9-4fe2-ba17-1cfb4a1533f7\"/><transition id=\"ad913315-1138-467a-be2c-ba6093dcb73e\"/><transition id=\"2e073c95-c028-4d2b-b83d-5a5cc3320942\"><name><text>several asset master records should be changed</text></name></transition><transition id=\"7270b705-ae7c-4031-902b-56e8ffadeba7\"><name><text>fixed asset was found</text></name></transition><transition id=\"f0fba2bf-247e-4e06-8161-c2e5e22b99f9\"/><transition id=\"0a6bd54b-c1c2-42ae-b7f0-a17e2d206234\"><name><text>post capitalization to be posted</text></name></transition><transition id=\"cf7237ae-8144-4bca-a70e-65b5b6f2b309\"/><transition id=\"2654070d-78bf-46a5-85c2-1a9dcfc4e10b\"/><transition id=\"ab5290ff-b971-41ed-8a34-70f1739840a6\"/><transition id=\"b53e9eef-3d47-417f-975c-8092e7e2a3ec\"/><transition id=\"44a7da67-f632-4923-bf73-873cce80f03e\"/><transition id=\"7b10a58b-ac4e-419b-ad73-e1a24d4397d6\"><name><text>assign asset to building  if required</text></name></transition><arc id=\"75b00ded-17b3-472c-9f2b-f3df82b18a2d\" source=\"61c40946-c18d-4500-abe8-8cdb382965ec\" target=\"02e49a40-d7e1-4c6e-a703-a34604543136\"/><arc id=\"6522eaa2-9da0-441e-906b-b0821ddcefd0\" source=\"73c5d1b9-3faf-44a7-9db0-433133ef8b48\" target=\"dbbbb762-64d8-4d44-be8a-2d7772b4d134\"/><arc id=\"cc366ac0-f3fb-4087-bea9-d002f15244ca\" source=\"fac8835d-9a6c-4745-b423-289f468b5311\" target=\"3d455578-8ee0-4b3c-a3f7-b2fd0624269f\"/><arc id=\"84741a26-32d3-4075-ac5c-59b9dff61bf8\" source=\"aacb7235-c484-48d8-80a3-3d579ba77c01\" target=\"a78f3b36-e91d-4151-a046-d715cf178de2\"/><arc id=\"cea716c1-0344-42ec-a6ff-56ae771f7b0c\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"36e60dce-dcb3-4d3b-b008-a4b2d49494df\"/><arc id=\"92c8c2d7-7c67-4d52-a0dd-68b4c1e962ca\" source=\"aacb7235-c484-48d8-80a3-3d579ba77c01\" target=\"af595107-a9e3-453e-8004-388600dbdc4a\"/><arc id=\"b0aa859a-5cd5-419e-99c1-88d82e46b8fa\" source=\"383f5a40-0735-43a3-9bf3-574b1cb97baa\" target=\"f0fba2bf-247e-4e06-8161-c2e5e22b99f9\"/><arc id=\"81184945-e0e2-4e85-906f-cfd448d998ae\" source=\"4a9c0d39-ce98-4dba-b712-70201ecbc590\" target=\"7270b705-ae7c-4031-902b-56e8ffadeba7\"/><arc id=\"984967b4-699a-4b3e-ad45-aa14fac91122\" source=\"2b11f68f-b524-404b-9e2a-a4a677125f1b\" target=\"769bdb06-6e57-4aa8-a4bb-fa01dfa67f76\"/><arc id=\"4727e0dc-2457-4f28-bf24-554ae18f5f5b\" source=\"dbbbb762-64d8-4d44-be8a-2d7772b4d134\" target=\"6dc83cd4-4175-427c-811a-391a3f871719\"/><arc id=\"e64228a3-dfba-446f-9a38-9f0c7917f07e\" source=\"eb6bf3d0-ff08-4c26-b3b8-8a56fc90856e\" target=\"66eef4ac-d62a-47fe-a7c7-552830dd113a\"/><arc id=\"fc913554-8b52-48ed-8916-97a0ac390bdc\" source=\"1bd08b9c-53b7-4383-8f0c-28b3c2fded5a\" target=\"1960718a-94c0-498c-b6a9-457df6ac3955\"/><arc id=\"fb7ecfad-9903-419b-be1b-d9576d8a90e0\" source=\"01691390-3901-467d-966e-9e261f211865\" target=\"46df1f8c-321b-4aec-9d00-09a67a8e29cf\"/><arc id=\"ee531411-bada-4fa0-a914-361610e5f6b8\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"1bd08b9c-53b7-4383-8f0c-28b3c2fded5a\"/><arc id=\"f510ec78-7439-4f43-9130-9fcf819a0ec1\" source=\"b53e9eef-3d47-417f-975c-8092e7e2a3ec\" target=\"a6d83837-9256-4656-9df3-79be5cd170d5\"/><arc id=\"109389d4-4e46-40c9-b144-db0454e7baa7\" source=\"68e64763-a6f5-4bd2-afb2-7f19cde7bebe\" target=\"d5f92aa5-c5d9-492f-9a90-4db14d91f4ac\"/><arc id=\"ec6bbb50-45f0-49c3-a969-3370466f20d2\" source=\"61c40946-c18d-4500-abe8-8cdb382965ec\" target=\"31d53bed-63b8-4584-86bb-d7bfc3ad6b2a\"/><arc id=\"7cc9a5db-462d-4372-b9d3-d6e08a524d9b\" source=\"c13179b8-8a3e-4ced-bb3d-77e1a9aee127\" target=\"65479e5c-968d-4814-a58e-644623b966f5\"/><arc id=\"fa2e5231-541c-4407-b629-011d5593748c\" source=\"77202fb2-67e2-4129-b7ec-16a73ca8efaa\" target=\"1e9f5cdd-2a7e-426d-85d8-9dc6b6afe5e6\"/><arc id=\"084ffd7d-cd99-45ba-9303-a95d19e17b36\" source=\"81fcdf8e-5595-4f16-8718-d8a848c5a340\" target=\"456422e2-d029-4538-8ebb-4b125ad259c4\"/><arc id=\"4e73595b-3f91-483a-a97d-98542cff8e90\" source=\"0c5299ca-ce4b-43de-8c2d-3732138b7394\" target=\"ab5290ff-b971-41ed-8a34-70f1739840a6\"/><arc id=\"af7984a3-75c8-4ba5-ac71-7bba51406718\" source=\"b6e00576-d34a-4a48-bc98-e25a05b23fca\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"6e47bf49-f9c6-4f5e-adb8-0e525d86597d\" source=\"909a382e-a2e5-4451-bde3-c6b684afdd8c\" target=\"9c6b1056-ee28-4ff4-a325-83cb46fed56a\"/><arc id=\"f32394dc-0aa3-4292-b7a0-57f96038a328\" source=\"aacb7235-c484-48d8-80a3-3d579ba77c01\" target=\"b53e9eef-3d47-417f-975c-8092e7e2a3ec\"/><arc id=\"cfdc285c-1510-4a18-8512-900a84262379\" source=\"278f94fa-56f0-4191-b0d4-13ccda66ddbb\" target=\"a36deaf0-aa3d-418a-abe3-1eab5063750b\"/><arc id=\"ccd355af-2967-4364-b534-8886879d12ef\" source=\"1960718a-94c0-498c-b6a9-457df6ac3955\" target=\"0a6bd54b-c1c2-42ae-b7f0-a17e2d206234\"/><arc id=\"10a7c30a-9f08-466b-b39d-43556d63aa47\" source=\"b63d1898-65ba-426e-adca-bb1cc2fb9fae\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"83b4df13-eb27-40e1-9216-a2e93568ba16\" source=\"4d257a12-fa78-4b50-a773-4cc2d18ceed5\" target=\"2d8830f9-c97b-480d-a3f7-e5b2d8b61f0b\"/><arc id=\"0917db1b-a2f1-4885-be4a-fde26bc0c149\" source=\"79d5a257-e167-44bf-9cc3-a45351b6c4bf\" target=\"383f5a40-0735-43a3-9bf3-574b1cb97baa\"/><arc id=\"399e80ba-2fa5-43f2-9b75-a8ba92fd854d\" source=\"7270b705-ae7c-4031-902b-56e8ffadeba7\" target=\"a3dc698b-f458-4033-a5e1-6124d831afe4\"/><arc id=\"8b8420df-6b3e-4211-b5b9-1df70b8bda97\" source=\"50fb3081-a79f-4309-873f-dc0f0213b91d\" target=\"8628dfce-b75c-4791-a326-831ecbc7d960\"/><arc id=\"0360117d-2fb2-46fe-9510-87fd0cc14309\" source=\"61c40946-c18d-4500-abe8-8cdb382965ec\" target=\"a4bad625-8c2d-4817-b991-bf611094ab1b\"/><arc id=\"cb2aba59-8a7c-404d-b0b2-62f12e8312a5\" source=\"54ca1ed6-b0bc-42a6-becf-2089db031b33\" target=\"eb444ae2-27ca-40ae-a377-cdf04a4d8bc2\"/><arc id=\"d16793a3-673d-47c3-b85c-fcb0524866bc\" source=\"99631d87-3e38-4f4a-9644-861015ec2495\" target=\"d5e12359-9767-4f05-a30f-5771f1696db4\"/><arc id=\"dedd16c1-c14e-46bd-b341-0358ca37c0e9\" source=\"f5347f5e-1100-44b4-9427-ec5958a70d65\" target=\"68e64763-a6f5-4bd2-afb2-7f19cde7bebe\"/><arc id=\"5b33abd1-81f4-4ffa-8116-1f5f4b4f70d3\" source=\"ded883b4-bdee-4433-9ba6-312b93fea943\" target=\"b63d1898-65ba-426e-adca-bb1cc2fb9fae\"/><arc id=\"e9fb4d39-4300-44ff-bb65-dc41a70beb51\" source=\"d5e12359-9767-4f05-a30f-5771f1696db4\" target=\"68340ee5-f95d-419d-a855-d1cde8f06a05\"/><arc id=\"2ed8df4c-a05b-46d9-9eda-dae8a7bf976e\" source=\"ab5290ff-b971-41ed-8a34-70f1739840a6\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"12993225-4c8e-4ac8-9351-33fcbfb3c8b9\" source=\"fc006fdf-f7bb-4e83-b69c-cad4e8a7e9ff\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"f56ab459-a8fe-474a-8eb1-b08fe8d7ccdf\" source=\"13a3035a-2ef3-4320-9ef5-622fe3ce8226\" target=\"fbba774e-3764-43ab-b240-82f1a685c05e\"/><arc id=\"c0c9d2e8-d773-42d8-ab17-e4f3bf9dc6bf\" source=\"f10f7326-1014-4583-bed2-f3f0b4752382\" target=\"13d209e9-cea1-48c2-ae59-c05302560d0a\"/><arc id=\"5d89a5d8-7c74-4fd7-a96a-3c12ca19cc8a\" source=\"047dd1bf-6981-4fd7-9b09-40f7745f38cc\" target=\"fc006fdf-f7bb-4e83-b69c-cad4e8a7e9ff\"/><arc id=\"e608582e-a89f-4cdb-835c-1bc6d580689b\" source=\"fc7267d2-df24-4ab2-84ed-f76c53a1295f\" target=\"7b10a58b-ac4e-419b-ad73-e1a24d4397d6\"/><arc id=\"0ee312e2-104b-4f7a-b569-33a2f4916c3f\" source=\"8638efad-0dce-4764-99e7-075f760f15c6\" target=\"609d83f5-790f-4148-bde7-09f8ec32be69\"/><arc id=\"5457e439-223b-4590-a6e7-8253a05cbf9c\" source=\"c0c325e0-d6f4-4ff4-87e5-d8bafe9100ca\" target=\"ded883b4-bdee-4433-9ba6-312b93fea943\"/><arc id=\"c071b3de-6a9e-410a-94ac-f4f894d21b93\" source=\"f0fba2bf-247e-4e06-8161-c2e5e22b99f9\" target=\"cf0d809f-8c78-45e9-8aec-a73e750067a8\"/><arc id=\"f5d389e0-a760-4ea5-8874-0163acda507e\" source=\"68340ee5-f95d-419d-a855-d1cde8f06a05\" target=\"aacb7235-c484-48d8-80a3-3d579ba77c01\"/><arc id=\"365382db-4cce-4fa9-83d9-c9a7d8c01443\" source=\"2b435378-f2f4-4bf1-8949-eca9a89b45e0\" target=\"54ca1ed6-b0bc-42a6-becf-2089db031b33\"/><arc id=\"e48df572-3985-4318-9cd0-41fcce2c7b8a\" source=\"2780b794-ddc6-453b-a413-a474cf3ee2f1\" target=\"0e8022c1-6c09-4129-973a-2fad5e642314\"/><arc id=\"cb89dc71-febf-460b-b4bc-42e572fe4499\" source=\"577486c2-bfdb-480d-8014-fae477371e7e\" target=\"4d257a12-fa78-4b50-a773-4cc2d18ceed5\"/><arc id=\"a2519273-7d98-4925-bbd0-ac895ce7cf37\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"47560cdd-04f9-4fe2-ba17-1cfb4a1533f7\"/><arc id=\"c277df2c-b827-4e0e-9a3a-73ff8533b7c6\" source=\"60e0b174-d585-4faa-83b9-e1ea5e7098ed\" target=\"2654070d-78bf-46a5-85c2-1a9dcfc4e10b\"/><arc id=\"de8626b0-9d6d-4404-a3eb-fa2c51fbfcc5\" source=\"c0400c82-05de-475b-8bfb-642a8842855c\" target=\"29702e9e-9c6a-47bd-b1bf-39038b24a442\"/><arc id=\"99acbdc9-73c8-4bbb-816f-e6248c38d009\" source=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\" target=\"397b0d19-0593-4824-ac0b-7787ac293f90\"/><arc id=\"ed1c52f6-e86d-459b-9303-2515f338ed28\" source=\"e21fb736-7a68-4980-a576-208d27377086\" target=\"3ce65202-2483-42cb-bcbd-0b336dadad95\"/><arc id=\"b6890da0-61f0-4723-bff9-026a188c686a\" source=\"63a6916e-1f60-4db7-bfa4-be6976c4df4a\" target=\"1dec93f1-3c1f-4048-84f0-82703a1b9135\"/><arc id=\"34598100-b1a7-43ee-bc0f-6122a9b0a209\" source=\"a78f3b36-e91d-4151-a046-d715cf178de2\" target=\"909a382e-a2e5-4451-bde3-c6b684afdd8c\"/><arc id=\"1aa492a2-8fe0-4b0e-99d2-f49c79ab5168\" source=\"f6b11751-aab1-4256-b19c-7fd6509a3744\" target=\"90eff012-be2c-4400-8eac-c6152ee11b78\"/><arc id=\"5ae814ae-9faa-4a4d-ad39-7c2d8d4ef0b9\" source=\"ef647550-ef00-4eaf-9cbf-c24c41864d99\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"ccd512b7-b958-4010-af5d-b34aee3a9162\" source=\"9c6b1056-ee28-4ff4-a325-83cb46fed56a\" target=\"a001db33-4aae-472d-9058-a1864297dae7\"/><arc id=\"6afc9d62-9625-461b-aa88-69b67ae558a8\" source=\"3ce65202-2483-42cb-bcbd-0b336dadad95\" target=\"13a3035a-2ef3-4320-9ef5-622fe3ce8226\"/><arc id=\"8e17c46e-b436-4b78-95b9-2640de1152e5\" source=\"ca9926cd-7a8a-4ed9-b70f-71557be2f344\" target=\"f0fba2bf-247e-4e06-8161-c2e5e22b99f9\"/><arc id=\"2cdae7e8-2244-4dfc-9c5e-fd53dd0b2c8c\" source=\"caac5932-41fe-4e17-ba53-85eda5b4a4e6\" target=\"3a04248f-a715-4411-81d8-20e967c315c6\"/><arc id=\"4361cc3b-4680-459e-9b78-4b3329532a7e\" source=\"3f0a06d6-7783-44e0-a6a0-ba8283845c04\" target=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\"/><arc id=\"ce174660-c38b-496e-a2a5-3291c40f6f2b\" source=\"369f4d9a-6807-42ac-ab80-67c2fbc71188\" target=\"99631d87-3e38-4f4a-9644-861015ec2495\"/><arc id=\"9ec18af3-e160-42dc-8ca6-e983ab54ac3a\" source=\"769bdb06-6e57-4aa8-a4bb-fa01dfa67f76\" target=\"e21fb736-7a68-4980-a576-208d27377086\"/><arc id=\"ca5a8b86-2f3c-4df9-8ff2-251dcf9b3c06\" source=\"ec2e1beb-cabf-4ae3-b7de-40f612df107a\" target=\"2780b794-ddc6-453b-a413-a474cf3ee2f1\"/><arc id=\"ca38efd8-a714-4cfc-abdb-f60513d3eaf6\" source=\"aacb7235-c484-48d8-80a3-3d579ba77c01\" target=\"dacc2f3f-87e6-4c2b-9c7e-2ab93f59d9c0\"/><arc id=\"03cfd63c-980b-4ee4-936a-e9e78b9304e4\" source=\"b591de18-3723-426f-8c6a-b38eb6018bfa\" target=\"9380bf0e-2ac4-4e79-b000-e525f3d8191f\"/><arc id=\"40ad4945-4671-42bb-acab-a34d42f29e7e\" source=\"ab11ce4d-9df3-4cc9-8401-f12e4a3b333f\" target=\"d8c0d90d-732f-40d0-b888-5f35759a5c5b\"/><arc id=\"3f2188f7-ca3d-4943-b3b0-60f56b2bcf65\" source=\"ed426528-fe4d-4baf-8680-3811d2781ff9\" target=\"81fcdf8e-5595-4f16-8718-d8a848c5a340\"/><arc id=\"ca65ba49-317d-4a5e-aebf-95d592665101\" source=\"47560cdd-04f9-4fe2-ba17-1cfb4a1533f7\" target=\"8638efad-0dce-4764-99e7-075f760f15c6\"/><arc id=\"056369dc-cefe-4083-8b10-c76e7b8ef422\" source=\"a3dc698b-f458-4033-a5e1-6124d831afe4\" target=\"3ff10844-faf3-4693-98b0-61a6afe5d228\"/><arc id=\"8d3cda5d-707a-4411-a893-a278fc93c884\" source=\"d5e12359-9767-4f05-a30f-5771f1696db4\" target=\"903e30c7-d1d0-4a6d-a70a-2bbf1383a310\"/><arc id=\"70fd3843-6690-4055-9355-64bdf3f7154a\" source=\"002c9965-97e1-4e46-b4b2-1a732b96b357\" target=\"e6129bb4-936f-4e33-981a-1e34fb34cdb3\"/><arc id=\"2b6d0859-9330-4e21-beb6-576f76717a17\" source=\"88788edf-6081-4176-80da-8306fabb6940\" target=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\"/><arc id=\"0b239dac-649a-477c-b7c3-ba11e959dbdf\" source=\"46df1f8c-321b-4aec-9d00-09a67a8e29cf\" target=\"b0e79469-29e6-4ae8-8c6f-7333a535d65a\"/><arc id=\"4f8f3a40-de57-446d-936c-551a9c4c1491\" source=\"d0b3c45b-2215-4b43-82af-8f8e66554b81\" target=\"61c40946-c18d-4500-abe8-8cdb382965ec\"/><arc id=\"f27467d7-2f08-4bb6-aae5-5d87859f171e\" source=\"0e8022c1-6c09-4129-973a-2fad5e642314\" target=\"d2aa7cd4-91cd-45a6-a9a4-d77d51c47cf3\"/><arc id=\"968df300-77a4-450c-a5b7-ef566e631432\" source=\"7af861a4-17fb-441f-a7d5-5672d060b0c5\" target=\"f56a65de-f0ad-4a1e-bcb5-bb589f1f15d3\"/><arc id=\"3ef25bbe-32e5-4317-a4d8-7339d7dc61aa\" source=\"6dc83cd4-4175-427c-811a-391a3f871719\" target=\"fc7267d2-df24-4ab2-84ed-f76c53a1295f\"/><arc id=\"15993de0-832b-4b5d-bea8-8a83ccd90936\" source=\"8628dfce-b75c-4791-a326-831ecbc7d960\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"2157c367-1cf6-4632-86c5-09de204746cd\" source=\"1ea773b8-992e-4585-9c7d-ef740a4f5484\" target=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\"/><arc id=\"d7b0d3ec-0ee4-450d-ba9b-61a0c0116769\" source=\"5d013664-e2c6-4ed5-b0c8-defac1a91134\" target=\"842c5a9a-0dce-4941-9568-c6bad9a8892b\"/><arc id=\"a7bdcbc4-8b70-4526-8e56-c6d40229d3f2\" source=\"903e30c7-d1d0-4a6d-a70a-2bbf1383a310\" target=\"63a6916e-1f60-4db7-bfa4-be6976c4df4a\"/><arc id=\"3685464b-bf1f-4d27-87af-46e11ac701b4\" source=\"609d83f5-790f-4148-bde7-09f8ec32be69\" target=\"77202fb2-67e2-4129-b7ec-16a73ca8efaa\"/><arc id=\"223fb786-a9d2-4651-a621-7e3a946ee750\" source=\"81f71686-56ca-4a7a-a569-09bb072005e3\" target=\"d63d69fe-ec08-4ad3-afa5-c3fd40fe0280\"/><arc id=\"8ea19c81-e488-430b-9c8b-5620307dcb1a\" source=\"2c3ce0cd-32c8-4627-b1f5-c3d62bb064b3\" target=\"ab11ce4d-9df3-4cc9-8401-f12e4a3b333f\"/><arc id=\"fd7e4036-cd8a-4c5e-aafd-1af2f57bc88b\" source=\"af88e0a2-e53e-4cb8-a172-a628b08465d0\" target=\"3ce65202-2483-42cb-bcbd-0b336dadad95\"/><arc id=\"0e6eee5d-0fad-4c71-b436-c89cd7bb9c17\" source=\"73c5d1b9-3faf-44a7-9db0-433133ef8b48\" target=\"7af861a4-17fb-441f-a7d5-5672d060b0c5\"/><arc id=\"b29234ba-3f01-4d7b-b004-656cc454746b\" source=\"dbbbb762-64d8-4d44-be8a-2d7772b4d134\" target=\"5d013664-e2c6-4ed5-b0c8-defac1a91134\"/><arc id=\"fc75a695-eeb2-42e9-a6e2-91dc0e92a896\" source=\"842c5a9a-0dce-4941-9568-c6bad9a8892b\" target=\"4300640c-329f-4fe4-9d97-efad7add169d\"/><arc id=\"b504558a-d178-448c-9024-51b3cb1891e2\" source=\"a6d83837-9256-4656-9df3-79be5cd170d5\" target=\"0ba695fd-6e65-45b5-91e9-78d8b2801a09\"/><arc id=\"71080a81-e56e-4a7a-b218-1009cb4f267a\" source=\"7b10a58b-ac4e-419b-ad73-e1a24d4397d6\" target=\"cfe567f7-bf65-4afd-a5ef-e99ce9b6f931\"/><arc id=\"70ee5302-5a84-4941-a499-49be4b13a7c9\" source=\"d23defa0-438b-4083-a5df-e12fb1f681f7\" target=\"2e073c95-c028-4d2b-b83d-5a5cc3320942\"/><arc id=\"5cc99153-6178-42a6-a299-28718236deb1\" source=\"af595107-a9e3-453e-8004-388600dbdc4a\" target=\"cf0033b4-21c5-4c07-89ab-f4ad60278642\"/><arc id=\"4eb3c811-8039-4cab-a704-0fc681255088\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"d91205b8-5d43-486c-98bc-a9c003eb699b\"/><arc id=\"6af9e455-9089-4d36-884c-bf86282a7612\" source=\"a571f921-7dfb-4ce8-9b0a-461c3a2e1527\" target=\"44a7da67-f632-4923-bf73-873cce80f03e\"/><arc id=\"fdf99058-8b22-4b17-a180-4400f60f5a78\" source=\"c9efc280-56d1-47b2-9213-d7bb56a2aa41\" target=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\"/><arc id=\"76dd9f02-a0f1-46cd-b192-830c4f5105c1\" source=\"d2aa7cd4-91cd-45a6-a9a4-d77d51c47cf3\" target=\"2c3ce0cd-32c8-4627-b1f5-c3d62bb064b3\"/><arc id=\"39bb531a-545f-4e6a-9b0f-5ec2de46b4c4\" source=\"aacb7235-c484-48d8-80a3-3d579ba77c01\" target=\"9242f595-c83d-4b39-82f0-4392054f4690\"/><arc id=\"ba4c10c7-5780-430f-8a2c-9c6f9b324727\" source=\"a6a77119-62bc-4c8a-a7ee-683121ffe69a\" target=\"48fa4b72-8884-439f-8371-313a942a3b05\"/><arc id=\"144e2715-e0aa-490e-b6d2-9b4a3c9e1493\" source=\"cfe567f7-bf65-4afd-a5ef-e99ce9b6f931\" target=\"2b435378-f2f4-4bf1-8949-eca9a89b45e0\"/><arc id=\"498f3fa0-d90d-40ca-8a83-ebaba8b154ed\" source=\"be9a9d92-1d2c-47e8-9711-79d76c80c9ac\" target=\"ca9926cd-7a8a-4ed9-b70f-71557be2f344\"/><arc id=\"19c47f44-83df-499d-a4dc-f8186e33879a\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"ad913315-1138-467a-be2c-ba6093dcb73e\"/><arc id=\"8c8c83d6-b17b-431a-8d1f-cc5c4aabbf16\" source=\"cf0d809f-8c78-45e9-8aec-a73e750067a8\" target=\"002c9965-97e1-4e46-b4b2-1a732b96b357\"/><arc id=\"82d826d8-0886-4318-ba17-6b55a8ac0834\" source=\"d63d69fe-ec08-4ad3-afa5-c3fd40fe0280\" target=\"61ed8d25-c067-4f0a-a1ce-54818e12e8e8\"/><arc id=\"3c1c720c-5fd6-4820-9ce4-540059352119\" source=\"0a6bd54b-c1c2-42ae-b7f0-a17e2d206234\" target=\"50fb3081-a79f-4309-873f-dc0f0213b91d\"/><arc id=\"7f530888-6f93-44cf-8070-e4cf6692e496\" source=\"cf0033b4-21c5-4c07-89ab-f4ad60278642\" target=\"f6eadeb5-11da-40f1-9372-f549801beab4\"/><arc id=\"cbe6eefb-885f-4935-b114-122ac1a7a034\" source=\"66eef4ac-d62a-47fe-a7c7-552830dd113a\" target=\"0c5299ca-ce4b-43de-8c2d-3732138b7394\"/><arc id=\"b515c36b-db63-4623-881e-78468b319e32\" source=\"471dcd81-b1b7-48f7-9790-c5732618accf\" target=\"577486c2-bfdb-480d-8014-fae477371e7e\"/><arc id=\"0e3c3018-9435-43ab-8c9c-f8420be88b68\" source=\"61c40946-c18d-4500-abe8-8cdb382965ec\" target=\"d23defa0-438b-4083-a5df-e12fb1f681f7\"/><arc id=\"6920216a-13db-45c3-b981-0d0544e03ff9\" source=\"dacc2f3f-87e6-4c2b-9c7e-2ab93f59d9c0\" target=\"caac5932-41fe-4e17-ba53-85eda5b4a4e6\"/><arc id=\"acbf9ce1-5cdf-47b3-808a-433341db2687\" source=\"aacb7235-c484-48d8-80a3-3d579ba77c01\" target=\"01691390-3901-467d-966e-9e261f211865\"/><arc id=\"858a6745-d017-486d-9ec1-58050fc9e3a7\" source=\"fdef60ba-d0be-43cf-a8da-563d73ddf77b\" target=\"5e8062cf-6e07-4306-bddf-1d5aeabbcfcf\"/><arc id=\"e1d0f2e6-7fd9-4819-b1ce-e0e73ba3f70f\" source=\"b0e79469-29e6-4ae8-8c6f-7333a535d65a\" target=\"f4b8cea0-0753-45cb-873a-73330154bdab\"/><arc id=\"9bfca87e-044e-4648-aeb6-58c6826c4d4f\" source=\"58cfb1a8-08a0-4e4c-a89c-cc0f7fb40224\" target=\"1ea773b8-992e-4585-9c7d-ef740a4f5484\"/><arc id=\"1c1e7a43-6c58-412a-b192-a49068919c0a\" source=\"48fa4b72-8884-439f-8371-313a942a3b05\" target=\"fc006fdf-f7bb-4e83-b69c-cad4e8a7e9ff\"/><arc id=\"ae070758-7937-48b3-b541-0c3e21d2d3b2\" source=\"f56a65de-f0ad-4a1e-bcb5-bb589f1f15d3\" target=\"c0400c82-05de-475b-8bfb-642a8842855c\"/><arc id=\"7001d540-3808-4005-a325-bb510dabc0d8\" source=\"1e9f5cdd-2a7e-426d-85d8-9dc6b6afe5e6\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"44160793-eda9-411c-b472-cc13983f2f21\" source=\"3d455578-8ee0-4b3c-a3f7-b2fd0624269f\" target=\"eca8c4ac-2e50-4b70-b6eb-b5df56310146\"/><arc id=\"162100c1-e1ec-4062-9c8f-1bc4ca534399\" source=\"2e073c95-c028-4d2b-b83d-5a5cc3320942\" target=\"0e346604-1e56-4b45-86ad-7c6f4e18cae6\"/><arc id=\"0ff26a2f-92e8-4697-8f2d-1d9d2d1c7120\" source=\"ad913315-1138-467a-be2c-ba6093dcb73e\" target=\"f5347f5e-1100-44b4-9427-ec5958a70d65\"/><arc id=\"36f18f42-e0f9-467d-9ddb-29625f70167a\" source=\"ed426528-fe4d-4baf-8680-3811d2781ff9\" target=\"a21acbff-fe77-4d30-8b6a-62291b0a18e0\"/><arc id=\"aaab6c71-8607-4e95-b70f-57dc8d43bda6\" source=\"49be4485-cbd8-4cb5-9054-dda79e3443b7\" target=\"471dcd81-b1b7-48f7-9790-c5732618accf\"/><arc id=\"84db0a72-a12f-4b96-8007-d3bdb4400159\" source=\"29702e9e-9c6a-47bd-b1bf-39038b24a442\" target=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\"/><arc id=\"c9f8223f-aef3-46cc-9cc3-39b40b525e5f\" source=\"02e49a40-d7e1-4c6e-a703-a34604543136\" target=\"a6a77119-62bc-4c8a-a7ee-683121ffe69a\"/><arc id=\"cb885e7c-ff97-46fe-b620-74d9f333d88b\" source=\"3a04248f-a715-4411-81d8-20e967c315c6\" target=\"583cb6e3-a574-4775-8781-dc62d1e739b9\"/><arc id=\"e4c8aacc-d7df-42be-9af5-85b7e7f5fd5e\" source=\"25b259c0-c776-448b-bdca-3df4d9a98347\" target=\"047dd1bf-6981-4fd7-9b09-40f7745f38cc\"/><arc id=\"e7775175-2e0f-491e-b276-1f2b7f889de2\" source=\"9242f595-c83d-4b39-82f0-4392054f4690\" target=\"4a9c0d39-ce98-4dba-b712-70201ecbc590\"/><arc id=\"da4b3fe5-45ea-4009-92fe-8b4ec3236b7e\" source=\"2654070d-78bf-46a5-85c2-1a9dcfc4e10b\" target=\"54ca1ed6-b0bc-42a6-becf-2089db031b33\"/><arc id=\"2fe529c5-d989-4595-9d1e-1fdbd6f18eb8\" source=\"df6f83c5-2df7-4aca-b918-ed9a0a92bb78\" target=\"369f4d9a-6807-42ac-ab80-67c2fbc71188\"/><arc id=\"9d481903-8ad7-4c79-9d19-ff717d9b0b7a\" source=\"471dcd81-b1b7-48f7-9790-c5732618accf\" target=\"ec2e1beb-cabf-4ae3-b7de-40f612df107a\"/><arc id=\"e17e12ac-b1bd-4b04-a1b6-e975e377db33\" source=\"2162d892-6215-491e-954e-0d29d043a652\" target=\"88788edf-6081-4176-80da-8306fabb6940\"/><arc id=\"5fbfda1b-8743-4fd7-86af-e9393b855e67\" source=\"8eb7b087-9409-4f22-96a1-6a168eae0451\" target=\"4a2ea91f-1b73-4edd-a79f-0b44537255f1\"/><arc id=\"27634074-cb8d-4fd7-9e35-df76e323b769\" source=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\" target=\"2b11f68f-b524-404b-9e2a-a4a677125f1b\"/><arc id=\"3def3ea6-2387-4b99-8947-4e4d5464cf90\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"49be4485-cbd8-4cb5-9054-dda79e3443b7\"/><arc id=\"235ffd34-3486-4008-a3d8-3b78a7b4cef4\" source=\"0ba695fd-6e65-45b5-91e9-78d8b2801a09\" target=\"2162d892-6215-491e-954e-0d29d043a652\"/><arc id=\"a847301d-27ed-4824-8fd2-cfb0670668cf\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"cf7237ae-8144-4bca-a70e-65b5b6f2b309\"/><arc id=\"3fe633f9-b4c3-44bc-a0ed-e34d7b657282\" source=\"1dec93f1-3c1f-4048-84f0-82703a1b9135\" target=\"f10f7326-1014-4583-bed2-f3f0b4752382\"/><arc id=\"14352b76-f9c7-41e7-8a85-880dd88e82cc\" source=\"b01a2183-b9ef-408c-b3bf-0ba75c9c9fc9\" target=\"e21fb736-7a68-4980-a576-208d27377086\"/><arc id=\"6b00f2ac-e0d1-40e6-8321-318427045ebd\" source=\"f6eadeb5-11da-40f1-9372-f549801beab4\" target=\"58cfb1a8-08a0-4e4c-a89c-cc0f7fb40224\"/><arc id=\"35b766b4-eb02-444b-a4e9-1bd39d10880a\" source=\"d8c0d90d-732f-40d0-b888-5f35759a5c5b\" target=\"fdef60ba-d0be-43cf-a8da-563d73ddf77b\"/><arc id=\"7b68af8e-dfb2-442a-a8dd-6a6ea5e86d4b\" source=\"a001db33-4aae-472d-9058-a1864297dae7\" target=\"2ad4ac3d-4da1-4ebd-bfdf-d7e02e21c1ca\"/><arc id=\"2f4a712a-d9a0-49f4-98fb-54ec40af1777\" source=\"d8bc5e04-771d-457c-9e89-f201b2e8f076\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"fb4a2880-3e9a-4056-b9eb-f2aec017dc24\" source=\"2d8830f9-c97b-480d-a3f7-e5b2d8b61f0b\" target=\"8eb7b087-9409-4f22-96a1-6a168eae0451\"/><arc id=\"1d05814d-7d63-4845-a646-05b7a6df38d9\" source=\"eb444ae2-27ca-40ae-a377-cdf04a4d8bc2\" target=\"b01a2183-b9ef-408c-b3bf-0ba75c9c9fc9\"/><arc id=\"ff375786-453f-411d-9906-b9250a88767e\" source=\"90eff012-be2c-4400-8eac-c6152ee11b78\" target=\"d8bc5e04-771d-457c-9e89-f201b2e8f076\"/><arc id=\"26764def-50f8-4a2f-9dac-5c5ed7b25b8f\" source=\"3ff10844-faf3-4693-98b0-61a6afe5d228\" target=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\"/><arc id=\"e7775740-b298-4431-89a3-650976d83e6e\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"b591de18-3723-426f-8c6a-b38eb6018bfa\"/><arc id=\"264344fb-a593-4e98-ab49-374206e911d3\" source=\"e6129bb4-936f-4e33-981a-1e34fb34cdb3\" target=\"25b259c0-c776-448b-bdca-3df4d9a98347\"/><arc id=\"d498edc1-b46d-45d2-83ed-3725c2fafb79\" source=\"65479e5c-968d-4814-a58e-644623b966f5\" target=\"af88e0a2-e53e-4cb8-a172-a628b08465d0\"/><arc id=\"adb14050-d71f-4d63-803b-ae4aa4bdc134\" source=\"b6ddeffe-5fda-4cc1-9449-9f655fdf44bb\" target=\"c0c325e0-d6f4-4ff4-87e5-d8bafe9100ca\"/><arc id=\"98a5ec5a-fbaf-4c37-b59c-3fbdfb65fef3\" source=\"d5dee855-7524-4095-b065-4f96e379aa46\" target=\"c13179b8-8a3e-4ced-bb3d-77e1a9aee127\"/><arc id=\"00798aea-1581-40a0-8309-0e4bef2e2d63\" source=\"583cb6e3-a574-4775-8781-dc62d1e739b9\" target=\"3f0a06d6-7783-44e0-a6a0-ba8283845c04\"/><arc id=\"4eb29204-5ea1-4b8d-9ad1-61a368765e3d\" source=\"61ed8d25-c067-4f0a-a1ce-54818e12e8e8\" target=\"ef647550-ef00-4eaf-9cbf-c24c41864d99\"/><arc id=\"a518696a-821a-43fb-8eb5-173e40cbbee7\" source=\"919de476-1796-4a7b-8f87-84b21f9b7005\" target=\"df6f83c5-2df7-4aca-b918-ed9a0a92bb78\"/><arc id=\"a0fb374b-8e47-424b-8b0e-ca01f5efd6ac\" source=\"cf7237ae-8144-4bca-a70e-65b5b6f2b309\" target=\"81f71686-56ca-4a7a-a569-09bb072005e3\"/><arc id=\"e83ff7cb-5b66-48ee-b207-be4e81572b7a\" source=\"4a2ea91f-1b73-4edd-a79f-0b44537255f1\" target=\"ab11ce4d-9df3-4cc9-8401-f12e4a3b333f\"/><arc id=\"984a2caf-22ca-4b01-8f84-a8e4d851b064\" source=\"397b0d19-0593-4824-ac0b-7787ac293f90\" target=\"ed426528-fe4d-4baf-8680-3811d2781ff9\"/><arc id=\"23a63154-35cd-4228-b1dc-ac6f44fbec20\" source=\"2ad4ac3d-4da1-4ebd-bfdf-d7e02e21c1ca\" target=\"ce98e252-ba90-4634-b6db-edccd24dd6c8\"/><arc id=\"760ee400-d1ce-410d-af46-11eb7add19c2\" source=\"44a7da67-f632-4923-bf73-873cce80f03e\" target=\"8184c449-f2f6-4ad4-bc77-68f6d6518871\"/><arc id=\"27d202c4-9896-48d1-9cfc-7b610e1d147e\" source=\"f2f53ffb-20b2-4079-b1b0-05ee58d91122\" target=\"f6b11751-aab1-4256-b19c-7fd6509a3744\"/><arc id=\"6aa5c741-0b15-4309-95ff-1a6ff75a39a1\" source=\"13d209e9-cea1-48c2-ae59-c05302560d0a\" target=\"ed426528-fe4d-4baf-8680-3811d2781ff9\"/><arc id=\"ed71ce70-3a1b-46bc-a8fa-6bc59375bcf7\" source=\"5e8062cf-6e07-4306-bddf-1d5aeabbcfcf\" target=\"a571f921-7dfb-4ce8-9b0a-461c3a2e1527\"/><arc id=\"61997e44-5d1b-47d5-8e08-fed52f107d8a\" source=\"31d53bed-63b8-4584-86bb-d7bfc3ad6b2a\" target=\"79d5a257-e167-44bf-9cc3-a45351b6c4bf\"/><arc id=\"7791a036-5b1f-4055-90dd-77089ac39fe8\" source=\"0e346604-1e56-4b45-86ad-7c6f4e18cae6\" target=\"f0fba2bf-247e-4e06-8161-c2e5e22b99f9\"/><arc id=\"2a9ec179-40d2-45ed-b534-0de1a54e37f7\" source=\"9380bf0e-2ac4-4e79-b000-e525f3d8191f\" target=\"fac8835d-9a6c-4745-b423-289f468b5311\"/><arc id=\"9b5a0287-02cc-48cf-87c3-d1c04b6cb4ae\" source=\"a4bad625-8c2d-4817-b991-bf611094ab1b\" target=\"be9a9d92-1d2c-47e8-9711-79d76c80c9ac\"/><arc id=\"913ffb21-972b-4dae-9696-ccfdfc94200c\" source=\"4300640c-329f-4fe4-9d97-efad7add169d\" target=\"60e0b174-d585-4faa-83b9-e1ea5e7098ed\"/><arc id=\"f9ccd64d-d622-42de-b1a8-54dfbef6f821\" source=\"d91205b8-5d43-486c-98bc-a9c003eb699b\" target=\"d0b3c45b-2215-4b43-82af-8f8e66554b81\"/><arc id=\"d08f4e18-9f60-4ae9-9b3c-a3a7fa7a468e\" source=\"d5f92aa5-c5d9-492f-9a90-4db14d91f4ac\" target=\"b6e00576-d34a-4a48-bc98-e25a05b23fca\"/><arc id=\"14d49da1-cbcb-450b-90f4-2361fbb48d63\" source=\"456422e2-d029-4538-8ebb-4b125ad259c4\" target=\"73c5d1b9-3faf-44a7-9db0-433133ef8b48\"/><arc id=\"df43fd22-8346-426e-8a83-75a64f953fb9\" source=\"eca8c4ac-2e50-4b70-b6eb-b5df56310146\" target=\"b6ddeffe-5fda-4cc1-9449-9f655fdf44bb\"/><arc id=\"8d7a7dd9-7c26-4465-aa53-c74f5af3e790\" source=\"f4b8cea0-0753-45cb-873a-73330154bdab\" target=\"c9efc280-56d1-47b2-9213-d7bb56a2aa41\"/><arc id=\"84f7b659-b1a2-4ab8-ad98-0f990c01bb1f\" source=\"a21acbff-fe77-4d30-8b6a-62291b0a18e0\" target=\"d5dee855-7524-4095-b065-4f96e379aa46\"/><arc id=\"d17000dc-a661-49d1-9fc0-484a040db146\" source=\"f6a5b57e-2da1-47d7-9c9d-d5d16854cd92\" target=\"f2f53ffb-20b2-4079-b1b0-05ee58d91122\"/><arc id=\"b2f900d6-a2cd-411e-9027-0ba99704faa8\" source=\"80d5ce00-5b21-4c54-8279-bef22afbfcf4\" target=\"f6a5b57e-2da1-47d7-9c9d-d5d16854cd92\"/><arc id=\"ab6ddc6b-0347-44bc-8ff3-58a92c543886\" source=\"fbba774e-3764-43ab-b240-82f1a685c05e\" target=\"278f94fa-56f0-4191-b0d4-13ccda66ddbb\"/><arc id=\"da823d4b-4a40-4134-bd57-ec8b6e762659\" source=\"36e60dce-dcb3-4d3b-b008-a4b2d49494df\" target=\"eb6bf3d0-ff08-4c26-b3b8-8a56fc90856e\"/></page></net></pnml>');
/*!40000 ALTER TABLE `jbpt_petri_nets` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `jbpt_petri_nets_before_del_tr` BEFORE DELETE ON `jbpt_petri_nets`
  FOR EACH ROW
BEGIN
  INSERT IGNORE INTO `pql_index_status`
  VALUES (OLD.id,"DELETE",2,0,NULL,NULL,NULL);
  
  UPDATE `pql_index_status` SET `pql_index_status`.`status`=2
  WHERE `pql_index_status`.`net_id` = OLD.id;

  DELETE FROM jbpt_petri_nodes  WHERE jbpt_petri_nodes.net_id=OLD.id;
  
  DELETE FROM pql_can_occur     WHERE pql_can_occur.net_id=OLD.id;
  DELETE FROM pql_always_occurs WHERE pql_always_occurs.net_id=OLD.id;
  DELETE FROM pql_can_conflict  WHERE pql_can_conflict.net_id=OLD.id;
  DELETE FROM pql_can_cooccur   WHERE pql_can_cooccur.net_id=OLD.id;
  DELETE FROM pql_total_causal  WHERE pql_total_causal.net_id=OLD.id;
  DELETE FROM pql_total_concur  WHERE pql_total_concur.net_id=OLD.id;
  
  
  


  

  
  DELETE FROM pql_index_status WHERE pql_index_status.net_id=OLD.id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `jbpt_petri_nodes`
--

DROP TABLE IF EXISTS `jbpt_petri_nodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jbpt_petri_nodes` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `net_id` int(11) unsigned NOT NULL,
  `uuid` varchar(100) NOT NULL,
  `name` text,
  `description` text,
  `label_id` int(10) unsigned DEFAULT NULL,
  `is_transition` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `label_id` (`label_id`),
  KEY `net_id` (`net_id`),
  KEY `is_transition` (`is_transition`),
  KEY `triple_index` (`net_id`,`is_transition`,`label_id`),
  CONSTRAINT `jbpt_nodes_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `jbpt_petri_nodes_fk` FOREIGN KEY (`label_id`) REFERENCES `jbpt_labels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jbpt_petri_nodes`
--

LOCK TABLES `jbpt_petri_nodes` WRITE;
/*!40000 ALTER TABLE `jbpt_petri_nodes` DISABLE KEYS */;
INSERT INTO `jbpt_petri_nodes` VALUES (1,1,'81f71686-56ca-4a7a-a569-09bb072005e3','p1','',NULL,0),(2,1,'31d53bed-63b8-4584-86bb-d7bfc3ad6b2a','p2','',NULL,0),(3,1,'e6129bb4-936f-4e33-981a-1e34fb34cdb3','p3','',NULL,0),(4,1,'b01a2183-b9ef-408c-b3bf-0ba75c9c9fc9','p4','',NULL,0),(5,1,'2162d892-6215-491e-954e-0d29d043a652','p5','',NULL,0),(6,1,'4a9c0d39-ce98-4dba-b712-70201ecbc590','p6','',NULL,0),(7,1,'919de476-1796-4a7b-8f87-84b21f9b7005','p7','',NULL,0),(8,1,'caac5932-41fe-4e17-ba53-85eda5b4a4e6','p8','',NULL,0),(9,1,'7af861a4-17fb-441f-a7d5-5672d060b0c5','p9','',NULL,0),(10,1,'0c5299ca-ce4b-43de-8c2d-3732138b7394','p10','',NULL,0),(11,1,'ab11ce4d-9df3-4cc9-8401-f12e4a3b333f','p11','',NULL,0),(12,1,'aacb7235-c484-48d8-80a3-3d579ba77c01','p12','',NULL,0),(13,1,'d5dee855-7524-4095-b065-4f96e379aa46','p13','',NULL,0),(14,1,'dbbbb762-64d8-4d44-be8a-2d7772b4d134','p14','',NULL,0),(15,1,'ca9926cd-7a8a-4ed9-b70f-71557be2f344','p15','',NULL,0),(16,1,'a6d83837-9256-4656-9df3-79be5cd170d5','p16','',NULL,0),(17,1,'46df1f8c-321b-4aec-9d00-09a67a8e29cf','p17','',NULL,0),(18,1,'583cb6e3-a574-4775-8781-dc62d1e739b9','p18','',NULL,0),(19,1,'65479e5c-968d-4814-a58e-644623b966f5','p19','',NULL,0),(20,1,'369f4d9a-6807-42ac-ab80-67c2fbc71188','p20','',NULL,0),(21,1,'769bdb06-6e57-4aa8-a4bb-fa01dfa67f76','p21','',NULL,0),(22,1,'02e49a40-d7e1-4c6e-a703-a34604543136','p22','',NULL,0),(23,1,'50fb3081-a79f-4309-873f-dc0f0213b91d','p23','',NULL,0),(24,1,'60e0b174-d585-4faa-83b9-e1ea5e7098ed','p24','',NULL,0),(25,1,'61ed8d25-c067-4f0a-a1ce-54818e12e8e8','p25','',NULL,0),(26,1,'a3dc698b-f458-4033-a5e1-6124d831afe4','p26','',NULL,0),(27,1,'f2f53ffb-20b2-4079-b1b0-05ee58d91122','p27','',NULL,0),(28,1,'f4b8cea0-0753-45cb-873a-73330154bdab','p28','',NULL,0),(29,1,'cfe567f7-bf65-4afd-a5ef-e99ce9b6f931','p29','',NULL,0),(30,1,'b6ddeffe-5fda-4cc1-9449-9f655fdf44bb','p30','',NULL,0),(31,1,'9380bf0e-2ac4-4e79-b000-e525f3d8191f','p31','',NULL,0),(32,1,'54ca1ed6-b0bc-42a6-becf-2089db031b33','p32','',NULL,0),(33,1,'eb6bf3d0-ff08-4c26-b3b8-8a56fc90856e','p33','',NULL,0),(34,1,'471dcd81-b1b7-48f7-9790-c5732618accf','p34','',NULL,0),(35,1,'ce98e252-ba90-4634-b6db-edccd24dd6c8','p35','',NULL,0),(36,1,'48fa4b72-8884-439f-8371-313a942a3b05','p36','',NULL,0),(37,1,'2780b794-ddc6-453b-a413-a474cf3ee2f1','p37','',NULL,0),(38,1,'fdef60ba-d0be-43cf-a8da-563d73ddf77b','p38','',NULL,0),(39,1,'909a382e-a2e5-4451-bde3-c6b684afdd8c','p39','',NULL,0),(40,1,'d2aa7cd4-91cd-45a6-a9a4-d77d51c47cf3','p40','',NULL,0),(41,1,'4d257a12-fa78-4b50-a773-4cc2d18ceed5','p41','',NULL,0),(42,1,'842c5a9a-0dce-4941-9568-c6bad9a8892b','p42','',NULL,0),(43,1,'047dd1bf-6981-4fd7-9b09-40f7745f38cc','p43','',NULL,0),(44,1,'58cfb1a8-08a0-4e4c-a89c-cc0f7fb40224','p44','',NULL,0),(45,1,'ed426528-fe4d-4baf-8680-3811d2781ff9','p45','',NULL,0),(46,1,'f5347f5e-1100-44b4-9427-ec5958a70d65','p46','',NULL,0),(47,1,'f10f7326-1014-4583-bed2-f3f0b4752382','p47','',NULL,0),(48,1,'a4bad625-8c2d-4817-b991-bf611094ab1b','p48','',NULL,0),(49,1,'8638efad-0dce-4764-99e7-075f760f15c6','p49','',NULL,0),(50,1,'a36deaf0-aa3d-418a-abe3-1eab5063750b','p50','',NULL,0),(51,1,'63a6916e-1f60-4db7-bfa4-be6976c4df4a','p51','',NULL,0),(52,1,'383f5a40-0735-43a3-9bf3-574b1cb97baa','p52','',NULL,0),(53,1,'8eb7b087-9409-4f22-96a1-6a168eae0451','p53','',NULL,0),(54,1,'3ce65202-2483-42cb-bcbd-0b336dadad95','p54','',NULL,0),(55,1,'80d5ce00-5b21-4c54-8279-bef22afbfcf4','p55','',NULL,0),(56,1,'d5e12359-9767-4f05-a30f-5771f1696db4','p56','',NULL,0),(57,1,'1960718a-94c0-498c-b6a9-457df6ac3955','p57','',NULL,0),(58,1,'ded883b4-bdee-4433-9ba6-312b93fea943','p58','',NULL,0),(59,1,'cf0d809f-8c78-45e9-8aec-a73e750067a8','p59','',NULL,0),(60,1,'d0b3c45b-2215-4b43-82af-8f8e66554b81','p60','',NULL,0),(61,1,'cf0033b4-21c5-4c07-89ab-f4ad60278642','p61','',NULL,0),(62,1,'d23defa0-438b-4083-a5df-e12fb1f681f7','p62','',NULL,0),(63,1,'456422e2-d029-4538-8ebb-4b125ad259c4','p63','',NULL,0),(64,1,'fc7267d2-df24-4ab2-84ed-f76c53a1295f','p64','',NULL,0),(65,1,'c0400c82-05de-475b-8bfb-642a8842855c','p65','',NULL,0),(66,1,'fbba774e-3764-43ab-b240-82f1a685c05e','p66','',NULL,0),(67,1,'90eff012-be2c-4400-8eac-c6152ee11b78','p67','',NULL,0),(68,1,'a571f921-7dfb-4ce8-9b0a-461c3a2e1527','p68','',NULL,0),(69,1,'0e346604-1e56-4b45-86ad-7c6f4e18cae6','p69','',NULL,0),(70,1,'d5f92aa5-c5d9-492f-9a90-4db14d91f4ac','p70','',NULL,0),(71,1,'a001db33-4aae-472d-9058-a1864297dae7','p71','',NULL,0),(72,1,'77202fb2-67e2-4129-b7ec-16a73ca8efaa','p72','',NULL,0),(73,1,'8184c449-f2f6-4ad4-bc77-68f6d6518871','p73','',NULL,0),(74,1,'3d455578-8ee0-4b3c-a3f7-b2fd0624269f','p74','',NULL,0),(75,1,'4300640c-329f-4fe4-9d97-efad7add169d','t1','',1,1),(76,1,'5d013664-e2c6-4ed5-b0c8-defac1a91134','t2','',NULL,1),(77,1,'fac8835d-9a6c-4745-b423-289f468b5311','t3','',2,1),(78,1,'af595107-a9e3-453e-8004-388600dbdc4a','t4','',NULL,1),(79,1,'73c5d1b9-3faf-44a7-9db0-433133ef8b48','t5','',NULL,1),(80,1,'68340ee5-f95d-419d-a855-d1cde8f06a05','t6','',NULL,1),(81,1,'a78f3b36-e91d-4151-a046-d715cf178de2','t7','',NULL,1),(82,1,'609d83f5-790f-4148-bde7-09f8ec32be69','t8','',3,1),(83,1,'d8bc5e04-771d-457c-9e89-f201b2e8f076','t9','',NULL,1),(84,1,'4a2ea91f-1b73-4edd-a79f-0b44537255f1','t10','',NULL,1),(85,1,'f6a5b57e-2da1-47d7-9c9d-d5d16854cd92','t11','',NULL,1),(86,1,'a21acbff-fe77-4d30-8b6a-62291b0a18e0','t12','',NULL,1),(87,1,'2c3ce0cd-32c8-4627-b1f5-c3d62bb064b3','t13','',NULL,1),(88,1,'8628dfce-b75c-4791-a326-831ecbc7d960','t14','',NULL,1),(89,1,'be9a9d92-1d2c-47e8-9711-79d76c80c9ac','t15','',4,1),(90,1,'dacc2f3f-87e6-4c2b-9c7e-2ab93f59d9c0','t16','',NULL,1),(91,1,'2b435378-f2f4-4bf1-8949-eca9a89b45e0','t17','',NULL,1),(92,1,'eb444ae2-27ca-40ae-a377-cdf04a4d8bc2','t18','',NULL,1),(93,1,'eca8c4ac-2e50-4b70-b6eb-b5df56310146','t19','',5,1),(94,1,'d63d69fe-ec08-4ad3-afa5-c3fd40fe0280','t20','',6,1),(95,1,'ec2e1beb-cabf-4ae3-b7de-40f612df107a','t21','',NULL,1),(96,1,'1ea773b8-992e-4585-9c7d-ef740a4f5484','t22','',NULL,1),(97,1,'01691390-3901-467d-966e-9e261f211865','t23','',NULL,1),(98,1,'903e30c7-d1d0-4a6d-a70a-2bbf1383a310','t24','',NULL,1),(99,1,'3ff10844-faf3-4693-98b0-61a6afe5d228','t25','',NULL,1),(100,1,'13d209e9-cea1-48c2-ae59-c05302560d0a','t26','',7,1),(101,1,'99631d87-3e38-4f4a-9644-861015ec2495','t27','',NULL,1),(102,1,'1dec93f1-3c1f-4048-84f0-82703a1b9135','t28','',8,1),(103,1,'1e9f5cdd-2a7e-426d-85d8-9dc6b6afe5e6','t29','',NULL,1),(104,1,'df6f83c5-2df7-4aca-b918-ed9a0a92bb78','t30','',NULL,1),(105,1,'3a04248f-a715-4411-81d8-20e967c315c6','t31','',9,1),(106,1,'c0c325e0-d6f4-4ff4-87e5-d8bafe9100ca','t32','',10,1),(107,1,'f6b11751-aab1-4256-b19c-7fd6509a3744','t33','',11,1),(108,1,'13a3035a-2ef3-4320-9ef5-622fe3ce8226','t34','',NULL,1),(109,1,'2b11f68f-b524-404b-9e2a-a4a677125f1b','t35','',NULL,1),(110,1,'e21fb736-7a68-4980-a576-208d27377086','t36','',NULL,1),(111,1,'88788edf-6081-4176-80da-8306fabb6940','t37','',NULL,1),(112,1,'d8c0d90d-732f-40d0-b888-5f35759a5c5b','t38','',12,1),(113,1,'61c40946-c18d-4500-abe8-8cdb382965ec','t39','',NULL,1),(114,1,'f56a65de-f0ad-4a1e-bcb5-bb589f1f15d3','t40','',13,1),(115,1,'9242f595-c83d-4b39-82f0-4392054f4690','t41','',NULL,1),(116,1,'ef647550-ef00-4eaf-9cbf-c24c41864d99','t42','',NULL,1),(117,1,'5e8062cf-6e07-4306-bddf-1d5aeabbcfcf','t43','',14,1),(118,1,'36e60dce-dcb3-4d3b-b008-a4b2d49494df','t44','',NULL,1),(119,1,'9c6b1056-ee28-4ff4-a325-83cb46fed56a','t45','',15,1),(120,1,'b591de18-3723-426f-8c6a-b38eb6018bfa','t46','',NULL,1),(121,1,'c13179b8-8a3e-4ced-bb3d-77e1a9aee127','t47','',16,1),(122,1,'b0e79469-29e6-4ae8-8c6f-7333a535d65a','t48','',17,1),(123,1,'a6a77119-62bc-4c8a-a7ee-683121ffe69a','t49','',18,1),(124,1,'fc006fdf-f7bb-4e83-b69c-cad4e8a7e9ff','t50','',NULL,1),(125,1,'0e8022c1-6c09-4129-973a-2fad5e642314','t51','',19,1),(126,1,'68e64763-a6f5-4bd2-afb2-7f19cde7bebe','t52','',20,1),(127,1,'f6eadeb5-11da-40f1-9372-f549801beab4','t53','',21,1),(128,1,'0ba695fd-6e65-45b5-91e9-78d8b2801a09','t54','',22,1),(129,1,'2ad4ac3d-4da1-4ebd-bfdf-d7e02e21c1ca','t55','',NULL,1),(130,1,'577486c2-bfdb-480d-8014-fae477371e7e','t56','',NULL,1),(131,1,'af88e0a2-e53e-4cb8-a172-a628b08465d0','t57','',NULL,1),(132,1,'49be4485-cbd8-4cb5-9054-dda79e3443b7','t58','',NULL,1),(133,1,'6dc83cd4-4175-427c-811a-391a3f871719','t59','',NULL,1),(134,1,'002c9965-97e1-4e46-b4b2-1a732b96b357','t60','',23,1),(135,1,'66eef4ac-d62a-47fe-a7c7-552830dd113a','t61','',24,1),(136,1,'278f94fa-56f0-4191-b0d4-13ccda66ddbb','t62','',NULL,1),(137,1,'d91205b8-5d43-486c-98bc-a9c003eb699b','t63','',NULL,1),(138,1,'79d5a257-e167-44bf-9cc3-a45351b6c4bf','t64','',25,1),(139,1,'81fcdf8e-5595-4f16-8718-d8a848c5a340','t65','',NULL,1),(140,1,'c9efc280-56d1-47b2-9213-d7bb56a2aa41','t66','',NULL,1),(141,1,'1bd08b9c-53b7-4383-8f0c-28b3c2fded5a','t67','',NULL,1),(142,1,'25b259c0-c776-448b-bdca-3df4d9a98347','t68','',26,1),(143,1,'b63d1898-65ba-426e-adca-bb1cc2fb9fae','t69','',NULL,1),(144,1,'29702e9e-9c6a-47bd-b1bf-39038b24a442','t70','',NULL,1),(145,1,'b6e00576-d34a-4a48-bc98-e25a05b23fca','t71','',NULL,1),(146,1,'3f0a06d6-7783-44e0-a6a0-ba8283845c04','t72','',NULL,1),(147,1,'2d8830f9-c97b-480d-a3f7-e5b2d8b61f0b','t73','',27,1),(148,1,'397b0d19-0593-4824-ac0b-7787ac293f90','t74','',28,1),(149,1,'47560cdd-04f9-4fe2-ba17-1cfb4a1533f7','t75','',NULL,1),(150,1,'ad913315-1138-467a-be2c-ba6093dcb73e','t76','',NULL,1),(151,1,'2e073c95-c028-4d2b-b83d-5a5cc3320942','t77','',29,1),(152,1,'7270b705-ae7c-4031-902b-56e8ffadeba7','t78','',30,1),(153,1,'f0fba2bf-247e-4e06-8161-c2e5e22b99f9','t79','',NULL,1),(154,1,'0a6bd54b-c1c2-42ae-b7f0-a17e2d206234','t80','',31,1),(155,1,'cf7237ae-8144-4bca-a70e-65b5b6f2b309','t81','',NULL,1),(156,1,'2654070d-78bf-46a5-85c2-1a9dcfc4e10b','t82','',NULL,1),(157,1,'ab5290ff-b971-41ed-8a34-70f1739840a6','t83','',NULL,1),(158,1,'b53e9eef-3d47-417f-975c-8092e7e2a3ec','t84','',NULL,1),(159,1,'44a7da67-f632-4923-bf73-873cce80f03e','t85','',NULL,1),(160,1,'7b10a58b-ac4e-419b-ad73-e1a24d4397d6','t86','',32,1);
/*!40000 ALTER TABLE `jbpt_petri_nodes` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `jbpt_petri_nodes_before_del_tr` BEFORE DELETE ON `jbpt_petri_nodes`
  FOR EACH ROW
BEGIN
  DELETE FROM jbpt_petri_markings WHERE jbpt_petri_markings.place_id=OLD.id;
  DELETE FROM jbpt_petri_flow WHERE jbpt_petri_flow.source=OLD.id OR jbpt_petri_flow.target=OLD.id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `pql_always_occurs`
--

DROP TABLE IF EXISTS `pql_always_occurs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_always_occurs` (
  `net_id` int(11) unsigned NOT NULL,
  `task_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`net_id`,`task_id`),
  KEY `net_id` (`net_id`),
  KEY `task_id` (`task_id`),
  CONSTRAINT `pql_always_occurs_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_always_occurs_fk1` FOREIGN KEY (`task_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_always_occurs`
--

LOCK TABLES `pql_always_occurs` WRITE;
/*!40000 ALTER TABLE `pql_always_occurs` DISABLE KEYS */;
/*!40000 ALTER TABLE `pql_always_occurs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pql_can_conflict`
--

DROP TABLE IF EXISTS `pql_can_conflict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_can_conflict` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `taskB_id` (`taskB_id`),
  CONSTRAINT `pql_can_conflict_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_can_conflict_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_can_conflict_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_can_conflict`
--

LOCK TABLES `pql_can_conflict` WRITE;
/*!40000 ALTER TABLE `pql_can_conflict` DISABLE KEYS */;
/*!40000 ALTER TABLE `pql_can_conflict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pql_can_cooccur`
--

DROP TABLE IF EXISTS `pql_can_cooccur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_can_cooccur` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `pql_can_cooccur_fk2` (`taskB_id`),
  CONSTRAINT `pql_can_cooccur_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_can_cooccur_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_can_cooccur_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_can_cooccur`
--

LOCK TABLES `pql_can_cooccur` WRITE;
/*!40000 ALTER TABLE `pql_can_cooccur` DISABLE KEYS */;
/*!40000 ALTER TABLE `pql_can_cooccur` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pql_can_occur`
--

DROP TABLE IF EXISTS `pql_can_occur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_can_occur` (
  `net_id` int(11) unsigned NOT NULL,
  `task_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`net_id`,`task_id`),
  KEY `net_id` (`net_id`),
  KEY `task_id` (`task_id`),
  CONSTRAINT `pql_can_occur_fk` FOREIGN KEY (`task_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_can_occurs_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_can_occur`
--

LOCK TABLES `pql_can_occur` WRITE;
/*!40000 ALTER TABLE `pql_can_occur` DISABLE KEYS */;
/*!40000 ALTER TABLE `pql_can_occur` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pql_index_bots`
--

DROP TABLE IF EXISTS `pql_index_bots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_index_bots` (
  `bot_name` varchar(36) NOT NULL,
  `last_alive` bigint(20) NOT NULL,
  PRIMARY KEY (`bot_name`,`last_alive`),
  UNIQUE KEY `bot_name` (`bot_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_index_bots`
--

LOCK TABLES `pql_index_bots` WRITE;
/*!40000 ALTER TABLE `pql_index_bots` DISABLE KEYS */;
INSERT INTO `pql_index_bots` VALUES ('43ae4f68-8fd2-4f55-9139-3c5fdcfbab92',1461800589);
/*!40000 ALTER TABLE `pql_index_bots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `pql_index_queue`
--

DROP TABLE IF EXISTS `pql_index_queue`;
/*!50001 DROP VIEW IF EXISTS `pql_index_queue`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `pql_index_queue` AS SELECT 
 1 AS `id`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `pql_index_status`
--

DROP TABLE IF EXISTS `pql_index_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_index_status` (
  `net_id` int(11) unsigned NOT NULL,
  `bot_name` varchar(36) NOT NULL,
  `status` tinyint(4) unsigned zerofill NOT NULL DEFAULT '0000',
  `type` tinyint(4) unsigned zerofill NOT NULL DEFAULT '0000' COMMENT 'index type:\r\n0 - store all behavioral relations',
  `claim_time` bigint(20) DEFAULT NULL,
  `start_time` bigint(20) DEFAULT NULL,
  `end_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`net_id`),
  UNIQUE KEY `net_id_2` (`net_id`),
  KEY `net_id` (`net_id`),
  CONSTRAINT `pql_index_status_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_index_status`
--

LOCK TABLES `pql_index_status` WRITE;
/*!40000 ALTER TABLE `pql_index_status` DISABLE KEYS */;
INSERT INTO `pql_index_status` VALUES (1,'43ae4f68-8fd2-4f55-9139-3c5fdcfbab92',0002,0000,1461800589,1461800589,1461800829);
/*!40000 ALTER TABLE `pql_index_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `pql_indexed_ids`
--

DROP TABLE IF EXISTS `pql_indexed_ids`;
/*!50001 DROP VIEW IF EXISTS `pql_indexed_ids`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `pql_indexed_ids` AS SELECT 
 1 AS `net_id`,
 1 AS `external_id`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `pql_tasks`
--

DROP TABLE IF EXISTS `pql_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_tasks` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `label_id` int(10) unsigned NOT NULL,
  `similarity` double(15,3) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `label_id_and_sim` (`label_id`,`similarity`),
  KEY `label_id` (`label_id`),
  CONSTRAINT `pql_tasks_fk` FOREIGN KEY (`label_id`) REFERENCES `jbpt_labels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_tasks`
--

LOCK TABLES `pql_tasks` WRITE;
/*!40000 ALTER TABLE `pql_tasks` DISABLE KEYS */;
INSERT INTO `pql_tasks` VALUES (1,1,00000000001.000),(2,2,00000000001.000),(3,3,00000000001.000),(4,4,00000000001.000),(5,5,00000000001.000),(6,6,00000000001.000),(7,7,00000000001.000),(8,8,00000000001.000),(9,9,00000000001.000),(10,10,00000000001.000),(11,11,00000000001.000),(12,12,00000000001.000),(13,13,00000000001.000),(14,14,00000000001.000),(15,15,00000000001.000),(16,16,00000000001.000),(17,17,00000000001.000),(18,18,00000000001.000),(19,19,00000000001.000),(20,20,00000000001.000),(21,21,00000000001.000),(22,22,00000000001.000),(23,23,00000000001.000),(24,24,00000000001.000),(25,25,00000000001.000),(26,26,00000000001.000),(27,27,00000000001.000),(28,28,00000000001.000),(29,29,00000000001.000),(30,30,00000000001.000),(31,31,00000000001.000),(32,32,00000000001.000),(33,33,00000000001.000);
/*!40000 ALTER TABLE `pql_tasks` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `pql_tasks_before_del_tr` BEFORE DELETE ON `pql_tasks`
  FOR EACH ROW
BEGIN
  DELETE FROM pql_tasks_sim WHERE pql_tasks_sim.task_id = OLD.id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `pql_tasks_sim`
--

DROP TABLE IF EXISTS `pql_tasks_sim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_tasks_sim` (
  `task_id` int(11) unsigned NOT NULL,
  `label_id` int(11) unsigned NOT NULL,
  UNIQUE KEY `task_and_label_ids` (`task_id`,`label_id`),
  KEY `label_id` (`label_id`),
  CONSTRAINT `pql_tasks_sim_fk_label_id` FOREIGN KEY (`label_id`) REFERENCES `jbpt_labels` (`id`),
  CONSTRAINT `pql_tasks_sim_fk_task_id` FOREIGN KEY (`task_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_tasks_sim`
--

LOCK TABLES `pql_tasks_sim` WRITE;
/*!40000 ALTER TABLE `pql_tasks_sim` DISABLE KEYS */;
INSERT INTO `pql_tasks_sim` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),(11,11),(12,12),(13,13),(14,14),(15,15),(16,16),(18,18),(19,19),(20,20),(21,21),(22,22),(23,23),(24,24),(25,25),(26,26),(27,27),(28,28),(29,29),(30,30),(31,31),(32,32),(17,33);
/*!40000 ALTER TABLE `pql_tasks_sim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pql_total_causal`
--

DROP TABLE IF EXISTS `pql_total_causal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_total_causal` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `taskB_id` (`taskB_id`),
  CONSTRAINT `pql_total_causal_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_total_causal_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_total_causal_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_total_causal`
--

LOCK TABLES `pql_total_causal` WRITE;
/*!40000 ALTER TABLE `pql_total_causal` DISABLE KEYS */;
/*!40000 ALTER TABLE `pql_total_causal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pql_total_concur`
--

DROP TABLE IF EXISTS `pql_total_concur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pql_total_concur` (
  `net_id` int(11) unsigned NOT NULL,
  `taskA_id` int(10) unsigned NOT NULL,
  `taskB_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`net_id`,`taskA_id`,`taskB_id`),
  KEY `net_id` (`net_id`),
  KEY `taskA_id` (`taskA_id`),
  KEY `taskB_id` (`taskB_id`),
  CONSTRAINT `pql_total_concurrent_fk` FOREIGN KEY (`net_id`) REFERENCES `jbpt_petri_nets` (`id`),
  CONSTRAINT `pql_total_concurrent_fk1` FOREIGN KEY (`taskA_id`) REFERENCES `pql_tasks` (`id`),
  CONSTRAINT `pql_total_concurrent_fk2` FOREIGN KEY (`taskB_id`) REFERENCES `pql_tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pql_total_concur`
--

LOCK TABLES `pql_total_concur` WRITE;
/*!40000 ALTER TABLE `pql_total_concur` DISABLE KEYS */;
/*!40000 ALTER TABLE `pql_total_concur` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'pql'
--
/*!50003 DROP FUNCTION IF EXISTS `jbpt_labels_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_labels_create`(label TEXT) RETURNS int(11)
    DETERMINISTIC
BEGIN
  DECLARE result INTEGER;
  
  SET label = TRIM(label);
  
  SELECT `jbpt_labels`.`id` INTO result
  FROM `jbpt_labels`
  WHERE `jbpt_labels`.`label`=label;
  
  IF result IS NOT NULL THEN
    RETURN result;
  END IF;

  INSERT INTO `jbpt_labels` (`jbpt_labels`.`label`)
  VALUES (label);

  SET result = LAST_INSERT_ID();

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_flow_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_flow_create`(source INTEGER(11), target INTEGER(11), name TEXT, description TEXT) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
  INSERT INTO `jbpt_petri_flow`
  (`jbpt_petri_flow`.`source`,`jbpt_petri_flow`.`target`,`jbpt_petri_flow`.`name`,`jbpt_petri_flow`.`description`)
  VALUES
  (source,target,name,description);
  
  RETURN TRUE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_markings_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_markings_create`(place_id INTEGER(11), tokens INTEGER(11)) RETURNS tinyint(1)
BEGIN
  INSERT INTO `jbpt_petri_markings`
  (`jbpt_petri_markings`.`place_id`,`jbpt_petri_markings`.`tokens`)
  VALUES
  (place_id,tokens);

  RETURN TRUE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_nets_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_nets_create`(uuid VARCHAR(36), name TEXT, description TEXT, external_id TEXT, pnml_content TEXT) RETURNS int(11)
BEGIN
  IF jbpt_petri_nets_get_internal_id(external_id) IS NOT NULL THEN
    RETURN 0;
  END IF;

  INSERT INTO `jbpt_petri_nets`
  (`jbpt_petri_nets`.`uuid`,`jbpt_petri_nets`.name,`jbpt_petri_nets`.description,`jbpt_petri_nets`.`external_id`,`jbpt_petri_nets`.`pnml_content`)
  VALUES
  (uuid,name,description,external_id,pnml_content);

  RETURN LAST_INSERT_ID();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_nets_delete` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_nets_delete`(internal_id INTEGER(11)) RETURNS int(11)
    DETERMINISTIC
BEGIN
  DECLARE delID INTEGER;
  
  SELECT id INTO delID FROM `jbpt_petri_nets` WHERE `jbpt_petri_nets`.`id` = internal_id;
  
  IF delID IS NULL THEN
    RETURN 0;
  END IF;

  DELETE FROM `jbpt_petri_nets` WHERE `jbpt_petri_nets`.`id` = delID;

  RETURN delID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_nets_get_external_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_nets_get_external_id`(internal_id INTEGER(11)) RETURNS text CHARSET utf8
BEGIN
  DECLARE result TEXT;

  SELECT `jbpt_petri_nets`.`external_id` INTO result
  FROM `jbpt_petri_nets`
  WHERE `jbpt_petri_nets`.`id`=internal_id;

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_nets_get_internal_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_nets_get_internal_id`(external_id TEXT) RETURNS int(11)
    DETERMINISTIC
BEGIN
  DECLARE result INTEGER;

  SELECT `jbpt_petri_nets`.`id` INTO result
  FROM `jbpt_petri_nets`
  WHERE `jbpt_petri_nets`.`external_id`=external_id;
    
  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_nets_get_pnml_content` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_nets_get_pnml_content`(internal_id INTEGER(11)) RETURNS text CHARSET utf8
BEGIN
  DECLARE result TEXT;

  SELECT `jbpt_petri_nets`.`pnml_content` INTO result
  FROM `jbpt_petri_nets`
  WHERE `jbpt_petri_nets`.`id`=internal_id;

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `jbpt_petri_nodes_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `jbpt_petri_nodes_create`(net_id INTEGER(11), uuid VARCHAR(100), name TEXT, description TEXT, label TEXT, is_transition BOOLEAN) RETURNS int(11)
BEGIN
  DECLARE labelID INTEGER;
  
  IF label = "" THEN
    SET labelID = NULL;
  ELSE
    SET labelID = jbpt_labels_create(label);
  END IF;

  INSERT INTO `jbpt_petri_nodes`
  (`jbpt_petri_nodes`.`net_id`,`jbpt_petri_nodes`.`uuid`,`jbpt_petri_nodes`.name,`jbpt_petri_nodes`.description,`jbpt_petri_nodes`.label_id,`jbpt_petri_nodes`.is_transition)
  VALUES
  (net_id,uuid,name,description,labelID,is_transition);

  RETURN LAST_INSERT_ID();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_always_occurs` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_always_occurs`(net_id INTEGER(11), task_id INTEGER) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
  IF EXISTS(SELECT 1 FROM `pql_always_occurs` WHERE `pql_always_occurs`.`net_id`=net_id AND `pql_always_occurs`.`task_id`=task_id LIMIT 1) THEN
    RETURN true;
  END IF;

  RETURN false;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_can_conflict` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_can_conflict`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
  IF EXISTS(SELECT 1 FROM `pql_can_conflict` WHERE `pql_can_conflict`.`net_id`=net_id AND `pql_can_conflict`.`taskA_id`=taskA_id AND `pql_can_conflict`.`taskB_id`=taskB_id LIMIT 1) THEN
    RETURN true;
  END IF;

  RETURN false;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_can_cooccur` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_can_cooccur`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
  IF EXISTS(SELECT 1 FROM `pql_can_cooccur` WHERE `pql_can_cooccur`.`net_id`=net_id AND `pql_can_cooccur`.`taskA_id`=taskA_id AND `pql_can_cooccur`.`taskB_id`=taskB_id LIMIT 1) THEN
    RETURN true;
  END IF;

  RETURN false;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_can_occur` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_can_occur`(net_id INTEGER(11), task_id INTEGER) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
  IF EXISTS(SELECT 1 FROM `pql_can_occur` WHERE `pql_can_occur`.`net_id`=net_id AND `pql_can_occur`.`task_id`=task_id LIMIT 1) THEN
    RETURN true;
  END IF;

  RETURN false;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_bots_is_alive` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_bots_is_alive`(bot_name VARCHAR(36)) RETURNS tinyint(4)
BEGIN
  DECLARE result bool DEFAULT FALSE;

  SELECT EXISTS(
  SELECT
    *
  FROM
    `pql_index_bots`
  WHERE
    `pql_index_bots`.`bot_name` = TRIM(bot_name)
  ) INTO result;

  IF result THEN
    UPDATE `pql_index_status` SET `pql_index_status`.`start_time` = UNIX_TIMESTAMP()
    WHERE `pql_index_status`.`net_id` = net_id AND
    `pql_index_status`.`bot_name` = bot_name;
  END IF;

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_delete` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_delete`(internal_id INTEGER(11)) RETURNS tinyint(1)
BEGIN
  DECLARE delID INTEGER;

  SELECT net_id INTO delID FROM `pql_index_status` WHERE `pql_index_status`.`net_id` = internal_id;

  IF delID IS NULL THEN
    RETURN FALSE;
  END IF;

  DELETE FROM `pql_index_status` WHERE `pql_index_status`.`net_id` = delID;

  RETURN TRUE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_delete_indexed_relations` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_delete_indexed_relations`(internal_id INTEGER(11)) RETURNS tinyint(1)
BEGIN
  
  DELETE FROM `pql_always_occurs` WHERE `pql_always_occurs`.`net_id` = internal_id;
  DELETE FROM `pql_can_conflict` WHERE `pql_can_conflict`.`net_id` = internal_id;
  DELETE FROM `pql_can_cooccur` WHERE `pql_can_cooccur`.`net_id` = internal_id;
  DELETE FROM `pql_can_occur` WHERE `pql_can_occur`.`net_id` = internal_id;
  DELETE FROM `pql_total_causal` WHERE `pql_total_causal`.`net_id` = internal_id;
  DELETE FROM `pql_total_concur` WHERE `pql_total_concur`.`net_id` = internal_id;

  RETURN TRUE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_get_end_time` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_get_end_time`(internal_id INTEGER) RETURNS int(11)
    DETERMINISTIC
BEGIN
DECLARE indexend INTEGER;

  SELECT `pql_index_status`.`end_time` INTO indexend
  FROM `pql_index_status`
  WHERE `pql_index_status`.`net_id`=internal_id;

RETURN indexend;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_get_next_job` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_get_next_job`() RETURNS int(11)
BEGIN
  DECLARE result INTEGER;

  SELECT id INTO result FROM `pql_index_queue` LIMIT 0,1;

  IF result IS NULL THEN RETURN 0; END IF;
  
  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_get_start_time` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_get_start_time`(internal_id INTEGER) RETURNS int(11)
    DETERMINISTIC
BEGIN
DECLARE indexstart INTEGER;

  SELECT `pql_index_status`.`start_time` INTO indexstart
  FROM `pql_index_status`
  WHERE `pql_index_status`.`net_id`=internal_id;

RETURN indexstart;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_get_status` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_get_status`(internal_id INTEGER(11)) RETURNS tinyint(4)
BEGIN
  DECLARE result TINYINT(4);

  SELECT `pql_index_status`.`status` INTO result
  FROM `pql_index_status` WHERE `pql_index_status`.`net_id`=internal_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_get_type` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_get_type`(internal_id INTEGER(11)) RETURNS tinyint(4)
BEGIN
  DECLARE result TINYINT(4);

  SELECT `pql_index_status`.`type` INTO result
  FROM `pql_index_status` WHERE `pql_index_status`.`net_id`=internal_id;

  IF result IS NULL THEN
    RETURN -1;
  END IF;

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_start_job` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_start_job`(net_id INTEGER(11), bot_name VARCHAR(36)) RETURNS tinyint(1)
BEGIN
  DECLARE result bool DEFAULT FALSE;

  SELECT EXISTS(
  SELECT
    *
  FROM
    `pql_index_status`
  WHERE
    `pql_index_status`.`net_id` = net_id AND
    `pql_index_status`.`bot_name` = bot_name AND
    `pql_index_status`.`status` = 0
  ) INTO result;

  IF result THEN
    UPDATE `pql_index_status` SET `pql_index_status`.`start_time` = UNIX_TIMESTAMP()
    WHERE `pql_index_status`.`net_id` = net_id AND
    `pql_index_status`.`bot_name` = bot_name;
  END IF;

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_index_time` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_index_time`(internal_id INTEGER) RETURNS int(11)
    DETERMINISTIC
BEGIN
DECLARE indexstart INTEGER;
DECLARE indexend INTEGER;

  SELECT `pql_index_status`.`start_time` INTO indexstart
  FROM `pql_index_status`
  WHERE `pql_index_status`.`net_id`=internal_id;

  SELECT `pql_index_status`.`end_time` INTO indexend
  FROM `pql_index_status`
  WHERE `pql_index_status`.`net_id`=internal_id;


RETURN indexend-indexstart;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_levenshtein` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_levenshtein`(s1 TEXT, s2 TEXT) RETURNS int(11)
    DETERMINISTIC
BEGIN
    
    
    
    DECLARE s1_len, s2_len, i, j, c, c_temp, cost INT;
    DECLARE s1_char CHAR;
    DECLARE cv0, cv1 TEXT; 
    SET s1_len = CHAR_LENGTH(s1), s2_len = CHAR_LENGTH(s2), cv1 = 0x00, j = 1, i = 1, c = 0;
    IF s1 = s2 THEN
      RETURN 0;
    ELSEIF s1_len = 0 THEN
      RETURN s2_len;
    ELSEIF s2_len = 0 THEN
      RETURN s1_len;
    ELSE
      WHILE j <= s2_len DO
        SET cv1 = CONCAT(cv1, UNHEX(HEX(j))), j = j + 1;
      END WHILE;
      WHILE i <= s1_len DO
        SET s1_char = SUBSTRING(s1, i, 1), c = i, cv0 = UNHEX(HEX(i)), j = 1;
        WHILE j <= s2_len DO
          SET c = c + 1;
          IF s1_char = SUBSTRING(s2, j, 1) THEN
            SET cost = 0; ELSE SET cost = 1;
          END IF;
          SET c_temp = CONV(HEX(SUBSTRING(cv1, j, 1)), 16, 10) + cost;
          IF c > c_temp THEN SET c = c_temp; END IF;
          SET c_temp = CONV(HEX(SUBSTRING(cv1, j+1, 1)), 16, 10) + 1;
          IF c > c_temp THEN
            SET c = c_temp;
          END IF;
          SET cv0 = CONCAT(cv0, UNHEX(HEX(c))), j = j + 1;
        END WHILE;
        SET cv1 = cv0, i = i + 1;
      END WHILE;
    END IF;
    RETURN c;
  END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_tasks_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_tasks_create`(label TEXT, similarity DOUBLE) RETURNS int(11)
    DETERMINISTIC
BEGIN
  DECLARE result INTEGER;
  DECLARE labelID INTEGER;

  SET labelID = jbpt_labels_create(label);
    
  SELECT `pql_tasks`.`id` INTO result
  FROM `pql_tasks`
  WHERE `pql_tasks`.`label_id`=labelID AND `pql_tasks`.`similarity`=similarity;

  IF result IS NOT NULL THEN
    RETURN result;
  END IF;

  INSERT INTO `pql_tasks` (`pql_tasks`.`label_id`,`pql_tasks`.`similarity`)
  VALUES (labelID,similarity);

  SET result = LAST_INSERT_ID();

  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_tasks_get` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_tasks_get`(label TEXT, sim DOUBLE, threshold DOUBLE) RETURNS int(11)
    DETERMINISTIC
BEGIN
  DECLARE labelID INTEGER;
  DECLARE result INTEGER;
  
  SET labelID = jbpt_labels_create(label);

  SELECT id INTO result
  FROM
  (SELECT id,label_id,ABS(similarity-sim) AS distance
  FROM pql_tasks
  WHERE label_id=labelID AND ABS(similarity-sim)<threshold
  ORDER BY distance ASC
  LIMIT 0,1) AS tbl;
  
  IF result IS NULL THEN
    RETURN 0;
  END IF;
  
  RETURN result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_tasks_sim_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_tasks_sim_create`(labelA TEXT, labelB TEXT, similarity DOUBLE) RETURNS int(11)
    DETERMINISTIC
BEGIN
  DECLARE labelAid INTEGER;
  DECLARE labelBid INTEGER;
  DECLARE taskID INTEGER;
  DECLARE result INTEGER;

  SET labelAid = jbpt_labels_create(labelA);
  SET labelBid = jbpt_labels_create(labelB);
  
  SET taskID = pql_tasks_create(labelA,similarity);

  SELECT `pql_tasks_sim`.`task_id` INTO result
  FROM `pql_tasks_sim`
  WHERE `pql_tasks_sim`.`task_id`=taskID AND `pql_tasks_sim`.`label_id`=labelBid;
  
  IF result IS NOT NULL THEN
    RETURN 0;
  END IF;
  
  INSERT INTO `pql_tasks_sim` (`pql_tasks_sim`.`task_id`,`pql_tasks_sim`.`label_id`)
  VALUES (taskID,labelBid);

  RETURN taskID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_total_causal` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_total_causal`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
  IF EXISTS(SELECT 1 FROM `pql_total_causal` WHERE `pql_total_causal`.`net_id`=net_id AND `pql_total_causal`.`taskA_id`=taskA_id AND `pql_total_causal`.`taskB_id`=taskB_id LIMIT 1) THEN
    RETURN true;
  END IF;

  RETURN false;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `pql_total_concur` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `pql_total_concur`(net_id INTEGER(11), taskA_id INTEGER, taskB_id INTEGER) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
  IF EXISTS(SELECT 1 FROM `pql_total_concur` WHERE `pql_total_concur`.`net_id`=net_id AND `pql_total_concur`.`taskA_id`=taskA_id AND `pql_total_concur`.`taskB_id`=taskB_id LIMIT 1) THEN
    RETURN true;
  END IF;

  RETURN false;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `jbpt_get_net_labels` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `jbpt_get_net_labels`(IN identifier TEXT)
BEGIN
  DECLARE nid INTEGER;
  
  SELECT id INTO nid FROM jbpt_petri_nets WHERE jbpt_petri_nets.`external_id`=identifier;
  
  SELECT DISTINCT `jbpt_labels`.`label`
  FROM `jbpt_labels`, `jbpt_petri_nodes`
  WHERE `jbpt_petri_nodes`.`net_id`=nid AND
        `jbpt_petri_nodes`.`label_id` IS NOT NULL AND
        `jbpt_labels`.`id` = `jbpt_petri_nodes`.`label_id`;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `jbpt_get_net_labels_ext_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `jbpt_get_net_labels_ext_id`(IN identifier TEXT)
    DETERMINISTIC
BEGIN
  DECLARE nid INTEGER;
  
  SELECT id INTO nid FROM jbpt_petri_nets WHERE jbpt_petri_nets.`external_id`=identifier;
  
  SELECT `jbpt_labels`.`label`
  FROM `jbpt_labels`, `jbpt_petri_nodes`
  WHERE `jbpt_petri_nodes`.`net_id`=nid AND
        `jbpt_petri_nodes`.`label_id` IS NOT NULL AND
        `jbpt_labels`.`id` = `jbpt_petri_nodes`.`label_id`;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `jbpt_get_net_labels_int_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `jbpt_get_net_labels_int_id`(IN id INTEGER(11))
    DETERMINISTIC
BEGIN
  SELECT `jbpt_labels`.`label`
  FROM `jbpt_labels`, `jbpt_petri_nodes`
  WHERE `jbpt_petri_nodes`.`net_id`=id AND
        `jbpt_petri_nodes`.`is_transition`=1 AND
        `jbpt_labels`.`id` = `jbpt_petri_nodes`.`label_id`;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `jbpt_petri_nets_get_internal_ids` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `jbpt_petri_nets_get_internal_ids`()
BEGIN
  SELECT `jbpt_petri_nets`.`id`
  FROM `jbpt_petri_nets`;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_always_occurs_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_always_occurs_create`(IN net_id INTEGER(11), IN task_id INTEGER)
    DETERMINISTIC
BEGIN
  INSERT IGNORE INTO `pql_always_occurs`
  (`pql_always_occurs`.`net_id`,`pql_always_occurs`.`task_id`)
  VALUES
  (net_id,task_id);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_can_conflict_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_can_conflict_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER)
    DETERMINISTIC
BEGIN
  INSERT IGNORE INTO `pql_can_conflict`
  (`pql_can_conflict`.`net_id`,`pql_can_conflict`.`taskA_id`,`pql_can_conflict`.`taskB_id`)
  VALUES (net_id,taskA_id,taskB_id);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_can_cooccur_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_can_cooccur_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER)
    DETERMINISTIC
BEGIN
  INSERT IGNORE INTO `pql_can_cooccur`
  (`pql_can_cooccur`.`net_id`,`pql_can_cooccur`.`taskA_id`,`pql_can_cooccur`.`taskB_id`)
  VALUES (net_id,taskA_id,taskB_id);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_can_occur_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_can_occur_create`(IN net_id INTEGER(11), IN task_id INTEGER)
    DETERMINISTIC
BEGIN
  INSERT IGNORE INTO `pql_can_occur`
  (`pql_can_occur`.`net_id`,`pql_can_occur`.`task_id`)
  VALUES
  (net_id,task_id);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_check_binary_predicate_macro` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_check_binary_predicate_macro`(IN netID int, IN op TEXT, IN q TEXT, IN set1 json, IN set2 json)
    DETERMINISTIC
BEGIN
       
  SET @size1 = JSON_LENGTH(set1);
  SET @size2 = JSON_LENGTH(set2);
  SET @ids1 = SUBSTRING(set1,2,LENGTH(set1)-2);
  SET @ids2 = SUBSTRING(set2,2,LENGTH(set2)-2);
  
  IF op='cancooccur' THEN SET @tbl='pql_can_cooccur';
  ELSEIF op='canconflict' THEN SET @tbl='pql_can_conflict'; 
  ELSEIF op='totalcausal' THEN SET @tbl='pql_total_causal'; 
  ELSE SET @tbl='pql_total_concur'; 
  END IF;
  
  IF q='any' OR q='all' THEN
  SET @query = CONCAT('select count(*) INTO @count FROM ',@tbl,' WHERE net_id=',netID,' AND taskA_id IN (',@ids1,') AND taskB_id IN (',@ids2,')');
  ELSE 
  SET @query = CONCAT('select count(distinct taskA_id) INTO @count FROM ',@tbl,' WHERE net_id=',netID,' AND taskA_id IN (',@ids1,') AND taskB_id IN (',@ids2,')');
  END IF;
 
  PREPARE stmt FROM @query;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
  
  IF q='any' AND @count>0 OR q='each' AND @count=@size1 OR q='all' AND @count=@size1*@size2 THEN SET @result=TRUE; 
  ELSE SET @result=FALSE; END IF;
  
  SELECT @result;
       
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_check_unary_predicate_macro` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_check_unary_predicate_macro`(IN netID int, IN op TEXT, IN q TEXT, IN ids json)
    DETERMINISTIC
BEGIN
       
  SET @size = JSON_LENGTH(ids);
  SET @idsLine = SUBSTRING(ids,2,LENGTH(ids)-2);
  
  IF op='canoccur' THEN SET @tbl='pql_can_occur';
  ELSE SET @tbl='pql_always_occurs'; END IF;
  
  SET @query = CONCAT('select count(*) INTO @count FROM ',@tbl,' WHERE net_id=',netID,' AND task_id IN (',@idsLine,')');
  PREPARE stmt FROM @query;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
  
  IF q='any' AND @count>0 OR q='all' AND @count=@size THEN SET @result=TRUE; 
  ELSE SET @result=FALSE; END IF;
  
  SELECT @result;
       
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_get_indexed_ids` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_get_indexed_ids`()
BEGIN
  SELECT * FROM `pql_indexed_ids`;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_get_task_ids` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_get_task_ids`(IN l json, s json)
BEGIN
  DECLARE max INTEGER;
  DECLARE counter INTEGER;
  DECLARE path TEXT;
    
  SET counter = 0;
  SET max = JSON_LENGTH(l)-1;
  
  SET @query = 'SELECT pql_tasks.id, pql_tasks.similarity, jbpt_labels.label FROM pql_tasks, jbpt_labels
					WHERE pql_tasks.label_id=jbpt_labels.id AND (';
					
     
  while counter < max do
	SET path = CONCAT('$[', counter, ']');
	SET @label = JSON_EXTRACT(l, path);
    SET @sim = JSON_EXTRACT(s, path);
    
    SET @query = CONCAT(@query,	'jbpt_labels.label=',@label,' AND pql_tasks.similarity=',@sim,' OR ');          	
     
    SET counter=counter+1;
  end while;
  
  SET path = CONCAT('$[', max, ']');
  SET @label = JSON_EXTRACT(l, path);
  SET @sim = JSON_EXTRACT(s, path);
    
  SET @query = CONCAT(@query,	'jbpt_labels.label=',@label,' AND pql_tasks.similarity=',@sim,')');          	
  
  PREPARE stmt FROM @query;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_index_bots_alive` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_index_bots_alive`(IN bot_name VARCHAR(36))
BEGIN
  DECLARE bname VARCHAR(36);

  SELECT `pql_index_bots`.`bot_name` INTO bname
  FROM `pql_index_bots`
  WHERE `pql_index_bots`.`bot_name` = TRIM(bot_name);

  IF bname IS NULL THEN
    INSERT INTO `pql_index_bots` (`pql_index_bots`.`bot_name`,`pql_index_bots`.`last_alive`)
    VALUES (TRIM(bot_name),UNIX_TIMESTAMP());
  ELSE
    UPDATE `pql_index_bots`
    SET `pql_index_bots`.`last_alive`=UNIX_TIMESTAMP()
    WHERE `pql_index_bots`.`bot_name` = bname;
  END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_index_cannot` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_index_cannot`(IN net_id INTEGER(11))
BEGIN

UPDATE `pql_index_status`
  SET `pql_index_status`.`status`=2, `pql_index_status`.`end_time`=UNIX_TIMESTAMP()
WHERE `pql_index_status`.`net_id`=net_id;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_index_claim_job` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_index_claim_job`(IN net_id INTEGER(11), IN bot_name VARCHAR(36))
BEGIN

  INSERT INTO `pql_index_status` (`pql_index_status`.`net_id`, `pql_index_status`.`bot_name`, `pql_index_status`.`claim_time`)
  VALUES (net_id,bot_name,UNIX_TIMESTAMP());

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_index_cleanup` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_index_cleanup`()
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE bname VARCHAR(36);

  DECLARE cur1 CURSOR FOR
  SELECT `pql_index_bots`.`bot_name`
  FROM `pql_index_bots`
  WHERE (UNIX_TIMESTAMP()-`pql_index_bots`.`last_alive`)>(3600*5);

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN cur1;

  read_loop: LOOP

    FETCH cur1 INTO bname;
  
    IF done THEN
      LEAVE read_loop;
    END IF;

    DELETE FROM `pql_index_status`
    WHERE `pql_index_status`.`bot_name`=bname AND `pql_index_status`.`status`<1;
  
    DELETE FROM `pql_index_bots`
    WHERE `pql_index_bots`.`bot_name` = bname;
    
  END LOOP;

  CLOSE cur1;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_index_finish_job` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_index_finish_job`(IN net_id INTEGER(11), IN bot_name VARCHAR(36))
BEGIN
  UPDATE `pql_index_status`
  SET
    `pql_index_status`.`end_time` = UNIX_TIMESTAMP(),
    `pql_index_status`.`status` = 1
  WHERE
    `pql_index_status`.`net_id` = net_id AND
    `pql_index_status`.`bot_name` = bot_name;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_levenshtein_label_sim_search` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_levenshtein_label_sim_search`(IN label TEXT)
BEGIN
  DECLARE x TEXT;
  SET x = TRIM(LOWER(label));
  SELECT jbpt_labels.`label`, 1-`pql_levenshtein`(x,TRIM(LOWER(jbpt_labels.`label`)))/(2*GREATEST(CHAR_LENGTH(x),CHAR_LENGTH(TRIM(LOWER(jbpt_labels.`label`))))) AS sim
  FROM jbpt_labels ORDER BY sim DESC LIMIT 0,10;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_tasks_get_in_net` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_tasks_get_in_net`(IN net_id INTEGER(11))
BEGIN
 SELECT DISTINCT `pql_tasks_sim`.`task_id`
  FROM `pql_tasks_sim`
  WHERE label_id IN
  (
    SELECT label_id
    FROM `jbpt_petri_nets`
    WHERE `jbpt_petri_nets`.`id`=net_id
  );
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_tasks_get_nets` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_tasks_get_nets`(IN task_id INTEGER)
BEGIN
  SELECT DISTINCT net_id
  FROM `jbpt_petri_nodes`, `pql_tasks_sim`
  WHERE `jbpt_petri_nodes`.`is_transition` IS TRUE AND
    `jbpt_petri_nodes`.`label_id`=`pql_tasks_sim`.`label_id` AND
    `pql_tasks_sim`.`task_id`=task_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_tasks_get_sim` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_tasks_get_sim`(IN task_id INTEGER(11))
BEGIN
  SELECT DISTINCT `jbpt_labels`.`label`
  FROM `pql_tasks_sim`,`jbpt_labels`
  WHERE `pql_tasks_sim`.`label_id`=`jbpt_labels`.`id` AND `pql_tasks_sim`.`task_id` = task_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_total_causal_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_total_causal_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER)
    DETERMINISTIC
BEGIN
  INSERT IGNORE INTO `pql_total_causal`
  (`pql_total_causal`.`net_id`,`pql_total_causal`.`taskA_id`,`pql_total_causal`.`taskB_id`)
  VALUES (net_id,taskA_id,taskB_id);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `pql_total_concur_create` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pql_total_concur_create`(IN net_id INTEGER(11), IN taskA_id INTEGER, IN taskB_id INTEGER)
    DETERMINISTIC
BEGIN
  INSERT IGNORE INTO `pql_total_concur`
  (`pql_total_concur`.`net_id`,`pql_total_concur`.`taskA_id`,`pql_total_concur`.`taskB_id`)
  VALUES (net_id,taskA_id,taskB_id);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `reset` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `reset`()
    DETERMINISTIC
    SQL SECURITY INVOKER
BEGIN
  DELETE FROM `jbpt_petri_nets`;
  
  DELETE FROM `pql_tasks` WHERE `pql_tasks`.`label_id` NOT IN
  (SELECT `jbpt_petri_nodes`.`label_id` FROM `jbpt_petri_nodes`);
  
  DELETE FROM jbpt_labels WHERE `jbpt_labels`.`id` NOT IN
  (SELECT `jbpt_petri_nodes`.`label_id` FROM `jbpt_petri_nodes`);
  
  ALTER TABLE jbpt_petri_nets  AUTO_INCREMENT = 1;
  ALTER TABLE jbpt_petri_nodes AUTO_INCREMENT = 1;
  ALTER TABLE jbpt_labels      AUTO_INCREMENT = 1;
  ALTER TABLE pql_tasks        AUTO_INCREMENT = 1;
  
  DELETE FROM `pql_index_bots`;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `pql_index_queue`
--

/*!50001 DROP VIEW IF EXISTS `pql_index_queue`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `pql_index_queue` AS select `jbpt_petri_nets`.`id` AS `id` from `jbpt_petri_nets` where (not(`jbpt_petri_nets`.`id` in (select `pql_index_status`.`net_id` AS `id` from `pql_index_status`))) order by `jbpt_petri_nets`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `pql_indexed_ids`
--

/*!50001 DROP VIEW IF EXISTS `pql_indexed_ids`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `pql_indexed_ids` AS select `pql_index_status`.`net_id` AS `net_id`,`jbpt_petri_nets`.`external_id` AS `external_id` from (`jbpt_petri_nets` join `pql_index_status`) where ((`pql_index_status`.`net_id` = `jbpt_petri_nets`.`id`) and (`pql_index_status`.`status` = 1)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-28 10:20:57
