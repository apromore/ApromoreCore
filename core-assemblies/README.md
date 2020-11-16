On macOS, the following environment variable must be set to avoid the unavailable Aqua look-and-feel from being requested and causing an exception:
```
export JAVA_OPTS=-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel
```
