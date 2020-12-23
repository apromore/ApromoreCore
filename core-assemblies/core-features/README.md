## Karaf features

This artifact is a Karaf-specific XML definition of various bundle assemblies ("features").
It serves the same role as Eclipse Virgo's plan artifacts.

Eventually it should be possible to deploy Apromore Core to a stock Karaf server using a
series of commands similar to the following:
```
config:install mvn:org.apromore/karaf-shell-branding/1.0/properties branding.properties
config:install file:/wherever/ApromoreCore/Apromore-Cache/src/main/resources/ehcache.xml ehcache.xml
config:install file:/wherever/ApromoreCore/target/classes/git.properties git.cfg
config:install file:/wherever/ApromoreCore/target/classes/git.properties git.core.cfg
config:install file:/wherever/ApromoreCore/Supplements/Virgo/portalContext-security.xml portalContext-security.xml
config:install file:/wherever/ApromoreCore/site.properties site.cfg
feature:repo-add mvn:org.apromore/core-features/7.20-SNAPSHOT/xml
feature:install apromore-core
```

This currently won't work because Apromore expects the `ehcache.xml` and
`portalContext-security.xml` configuration files to reside in `configuration/`, and
the `config:install` command can only install to `etc/`.
