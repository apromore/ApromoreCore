#!/usr/bin/env bash
npm run build
node scripts/bundle-fix/index.js
cp dist/bpmn-modeler.development.js ../../../Apromore-Frontend/src/bpmneditor/editor/bpmnio/bpmn-modeler.development.js
cd ../../../Apromore-Frontend
npm run build
cp dist/bpmneditor.js ../Apromore-Core-Components/Apromore-BPMN-Editor/Apromore-BPMN-Editor-Portal/src/main/resources/static/bpmneditor/editor/
cp dist/bpmneditor.js ../Apromore-Boot/build/resources/main/web/bpmneditor/editor/bpmneditor.js
cd ../Apromore-Core-Components/Apromore-BPMN-Editor/Apromore-BPMN-Modeler-JS