# QBPSimulator
Integration of the QBP process simulator into Apromore

To build:

    npm run build
    node scripts/bundle-fix/index.js
    cp dist/bpmn-modeler.development.js ../../Apromore-Frontend/src/bpmneditor/editor/bpmnio/bpmn-modeler.development.js

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