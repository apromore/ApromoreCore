Apromore Frontend
=================

This module contains javascript in Node.js project for the following components:
    - Process Discoverer
    - Log Animation
    - BPMN Editor

Folder structure:

- src: the source files divided into modules
- test: source test files divided into modules
- resources: resource files such as images, text, CSS.
- config: config files
- scripts: NPM script files, if any.
- dist: distribution bundles

Important:
---------

Currently npm version 6 is used for this repo.

Commands:

npm install --production=false: to install all dependencies
npm i -g webpack-cli: to install webpack-cli if asked when run 'npm run build'.
npm install: to install only production dependencies if the development dependencies have been installed and unchanged.
npm run test: to run unit tests.
npm run build: to build distribution bundles
    - processdiscoverer.js bundle: used for Process Discoverer
    - loganimationbpmn.js: used for Log Animation calling from the Portal.
    - bpmneditor.js: used for BPMN Editor.

To deploy Javascript bundles to Apromore plugins:

- For Process Discoverer: drop the file dist/processdiscoverer.js into ApromoreCore/Apromore-Custom-Plugins/Process-Discoverer-Portal-Plugin/src/main/resources/static/processdiscoverer/js/ap (Overwrite any existing file)

- For Log Animation: drop the file dist/loganimationbpmn.js into ApromoreEE/Log-Animation-Portal-Plugin/src/main/resources/static/loganimation2/js/ap/ (Overwrite any existing file)
- Build and deploy PD or Log Animation plugins
- Login Apromore portal, run the plugin to view the new deployment.

- For BPMNEditor: drop the file dist/bpmneditor.js into ApromoreCore/Apromore-Core-Components/Apromore-BPMN-Editor/Apromore-BPMN-Editor-Portal/src/main/resources/static/bpmneditor/editor/ (Overwrite any existing file)

