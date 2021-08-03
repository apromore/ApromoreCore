![apromore](http://apromore.org/wp-content/uploads/2019/11/Apromore-banner_narrow.png "apromore")

# Apromore Core

This repository contains source code of the [Apromore](https://apromore.org) Core process analytics web application server.  It can be built and run on its own, or used as a submodule containing components common to the two other Apromore editions:

* [Apromore Community Edition](https://github.com/apromore/ApromoreCE), which is open source.
* [Apromore Enterprise Edition](https://github.com/apromore/ApromoreEE), which is proprietary.

The instructions below are for the installation of Apromore Core from the source code. For convenience, we also make available a containerized image in [Docker](https://github.com/apromore/ApromoreDocker).
If you are looking for the commercial edition (Apromore Enterprise Edition), check the [Apromore web site](https://apromore.org/)

## System requirements
* Linux Ubuntu 20.04 (We do not support newer versions as it may lead to dependency issues), Windows 10/WS2016/WS2019, Mac OSX 10.8 or newer.
* Java SE 11 ["Server JRE"](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or
  ["JDK 11"](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) . For Ubuntu, it can be installed as `sudo apt install openjdk-11-jdk`
 
 
* Gradlew Wrapper, included in the source code. It will download automatically on use of gradlew wrapper command.
* (optional) [MySQL server](https://dev.mysql.com/downloads/mysql/) 8.0.
* <b>Note:</b> These instructions are tested with Linux Ubuntu 20.04. With minor adaptations, these instructions may be used for Windows 10/WS20016/WS2019 and macOS 10.8 or newer.

## Installation Instructions

* Check out the source code using git: `git clone https://github.com/apromore/ApromoreCore.git`
* Switch to the ApromoreCore directory: `cd ApromoreCore`
* Check out the desired branch or tag: `git checkout development`
* Execute `./gradlew bu bR --args='--spring.profiles.active=dev'` to compile the source code and run the project using in memory database.
* Browse [(http://localhost:8181/)](http://localhost:8181/). Login as an administrator by using the following credentials: username - "admin" and password - "password". You can also create a new account. Once logged in, a user can change their password via `Account -> Change password` menu.
* Keep the prompt/terminal window open. Ctrl-C on the window will shut the server down.


## Configuration
The following configuration options apply to all editions of Apromore.
When there are additional configurations specific to a particular edition, they are documented in that edition's own README file.
Almost all configuration occurs in the `application.properties` file which is located in the `ApromoreCore/Apromore-Boot/src/main/resources` directory.
The default version of this file from a fresh git checkout contains reasonable defaults that allow the server to be started without any manual configuration.


### MySQL setup
By default, Apromore Core uses MySQL database. For casual evaluation, Apromore can also be used with H2.

* Ensure MySQL is configured to accept local TCP connections on port 3306 in its .cnf file; "skip-networking" should not be present.

* Create a database named 'apromore' in your MySQL server. Also create 2 user accounts which use the Apromore application.
* You will be prompted to enter the root password of MySQL

```bash
mysql -u root -p
CREATE DATABASE apromore CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE USER 'apromore'@'%' IDENTIFIED BY 'Apromore#!dev';
GRANT SELECT, INSERT, UPDATE, DELETE, LOCK TABLES, EXECUTE, SHOW VIEW ON apromore.* TO 'apromore'@'%';

CREATE USER 'liquibase_user'@'%' IDENTIFIED BY 'Apromore#!dev';
GRANT ALL PRIVILEGES ON apromore.* TO 'liquibase_user'@'%';
	
```

* Verify the database configuration in application.poperties file in directory 'ApromoreCore/Apromore-Boot/src/main/resources/application.properties'
* Execute `./gradlew bu bR` to compile the source code and run the project using MYSQL database.


### Heap size
Memory limits are set using the usual JVM parameters.

The startup options must be set in the `JAVA_OPTS` environment variable.

On Windows:

```dos
set "JAVA_OPTS= -server -Xms20g -Xmx20g"
```

On unix-style systems:

```bash
export JAVA_OPTS="-server -Xms20g -Xmx20g"
```


### Cache size
Apromore uses [Ehcache](https://www.ehcache.org/) for internal caching, which uses an XML configuration file.
The default in a deployed server is that the `ehcache.xml` configuration file is located at `ApromoreCore/ApromoreBoot/src/main/resoiurces`.
The manager.ehcache.config.url property in application.properties can be used to point to an `ehcache.xml` at a URL of your choice.


### Backup and restore
Apromore stores its data objects in two places:
* Database: all data, except the event logs
* Event logs which are by default located in the top-level `Event-Logs-Repository` directory

As such, both need to be backed up and restored.
* To backup a H2 database, it is enough to copy across the `Manager-Repository.h2.db` file
* To backup a MySQL database, the following command may be used  (If prompted for password, enter the password of the ‘apromore’ user i.e ‘MAcri’):
```bash
mysqldump -u root -p apromore > backup.sql
```

To backup only one table (rather than the whole database), the following command may be used:
```bash
mysqldump -u -p apromore tablename > tablename.sql
```

To restore, use
```bash
mysql --max_allowed_packet=1G --user=root -p -h localhost apromore < backup.sql
```

* For the event logs directory, it is recommended to zip the directory before copying it across


### KeyCloak setup

* Apromore supports keycloak as authentication broker which acts as IDP provider.
* You can configure your own keycloak instance with SAML,IDP,LDAP authentication.
* Once done edit the application.properties with relevant information (keycloak. prefix) that you get from keycloak.
* Apromore will use keycloak for authentication.


## Change Port Number (optional)
* Change the default port number by changing the value of `server.port` variable in the `application.properties` file .
* Or `./gradlew bR --args='--server.port=8282'` to run on port 8282 as an example.

### Share file to all users (optional)

* By default Apromore does not allow you to share a file with all users (i.e. the "public" group is not supported by default). You can change this by editing the application.properties file present . Specifically, to enable the option to share files and folders with the “public” group, you should set `security.publish.enable = true` in the application.properties file.


## Common problems

> Server fails to start.

* If Apromore is configured to use MySQL, confirm that the database server is running.
* If you already run another server (e.g. OS X Server) you may need to change the port number.

> Can't view models by clicking them in the summary list.

* Model diagrams are opened in new tabs/windows; you may need to disable popup blocking for Apromore in your browser settings.

> Where is the server log?

* Check the logging file location in application.properties. The field 'logging.file.name'
* Default location is '${user_home}/.apromore/logs/apromore.log'
