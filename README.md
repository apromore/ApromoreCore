![apromore](http://apromore.org/wp-content/uploads/2019/11/Apromore-banner_narrow.png "apromore")

# Apromore Core

This repository contains source code of [Apromore](https://apromore.org) Core.  This is not a standalone repository; it is a submodule containing components common to the two Apromore editions:

* [Apromore Community Edition](https://github.com/apromore/ApromoreCE), which is open source.
* [Apromore Enterprise Edition](https://github.com/apromore/ApromoreEE), which is proprietary.

This document is relevant to both editions, but if you have checked out this Core repository on its own, you are in the wrong place and should instead first check out one of the two editions listed above.


## System requirements
* Windows 7 or newer or Mac OSX 10.8 or newer (other users - check out our [Docker-based version](https://github.com/apromore/ApromoreDocker))
* Java SE 8 ["Server JRE"](https://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html) or
  ["JDK"](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) Edition 1.8.
  Note that newer versions, including Java SE 11, are currently not supported
* [Apache Maven](https://maven.apache.org/download.cgi) 3.5.2 or newer
* [Apache Ant](https://ant.apache.org/bindownload.cgi) 1.10.1 or newer
* [Lessc](http://lesscss.org/usage/) 3.9.0 or newer
* (optional) [MySQL server](https://dev.mysql.com/downloads/mysql/5.7.html) 5.6 or 5.7.
  Note that version 8.0 is currently not supported.


## Common problems

> Out of memory while building.
* Either invoke `mvn` as `mvn -Xmx1G -XX:MaxPermSize=256m` or set the system property `MAVEN_OPTS` to `-Xmx1G -XX:MaxPermSize=256m`

> Server fails to start.
* If either Apromore or PQL are configured to use MySQL, confirm that the database server is running.
* If you already run another server (e.g. OS X Server) you may need to change the port number 8443 in `Supplements/Virgo/tomcat-server.xml`.

> Login screen appears, but "admin" / "password" doesn't work.
* You may need to run `ant create-h2` to populate the H2 database.

> Can't view models by clicking them in the summary list.
* Model diagrams are opened in new tabs/windows; you may need to disable popup blocking for Apromore in your browser settings.

> Where is the server log?
* `Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/serviceability/logs/log.log`


## Configuration
The following configuration options apply to all editions of Apromore.
When there are additional configuration specific to a particular edition, they are documented in that edition's own README file.

Almost all configuration occurs in the `site.properties` file which is located in the `ApromoreCore` directory.
The default version of this file from a fresh git checkout contains reasonable defaults that allow the server to be started without manual configuration.


### MySQL setup
The H2 flat file database is the default only because it allows casual evaluation without requiring any configuration.
For earnest use or development, Apromore should be configured to use MySQL instead..

* Ensure MySQL is configured to accept local TCP connections on port 3306 in its .cnf file; "skip-networking" should not be present.
* Create a database named 'apromore' in your MySQL server
```bash
mysql -u root -p -e 'CREATE DATABASE apromore CHARACTER SET utf8 COLLATE utf8_general_ci;'
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


### Heap size
Memory limits are set using the usual JVM parameters.
If you start Apromore using Ant (appropriate during development) it looks like this:

> ant start-virgo -Dstartup.java.opts="-server -Xmx15g -Xmn2g"

When you do not explicitly set `startup.java.opts`, the default value in `ApromoreCore/build-core.xml` will be used.

If you start Apromore by directly running the startup script (appropriate in production) then the startup options must be set in the `JAVA_OPTS` environment variable.
Unlike Ant-based startup, no sensible default value will be set if `JAVA_OPTS` is omitted, so it's likely the server will fail with memory-related errors.

On Windows:

```dos
set "JAVA_OPTS= -server -Xms20g -Xmx20g"
startup.bat -clean
```

On unix-style systems:

```bash
export JAVA_OPTS="-server -Xms20g -Xmx20g"
startup.sh -clean
```


### Cache size
Apromore uses [Ehcache](https://www.ehcache.org/) for internal caching, which uses an XML configuration file.
The default in a deployed server is that the `ehcache.xml` configuration file is located at `virgo-tomcat-server-3.6.4.RELEASE/configuration/ehcache.xml`.
The manager.ehcache.config.url property in site.properties can be used to point to an `ehcache.xml` at a URL of your choice.


### LDAP setup

As distributed, Apromore maintains its own catalogue of users and passwords.
It can be configured to instead allow login based on an external LDAP directory.

* Edit the portal Spring configuration in `virgo-tomcat-server-3.6.4.RELEASE/configuration/portalContext-security.xml`, uncommenting the jaasAuthenticationProvider as so:

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

* Unless you're using the University of Melbourne's central authentication, you will need to additionally edit the following files to match your local LDAP installation:
  - `virgo-tomcat-server-3.6.4.RELEASE/repository/usr/site.properties` (section marked "LDAP")
  - `virgo-tomcat-server-3.6.4.RELEASE/configuration/login.conf`

* Since accounts will now be created automatically, you might want to remove the "Forgot password?" and "No account yet? Register for free!" buttons on the login screen by editing `Apromore-Core-Components/Apromore-Portal/src/main/webapp/login.zul`

When the server starts with the reconfigured portal, it will automatically create new accounts for valid LDAP logins.
