#!/usr/bin/env bash
npm run build
cp dist/bpmneditor.js ../Apromore-Core-Components/Apromore-BPMN-Editor/Apromore-BPMN-Editor-Portal/src/main/resources/static/bpmneditor/editor/
cp dist/bpmneditor.js ../Apromore-Boot/build/resources/main/web/bpmneditor/editor/bpmneditor.js
