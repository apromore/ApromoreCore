# Apromore BPMN Modeler 

This module provides a customized distribution of bpmn.io editor including custom extensions
for Apromore platform. These extensions could be developed in-house like the Simulator 
or from bpmn.io/third-party sources like the Color Picker. The following functionalities are 
implemented in this module: 

- QBPSimulator parameters saving. Integration of the QBP process simulator into Apromore
- Color picker for BPMN nodes.

To build and bundle:
```
    npm run test (make sure all tests passed before doing the next step)
    ./update-editor.sh
```
	

WARNING
-------

There is a bug in Safari, where forEach function in bpmn-modeler.development.js incorrectly take remove as valid property. Please replace forEach with the following:

        function forEach(collection, iterator) {
          var val, result;

          if (isUndefined(collection)) {
            return;
          }

          var convertKey = isArray(collection) ? toNum : identity;

          for (var key in collection) {
            if (has(collection, key) && key !== 'remove') {
              val = collection[key];
              result = iterator(val, convertKey(key));

              if (result === false) {
                return val;
              }
            }
          }
        }

This can be done by running:

    node scripts/bundle-fix/index.js        