#!/usr/bin/env bash
npm run build
cp dist/processdiscoverer.js ../Apromore-Custom-Plugins/Process-Discoverer-Portal-Plugin/src/main/resources/static/processdiscoverer/js/ap/
cp dist/processdiscoverer.js ../Apromore-Boot/build/resources/main/web/processdiscoverer/js/ap/