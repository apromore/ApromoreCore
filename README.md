![apromore](http://apromore.org/wp-content/uploads/2019/11/Apromore-banner_narrow.png "apromore")

# Apromore Core

This repository contains source code of the [Apromore](https://apromore.org) Core process analytics web application server.  It can be built and run on its own, or used as a submodule containing components common to the two other Apromore editions:

* [Apromore Community Edition](https://github.com/apromore/ApromoreCE), which is open source.
* [Apromore Enterprise Edition](https://github.com/apromore/ApromoreEE), which is proprietary.

The instructions below are for the installation of Apromore Core from the source code. For convenience, we also make available a containerized image in [Docker](https://github.com/apromore/ApromoreDocker).
If you are looking for the commercial edition (Apromore Enterprise Edition), check the [Apromore web site](https://apromore.org/)

## System requirements
* Linux Ubuntu 18.04 (We do not support newer versions as it may lead to dependency issues), Windows 10/WS2016/WS2019, Mac OSX 10.8 or newer.
* Java SE 8 ["Server JRE"](https://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html) or
  ["JDK"](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) Edition 1.8. For Ubuntu, it can be installed as `sudo apt install openjdk-8-jdk`
  Note that newer versions, including Java SE 11, are currently not supported
* [Apache Maven](https://maven.apache.org/download.cgi) 3.5.2 or newer, and internet access for it to download this project's dependencies.
* [Lessc](http://lesscss.org/usage/) 3.9.0 or newer
* (optional) [MySQL server](https://dev.mysql.com/downloads/mysql/5.7.html) 5.7.
* <b>Note:</b> These instructions are tested with Linux Ubuntu 18.04. In Linux Ubuntu 20.04 there may be some dependency management issues. With minor adaptations, these instructions may be used for Windows 10/WS20016/WS2019 and macOS 10.8 or newer.

## Installation Instructions

* Check out the source code using git: `git clone https://github.com/apromore/ApromoreCore.git`
* Switch to the ApromoreCore directory: `cd ApromoreCore`
* Check out the desired branch or tag: `git checkout development`
* Execute `mvn clean install` to compile the source code into executable bundles.
* Execute `core-assemblies/apromore-core/target/assembly/bin/karaf` to start the server.
  <b>Note:</b> If you deploy to port 80 (or another port below 1024), you will need to run the previous command as sudo.
* Browse [(http://localhost:9000/)](http://localhost:8181/). Login as an administrator by using the following credentials: username - "admin" and password - "password". You can also create a new account. Once logged in, a user can change their password via `Account -> Change password` menu.
* Keep the prompt/terminal window open. Ctrl-D on the window will shut the server down.


## Configuration
The following configuration options apply to all editions of Apromore.
When there are additional configurations specific to a particular edition, they are documented in that edition's own README file.

Almost all configuration occurs in the `site.properties` file which is located in the `ApromoreCore` directory.
The default version of this file from a fresh git checkout contains reasonable defaults that allow the server to be started without any manual configuration.


### MySQL setup
By default, Apromore Core uses H2 database because it allows casual evaluation without requiring any configuration.
For earnest use or development, Apromore should be configured to use MySQL instead.

* Ensure MySQL is configured to accept local TCP connections on port 3306 in its .cnf file; "skip-networking" should not be present.

* Create a database named 'apromore' in your MySQL server. Also create 2 user accounts which use the Apromore application.
* You will be prompted to enter the root password of MySQL

```bash
mysql -u root -p
CREATE DATABASE apromore CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE USER 'apromore'@'localhost' IDENTIFIED BY 'MAcri';
GRANT SELECT, INSERT, UPDATE, DELETE, LOCK TABLES, EXECUTE, SHOW VIEW ON apromore.* TO 'apromore'@'localhost';

CREATE USER 'liquibase_user'@'%' IDENTIFIED BY '7fHJV41fpJ';
GRANT ALL PRIVILEGES ON apromore.* TO 'liquibase_user'@'%';
	
```

* Edit the top-level `site.properties` file, replacing the H2 declarations in "Database and JPA" with the commented-out MySQL properties.
* Rebuild the server using `mvn clean install -pl :apromore-core`, or simply copy the edited `site.properties` to `core-assemblies/apromore-core/target/assembly/etc/site.cfg`.
* Identically to the default H2 database, the initial MySQL database will have one user: "admin".


### Heap size
Memory limits are set using the usual JVM parameters.

The startup options must be set in the `JAVA_OPTS` environment variable.

On Windows:

```dos
set "JAVA_OPTS= -server -Xms20g -Xmx20g"
core-assemblies\apromore-core\target\assembly\bin\karaf.bat clean
```

On unix-style systems:

```bash
export JAVA_OPTS="-server -Xms20g -Xmx20g"
core-assemblies/apromore-core/target/assembly/bin/karaf clean
```


### Cache size
Apromore uses [Ehcache](https://www.ehcache.org/) for internal caching, which uses an XML configuration file.
The default in a deployed server is that the `ehcache.xml` configuration file is located at `core-assemblies/apromore-core/target/assembly/etc/ehcache.xml`.
The manager.ehcache.config.url property in site.properties can be used to point to an `ehcache.xml` at a URL of your choice.


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


### LDAP setup

As distributed, Apromore maintains its own catalogue of users and passwords.
It can be configured to instead allow login based on an external LDAP directory.

* Edit the portal Spring configuration in `core-assemblies/apromore-core/target/assembly/etc/configuration/portalContext-security.xml`, uncommenting the jaasAuthenticationProvider as so:

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
        <beans:property name="loginConfig" value="file:etc/login.conf"/>
        <beans:property name="loginContextName" value="apromore"/>
        <beans:property name="callbackHandlers">
            <beans:list>
                <beans:bean class="org.springframework.security.authentication.jaas.JaasNameCallbackHandler"/>
                <beans:bean class="org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler"/>
            </beans:list>
        </beans:property>
        <beans:property name="authorityGranters">
            <beans:list>
                <beans:bean class="org.apromore.security.AuthorityGranterImpl">
                    <beans:property name="principalClassName" value="com.sun.security.auth.UserPrincipal"/>
                    <beans:property name="grants">
                        <beans:set>
                            <beans:value>ROLE_USER</beans:value>
                        </beans:set>
                    </beans:property>
                </beans:bean>
            </beans:list>
        </beans:property>
    </beans:bean>
```

* Edit `core-assemblies/apromore-core/target/assembly/etc/site.cfg` (section marked "LDAP")
* Create `core-assemblies/apromore-core/target/assembly/etc/login.conf` to match your local LDAP installation.
  You could place it at another location by changing the loginConfig property from the previous step.
  As an example, the UoM version looks like this:
  ```
  apromore {
      com.sun.security.auth.module.LdapLoginModule REQUIRED
              userProvider="ldaps://centaur.unimelb.edu.au"
              authIdentity="uid={USERNAME},ou=people,o=unimelb";
  };
  ```

When the server starts with the reconfigured portal, it will automatically create new accounts for valid LDAP logins.


## Change Port Number (optional)
* Change the default port number by changing the value of `site.port` variable in the `site.cfg` file and the `org.osgi.service.http.port` variable in the `org.ops4j.pax.web.cfg` file, both present in the `core-assemblies/apromore-core/target/assembly/etc` directory.

### Share file to all users (optional)

* By default Apromore does not allow you to share a file with all users (i.e. the "public" group is not supported by default). You can change this by editing the site.cfg file present in the `core-assemblies/apromore-core/target/assembly/etc` directory. Specifically, to enable the option to share files and folders with the “public” group, you should set `security.publish.enable = true` in the site.cfg file.


## Common problems

> Out of memory while building.

* Either invoke `mvn` as `mvn -Xmx1G -XX:MaxPermSize=256m` or set the system property `MAVEN_OPTS` to `-Xmx1G -XX:MaxPermSize=256m`

> Server fails to start.

* If Apromore is configured to use MySQL, confirm that the database server is running.
* If you already run another server (e.g. OS X Server) you may need to change the port number.

> Web pages are illegible.

* Double-check that `lessc` is correctly installed.  Confirm that `mvn clean install -pl :ui-theme-compact` reports no errors.

> Can't view models by clicking them in the summary list.

* Model diagrams are opened in new tabs/windows; you may need to disable popup blocking for Apromore in your browser settings.

> Where is the server log?

* `core-assemblies/apromore-core/target/assembly/data/log/karaf.log`
