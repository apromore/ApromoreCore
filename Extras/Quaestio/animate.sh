#!/bin/sh

CBPMN2BPMNDIR=../../Apromore-Editor

# Convert to Signavio's JSON format (test.bpmn -> test.json)
(cd ${CBPMN2BPMNDIR}; ant bpmn2json)

# Upload to Signavio for viewing (test.json -> Test model in Signavio)
(cd ${CBPMN2BPMNDIR}; sh upload.sh)
