![apromore](http://apromore.org/wp-content/uploads/2019/11/Apromore-banner_narrow.png "apromore")
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fapromore%2FApromoreCore.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fapromore%2FApromoreCore?ref=badge_shield)

# Apromore Community Edition

This repository contains source code of [Apromore](https://apromore.org) Community Edition. Suitable for developers and advanced users, it allows one to fully configure Apromore (e.g. you can choose to use the H2 or MySQL database, the plugins you want to install, configure LDAP access and Apromore Portal’s URL, etc.). It includes the core platform and all the experimental plugins built by the community. You can either build this edition locally or run it from our pubic nodes in [Australia](http://apromore.cis.unimelb.edu.au/) and [Estonia](http://apromore.cs.ut.ee/).


## System Requirements
* Windows 7 or newer or Mac OSX 10.8 to 10.11 (other users - check out our [Docker-based version](https://github.com/apromore/ApromoreDocker))
* Java SE 8 ["Server JRE"](https://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html) or ["JDK"](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) Edition 1.8. Note newer versions, including Java SE 11, are currently not supported
* [Apache Maven](https://maven.apache.org/download.cgi) 3.5.2 or newer
* [Apache Ant](https://ant.apache.org/bindownload.cgi) 1.10.1 or newer
* [Lessc](http://lesscss.org/usage/) 3.9.0 or newer
* (optional) [MySQL server](https://dev.mysql.com/downloads/mysql/5.7.html) 5.6 or 5.7. Note version 8.0 is currently not supported


## Installation instructions
* Download and unzip the latest [Apromore release](https://github.com/apromore/ApromoreCore/releases/latest) or check out the source code using git: `git clone https://github.com/apromore/ApromoreCore.git`
* Open command prompt/terminal and change to the root of the project `cd ApromoreCore`
* Run the maven command `mvn clean install`.  This will build the Apromore manager, portal and editor and all the extra plugins.
* Create an empty H2 database `ant create-h2`.  Only do this once, unless you just want to reset to a blank database later on.
* Run the ant command `ant start-virgo`.  This will install, configure and start Eclipse Virgo, and deploy Apromore.
* Open a web browser to [http://localhost:9000](http://localhost:9000). Use "admin”/“password” to access as administrator, or create a new account.
* Keep the prompt/terminal window open, Ctrl-C on the window will shut the server down.

## Configuration
* Almost all configuration occurs in the top level `site.properties` file.  The default version of this file from a fresh git
checkout contains reasonable defaults that use H2 as the main database, but disable PQL (which requires MySQL or Postgres and
more intricate configuration).

* H2 running from a flat file is the default database for the sake of zero-configuration.
However our development is done chiefly on MySQL; instructions for reconfiguring Apromore to use MySQL appear below.
We do have plugins for Postgres and Oracle, but some extra setup will be required since we only have sql scripts to create the
database for H2 and MySQL.

* You can upload some sample data into the system with the following command
```bash
ant install-sample-data
```
     /airport contains a Configurable BPMN process models which demonstrate configurability   
     /pql contains Petri nets in PNML format from the PQL test suite   
     /repair contains a BPMN model which demonstrates log animation   
* Some of Apromore's features are implemented as Java applets running client-side in the browser.  If you possess an code-signing
certificate (not an SSL certificate), you can edit the top-level `codesigning.properties` file to use your certificate rather
than the self-signed certificate included in the source tree.  This will avoid browser warnings.

## Common problems

> Out of memory while building.
* Either invoke `mvn` as `mvn -Xmx1G -XX:MaxPermSize=256m` or set the system property `MAVEN_OPTS` to `-Xmx1G -XX:MaxPermSize=256m`

> Server fails to start.
* If either Apromore or PQL are configured to use MySQL, confirm that the database server is running.
* If you already run another server (e.g. OS X Server) may need to change the port number 8443 in `Supplements/Virgo/tomcat-server.xml`.

> Login screen appears, but "admin" / "password" doesn't work.
* You may need to run `ant create-h2` to populate the H2 database.

> Can't view models by clicking them in the summary list.
* Model diagrams are opened in new tabs/windows; you may need to disable popup blocking for Apromore in your browser settings.

> Where is the server log?
* `Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/serviceability/logs/log.log`

> I grabbed the PQL.MySQL-1.2.sql file directly from the PQL sources and it doesn't work!
* Edit the file and change the uuid attribute of the jbpt_petri_nodes table from VARCHAR(50) to VARCHAR(100) in two places

> Models always show up in the log as unable to be indexed.
* Check that LoLA executable is correctly configured.

## MySQL setup (optional)

* Ensure MySQL is configured to accept local TCP connections on port 3306 in its .cnf file; "skip-networking" should not be present.
* Create a database named 'apromore' in your MySQL server
```bash
mysqladmin -u root -p create apromore
```
You will be prompted to enter the root password of MySQL
* Create a user named 'apromore' with the required permissions
```bash
mysql -u root -p
	CREATE USER 'apromore'@'localhost' IDENTIFIED BY 'MAcri';
	GRANT SELECT, INSERT, UPDATE, DELETE, LOCK TABLES, EXECUTE, SHOW VIEW ON apromore.* TO 'apromore'@'localhost';
```
* Create and populate the database tables.
```bash
mysql -u root -p apromore < Supplements/database/db-mysql.sql
```

At the end of the `db-mysql.sql` script is where we populate some of the system data including user information.  Currently, we have a few users setup that are developers or affiliates and they can be used or you can choose to add your own.  All passwords are 'password'by default. Once logged in, a user can change their password via `Account -> Change password` menu.

* Edit the top-level `site.properties` file, replacing the H2 declarations in "Database and JPA" with the commented-out MySQL properties.
Stop and restart the server so that it picks up the changes to `site.properties`.


## Predictive monitoring setup (optional)

* Predictive monitoring requires the use of MySQL, as described above. Populate the database with additional tables as follows:
```bash
mysql -u root -p apromore < Supplements/database/Nirdizati.MySQL-1.0.sql
```
* Check out predictive monitoring repository from GitHub:
```bash
git clone https://github.com/nirdizati/nirdizati-training-backend.git
```
* Set up additional servers (alongside the Apromore server), as directed in `nirdizati-training-backend/apromore/README.md`
* In site.properties, set the following properties:
  - `training.python` must be set to the location of a Python 3 executable
   - `training.backend` must be directory containing `nirdizati-training-backend`
   - `training.tmpDir` must be a writable directory for temporary files
   - `training.logFile` must be a writable file path for logging
The following properties may usually by left at their default values:
   - `kafka.host` can be left at the default `localhost:909`2, presuming Zookeeper and Kafka are running locally
   - the various `kafka.*.topic` properties should already match those used in the `nirdizati-training-backend` scripts
* Stop and restart the server so that it picks up the changes to `site.properties`.
* Ensure that the following bundles are present in the Virgo `pickup` directory (`ant start-virgo` copies them there on startup):
  - Predictive-Monitor-Logic/target/predictive-monitor-logic-1.0.jar
  - Predictive-Monitor-Portal-Plugin/target/predictive-monitor-portal-plugin-1.0.war
  - Predictor-Training-Portal-Plugin/target/predictor-training-portal-plugin-1.0.war


## LDAP setup (optional)

As distributed, Apromore maintains its own catalogue of users and passwords.
It can be modified to instead allow login based on an external LDAP directory.
This requires recompiling server components rather than just changing to `site.properties`; you will need to be comfortable hacking Spring, LDAP, Java and ZUL.

* Edit the portal Spring configuration in `Apromore-Core-Components/Apromore-Portal/src/main/resources/META-INF/spring/portalContext-security.xml`, uncommenting the jaasAuthenticationProvider as so:

```xml
    <!-- The remote authentication details -->
    <authentication-manager id="authenticationManager">
        <authentication-provider ref="jaasAuthenticationProvider"/>
        <authentication-provider ref="remoteAuthenticationProvider"/>
        <authentication-provider ref="rememberMeAuthenticationProvider"/>
    </authentication-manager>

    <!-- Uncommenting this bean and adding it to #authenticationManager (above) will enable LDAP logins.
         See https://docs.spring.io/spring-security/site/docs/3.1.x/reference/jaas.html -->
    <beans:bean id="jaasAuthenticationProvider" class="org.springframework.security.authentication.jaas.JaasAuthenticationProvider">
        <beans:property name="loginConfig" value="/WEB-INF/login.conf"/>
        <beans:property name="loginContextName" value="apromore"/>
        <beans:property name="callbackHandlers">
            <beans:list>
                <beans:bean class="org.springframework.security.authentication.jaas.JaasNameCallbackHandler"/>
                <beans:bean class="org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler"/>
            </beans:list>
        </beans:property>
        <beans:property name="authorityGranters">
            <beans:list>
                <beans:ref bean="authorityGranter"/>
            </beans:list>
        </beans:property>
    </beans:bean>

    <beans:bean id="authorityGranter" class="org.apromore.security.AuthorityGranterImpl">
        <beans:property name="principalClassName" value="com.sun.security.auth.UserPrincipal"/>
        <beans:property name="grants">
            <beans:set>
                <beans:value>ROLE_USER</beans:value>
            </beans:set>
        </beans:property>
    </beans:bean>
```

* Unless you're using the University of Melbourne's central authentication, you will need to additionally edit the following files to match your local LDAP installation:
  - Apromore-Core-Components/Apromore-Portal/src/main/webapp/WEB-INF/login.conf
  - Apromore-Core-Components/Apromore-Portal/src/main/java/org/apromore/portal/common/UserSessionManager.java (constructUserType method)

* Since accounts will now be created automatically, you might want to remove the "Forgot password?" and "No account yet? Register for free!" buttons on the login screen by editing `Apromore-Core-Components/Apromore-Portal/src/main/webapp/login.zul`

* Recompile the Portal bundle and its dependencies:
```bash
mvn clean install -pl :apromore-portal -amd
```

When the server starts with the modified portal, it will automatically create new accounts for valid LDAP logins.


## PQL setup (optional)
* [LoLA 2.0](http://service-technology.org/lola/) is required for PQL support
* PQL queries over the process store are only supported on MySQL.  Create and populate the database with additional tables for PQL:
```bash
mysql -u root -p apromore < Supplements/database/PQL.MySQL-1.2.sql
```
* In `site.properties`, perform the following changes:
  - Change `pql.numberOfIndexerThreads` to at least 1
  - Change `pql.numberOfQueryThreads` to at least 1
  - Change `pql.lola.dir` to the location of your LoLA 2.0 executable
  - Change the various `pql.mysql.*` properties to match your MySQL database
* In `build.xml`, uncomment the inclusion of the following PQL components in the `pickupRepo` fileset:
  - APQL-Portal-Plugin/target/apql-portal-plugin-1.1.war
  - Apromore-Assembly/PQL-Indexer-Assembly/src/main/resources/103-pql-indexer.plan
    PQL-Logic/target/pql-logic-1.1.jar
  - PQL-Logic-WS/target/pql-logic-ws-1.1.war
  - PQL-Portal-Plugin/target/pql-portal-plugin-1.1.jar
* Also, uncomment the following component in the `copy-virgo` target: `PQL-Indexer-Portal-Plugin/target/pql-indexer-portal-plugin-1.1.jar`


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fapromore%2FApromoreCore.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fapromore%2FApromoreCore?ref=badge_large)