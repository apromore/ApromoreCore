## Karaf features

This artifact is a Karaf-specific XML definition of various bundle assemblies ("features").
It serves the same role as Eclipse Virgo's plan artifacts.

To use the features directly, start Apache Karaf and enter the following commands:
```
feature:repo-add mvn:org.apromore.lumberyard/karaf-features/8.0.0-SNAPSHOT/xml
feature:install virgo-compatibiity
```
