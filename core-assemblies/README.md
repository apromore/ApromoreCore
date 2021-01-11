On macOS, the following environment variable must be set to avoid the unavailable Aqua look-and-feel from being requested and causing an exception:
```
export JAVA_OPTS=-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel
```
Either set it in your environment, or on the fly as follows:
```
(export JAVA_OPTS=-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel; apromore-core/target/assembly/bin/karaf)
```

This is the appropriate directory from which to issue a command like `mvn versions:set -DnewVersion=7.20-SNAPSHOT` in order to set the release version.
