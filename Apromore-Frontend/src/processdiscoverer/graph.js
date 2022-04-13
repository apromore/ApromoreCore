import cytoscape from "cytoscape/dist/cytoscape.esm";
import popper from 'cytoscape-popper';
import dagre from 'cytoscape-dagre';
const jquery = require('jquery');
const konva = require('konva');
const edgeEditing = require('cytoscape-edge-editing');
// Disable this to avoid interference with server side undo/redo
// const undoRedo = require('cytoscape-undo-redo');

cytoscape.use(popper);
cytoscape.use( dagre );
edgeEditing( cytoscape, jquery, konva );
let edgeEditingOptions = {
    anchorShapeSizeFactor: 6,
    initAnchorsAutomatically: false,
    undoable: false,
    enableCreateAnchorOnDrag: false,
    zIndex: 0
}

import GraphModelWrapper from "../processmap/graphModelWrapper";
import LogAnimation from "../loganimation/logAnimation";
import tippy from "tippy.js";
import * as jsPDF from "jspdf";
import { saveAs } from 'file-saver';

const LAYOUT_MANUAL_BEZIER = 0;
const LAYOUT_DAGRE_LR = 1;
const LAYOUT_DAGRE_TB = 2;
const LAYOUT_BREADTH_FIRST = 3;
const NAME_PROP = 'oriname';
const MAX_AUTOFIT_ZOOM = 1;

let PDp = {};

let SIGNATURE = '/zkau/web/themes/ap/common/img/brand/logo-colour.svg';

const layouters = {
    [LAYOUT_MANUAL_BEZIER]: function (cy) {
        cy.elements().layout({
            name: 'preset',
        }).run();
    },
    [LAYOUT_DAGRE_LR]: function (cy) {
        cy.elements().layout({
            avoidOverlap: !0,
            edgeSep: 50,
            name: 'dagre',
            nodeSep: 110,
            randomize: false,
            rankDir: 'LR',
            ranker: 'network-simplex',
        }).run();
    },
    [LAYOUT_DAGRE_TB]: function (cy, randomize) {
        cy.style().selector('edge').style({
            'text-background-opacity': 1,
            'text-margin-y': 0,
        }).update();

        cy.elements().layout({
            avoidOverlap: !0,
            edgeSep: 50,
            name: 'dagre',
            nodeSep: 110,
            randomize,
            rankDir: 'TB',
            ranker: 'tight-tree',
        }).run();
    },
    [LAYOUT_BREADTH_FIRST]: function (cy) {
        cy.style().selector('edge').style({
            'text-background-opacity': 1,
            'text-margin-y': 0,
        }).update();

        cy.elements().layout({
            avoidOverlap: true,
            directed: !0,
            name: 'breadthfirst',
            spacingFactor: 1,
        }).run();
    },
};

let container;
let sourceJSON;
let vizBridgeId = '$vizBridge';
let eventRegistered = false;
let options = {
    maxZoom: 1E50,
    minZoom: 1E-50,
    panningEnabled: true,
    userPanningEnabled: true,
    userZoomingEnabled: true,
    wheelSensitivity: .1,
    zoom: 1,
    zoomingEnabled: true,
};
// Cut text in the server side
// const cutText = (text) => {
//   if (text.length > 60) {
//     return text.substring(0, 60) + '...';
//   }
//   return text;
// }
const calcFontSize = (text) => {
    let len = text.length;
    if (len > 40) {
        return 14;
    } else if (len > 20) {
        return 15;
    } else {
        return 16;
    }
};
let style = [
    {
        selector: 'node',
        style: {
            'background-color': 'data(color)',
            'border-color': 'black',
            // 'border-width': 'data(borderwidth)',
            'border-width': '1px',
            'border-style': 'solid',
            'color': 'data(textcolor)',
            'content': 'data(name)',
            'font-size': function (ele) { // 'data(textsize)',
                if (ele.data('shape') === 'roundrectangle') {
                    let fontSize = calcFontSize(ele.data('oriname'));
                    return fontSize + 'px';
                }
                return ele.data('textsize');
            },
            'height': function (ele) { // 'data(height)',
                let oriHeight = ele.data('height');
                if (oriHeight === '70px') {
                    return '80px';
                }
                return oriHeight;
            },
            'padding': '5px',
            'shape': 'data(shape)',
            'text-border-width': 0,
            'text-max-width': 'data(textwidth)',
            'text-valign': 'center',
            'text-wrap': 'wrap',
            'width': 'data(width)',
        },
    },
    {
        selector: 'edge',
        style: {
            'curve-style': 'unbundled-bezier',
            'edge-distances': 'intersection',
            'color': 'data(color)',
            'edge-text-rotation': 0,
            'font-size': 16,
            'label': 'data(label)',
            'line-color': 'data(color)',
            'line-style': 'data(style)',
            'loop-direction': -41,
            'loop-sweep': 181,
            'opacity': 1,
            'source-arrow-color': 'data(color)',
            'target-arrow-color': 'data(color)',
            'target-arrow-shape': 'triangle',
            'text-background-color': '#ffffff',
            'text-background-opacity': 0,
            'text-background-padding': 5,
            'text-background-shape': 'roundrectangle',
            'text-margin-y': -16,
            'text-wrap': 'wrap',
            'width': 'mapData(strength, 0, 100, 1, 6)',
        },
    },
    {
        selector: 'node:selected',
        style: {
            'border-width': '2px',
            'overlay-color': '#f96100',
            'overlay-padding': '18px',
            'overlay-opacity': 0.2
        },
    },
    {
        selector: ':selected',
        style: {
            'border-color': '#f96100',
            'line-color': '#f96100',
            'line-style': 'solid',
            'target-arrow-color': '#f96100',
        },
    }
];
let elements = {
    nodes: [],
    edges: [],
};

let currentLayout = 0;
let isCtrlPressed = false;
let isAltPressed = false;
let isShiftPressed = false;

let currentNodeTooltip;
let currentZoomLevel = 1;
let currentPanPosition;
let isTraceMode = false; // source trace or source full log

let activityToAttributeMap = null;

const tablize = function (attributeArray, klass) {
    var html = `<table class="${klass}">`;

    for (const { name, value } of attributeArray) {
        html += `<tr><td>${name}</td><td>${value}</td></tr>`;
    }
    html += '</table>';
    return html;
};

PDp.updateActivityToAttributeMap = function (json) {
    activityToAttributeMap = json || null;
};

PDp.init = function() {
    let pd = this;
    SIGNATURE = `/zkau/web/themes/${Ap.theme}/common/img/brand/logo-colour.svg`;
    container = $j('#' + pd._private.processModelContainerId)[0];
    let cy = this._private.cy = cytoscape(Object.assign(options, {
        container,
        style,
        elements,
    }));

    cy.on('cxttap', 'edge', function (source) {
        if (!isTraceMode) {
            pd.removeEdge(source);
        }
    });
    cy.on('cxttap', 'node', function (source) {
        if (!isTraceMode) {
            pd.removeNode(source);
        }
    });
    cy.on('pan', function (event) {
        if (!isTraceMode) {
            currentPanPosition = cy.pan();
        }
    });
    cy.on('zoom', function (event) {
        if (!isTraceMode) {
            currentZoomLevel = cy.zoom();
        }
    });

    // cy.on('beforeUndo', function() {
    // });

    cy.on('mouseover', 'node', function (event) {
        let node = event.target;
        let name = node.data(NAME_PROP);
        let id = node.data('id');
        if (id) {
            if (activityToAttributeMap) { // more detail tooltip for trace mode
                let attributes = activityToAttributeMap[id];
                if (attributes && Object.keys(attributes).length) {
                    currentNodeTooltip = pd.makeTippy(node, tablize(attributes, 'ap-pd-case-activity-attrs'));
                } else {
                    currentNodeTooltip = pd.makeTippy(node, name);
                }
            } else {
                currentNodeTooltip = pd.makeTippy(node, name);
            }
            currentNodeTooltip.show();
        } else {
            currentNodeTooltip = undefined;
        }
    });

    cy.on('mouseout', 'node', function (event) {
        if (currentNodeTooltip) currentNodeTooltip.hide();
    });

    if (!eventRegistered) {
        eventRegistered = true;
        $(document).keydown(function (evt) {
            if (evt.shiftKey || 16 === evt.keyCode || 16 === evt.which) {
                isShiftPressed = true;
            }
            if (evt.ctrlKey || evt.metaKey || 17 === evt.keyCode || 17 === evt.which) {
                isCtrlPressed = true;
            }
            if (evt.altKey || 18 === evt.keyCode || 18 === evt.which) {
                isAltPressed = true;
            }
            if (isCtrlPressed && evt.which === 90) { // "Z" key
                evt.preventDefault();
                pd.zkSendEvent("$filterUndo", "onClick", "");
            } else if (isCtrlPressed && evt.which === 89) { // "Y" key
                evt.preventDefault();
                pd.zkSendEvent("$filterRedo", "onClick", "");
            }
        });
        $(document).keyup(function () {
            isAltPressed = isCtrlPressed = isShiftPressed = false;
        });
    }
}

PDp.makeTippy = function(node, text) {
    return tippy(node.popperRef(), {
        content: function () {
            let div = document.createElement('div');
            div.innerHTML = text;
            return div;
        },
        trigger: 'manual',
        arrow: true,
        placement: 'bottom',
        hideOnClick: true,
        multiple: false,
        sticky: true,
    });
}

PDp.reset = function() {
    let cy = this._private.cy;
    if (cy) {
        cy.destroy();
    }
}

PDp.loadLog = function(json, layoutType, retain) {
    currentLayout = layoutType;

    // Need to set the current zoom/pan level again as the reset/zoom/pan actions
    // will generate zoom and pan events and change them.
    let zoom = currentZoomLevel;
    let pan = currentPanPosition;

    isTraceMode = false;
    this.reset();
    this.init();

    let cy = this._private.cy;
    let source = $.parseJSON(json);
    sourceJSON = source;
    cy.add(source);
    this.layout(layoutType);
    this.setupSearch(source, true);

    if (retain) {
        cy.zoom(zoom);
        cy.pan(pan);
        currentZoomLevel = zoom;
        currentPanPosition = pan;
    } else {
        this.fit(layoutType);
    }

    cy.edgeEditing(edgeEditingOptions);
    cy.style().update(); // to inform cytoscape as the edge editing plugin made changes to its style.
}

PDp.loadTrace = function(json) {
    isTraceMode = true;
    this.reset();
    this.init();

    let cy = this._private.cy;
    json = json.replaceAll('\n', '\\n'); // escape
    const source = $.parseJSON(json);
    cy.add(source);
    this.layout(LAYOUT_MANUAL_BEZIER);
    this.setupSearch(source);
    this.fit(1);

    cy.edgeEditing(edgeEditingOptions);
    cy.style().update(); // to inform cytoscape as the edge editing plugin made changes to its style.
}

const getViewportCenter = (extent) => {
    let { x1, x2, y1, y2 } = extent;
    let x = (x1 + x2) / 2;
    let y = (y1 + y2) / 2;
    return { x, y };
}

PDp.zoomIn = function() {
    let cy = this._private.cy;
    cy.zoom({ level: cy.zoom() + 0.1, position: getViewportCenter(cy.extent()) });
}

PDp.zoomOut = function() {
    let cy = this._private.cy;
    cy.zoom({ level: cy.zoom() - 0.1, position: getViewportCenter(cy.extent()) });
}

PDp.fit = function(layoutType) {
    let cy = this._private.cy;
    cy.fit();
    if (cy.zoom() > MAX_AUTOFIT_ZOOM) {
        cy.zoom(MAX_AUTOFIT_ZOOM);
        cy.center();
    }
}

PDp.center = function(layoutType) {
    let cy = this._private.cy;
    cy.center();
}

PDp.resize = function() {
    let cy = this._private.cy;
    cy.resize();
    this.fit();
}

PDp.moveTop = function(layoutType) {
    let cy = this._private.cy;
    let currentPos = cy.pan();
    let box = cy.elements().boundingBox({includeNodes: true, includeEdges: true});

    switch (layoutType) {
        case 0:
        case 1:
            if (cy.zoom() > 1.0) {
                cy.pan({x: currentPos.x, y: -box.y1 + 10});
            } else {
                cy.pan({x: currentPos.x, y: -box.y1 * cy.zoom() + 10});
            }
            break;
        case 2:
            cy.center(cy.nodes().filter(function (ele) {
                return ele.data(NAME_PROP) == '|>';
            }));
            cy.pan({x: currentPos.x, y: 0});
            break;
        case 3:
            cy.fit();
            break;
        default:
        // code block
    }
}

PDp.layout = function(layoutType) {
    let cy = this._private.cy;
    let layouter = layouters[layoutType];
    if (layouter) {
        layouter(cy, true);
    }
}

PDp.pos = function(source, b) {
    let c = 0,
        e = 0,
        d;
    for (d in source.incomers().sources().outgoers().targets()) c += d.position()[0], e += 1;
    return 0 == e ? 0 : c / e;
}

PDp.removeEdge = function(evt) {
    let evTarget = evt.target;
    let source = evTarget.source().data(NAME_PROP);
    let target = evTarget.target().data(NAME_PROP);
    if (source === '') { source = '|>'; }
    if (target === '') { target = '[]'; }
    let graphEvent;
    let payload = source.concat(' => ', target);
    let compId = vizBridgeId;
    if (isShiftPressed) {
        payload = {type: 'ATTRIBUTE_ARC_DURATION', source, target};
        compId = '$filter';
        graphEvent = 'onInvokeExt';
    } else if (isCtrlPressed) {
        graphEvent = 'onEdgeRetained';
    } else {
        graphEvent = 'onEdgeRemoved';
    }
    this.zkSendEvent(compId, graphEvent, payload);
}

PDp.removeNode = function(evt) {
    let evTarget = evt.target;
    let graphEvent;
    let data = evTarget.data(NAME_PROP);
    let compId = vizBridgeId;
    let payload = data;
    if (data !== '') {
      if (isShiftPressed) {
        compId = '$filter';
        graphEvent = 'onInvokeExt';
        if (isCtrlPressed) {
          payload = { type: 'CASE_SECTION_ATTRIBUTE_COMBINATION', data };
        } else {
          payload = { type: 'EVENT_ATTRIBUTE_DURATION', data };
        }
      } else if (isCtrlPressed || isAltPressed) {
        if (isCtrlPressed && !isAltPressed) {
          graphEvent = 'onNodeRetainedTrace'
        } else if (!isCtrlPressed && isAltPressed) {
          graphEvent = 'onNodeRemovedEvent'
        } else {
          graphEvent = 'onNodeRetainedEvent'
        }
      } else {
        graphEvent = 'onNodeRemovedTrace'
      }
      this.zkSendEvent(compId, graphEvent, payload);
    }
  }

PDp.zkSendEvent = function(widgetId, event, payload) {
    zAu.send(new zk.Event(zk.Widget.$(widgetId), event, payload));
}

PDp.rediscover = function() {
    this.zkSendEvent(vizBridgeId, 'onNodeFiltered', this._private.cy.json());
}

PDp.animate = function() {
    this.zkSendEvent(vizBridgeId, 'onAnimate', this._private.cy.json());
}

PDp.exportFitted = function() {
    this.zkSendEvent('$exportFitted', 'onExport', this._private.cy.json());
}

PDp.exportUnfitted = function () {
    this.zkSendEvent('$exportUnfitted', 'onExport', this._private.cy.json());
}

PDp.loadImage = function(src) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.addEventListener("load", () => resolve(img));
        img.addEventListener("error", err => reject(err));
        img.src = src;
    });
};

const SIGN_HEIGHT = 100;
const MARGIN = 100;

PDp.rasterizeForPrint = function() {
    let cy = this._private.cy;
    return Promise.all([
        this.loadImage(SIGNATURE),
        this.loadImage('data:image/png;base64,' + cy.png({
            full: true,
            output: 'base64',
            scale: 1.0,
            quality: 1.0,
        }))
    ]).then(function ([sign, graph]) {
        let canvas = document.createElement('canvas');
        let context = canvas.getContext('2d');
        let signHeight = SIGN_HEIGHT;
        let signWidth = signHeight * sign.width / sign.height;
        sign.width = signWidth;
        sign.height = signHeight;
        canvas.width = graph.width + 2 * MARGIN;
        canvas.height = graph.height + signHeight + 2 * MARGIN;
        context.fillStyle = 'white';
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.drawImage(sign, MARGIN, MARGIN, signWidth, signHeight);
        context.drawImage(graph, MARGIN, signHeight + MARGIN);
        return canvas;
    });
}

PDp.exportPDF = function(filename) {
    let pd = this;
    this.rasterizeForPrint()
        .then(function (canvas) {
            let pdf = new jsPDF('l', 'px', [canvas.width, canvas.height], false, true);
            pd.loadImage(canvas.toDataURL())
                .then(function (raster) {
                    pdf.addImage(raster, 'PNG', 0, 0, canvas.width, canvas.height, NaN, 'FAST');
                    pdf.save(filename + '.pdf', {returnPromise: true});
                });
        });
}

PDp.exportPNG = function(filename) {
    this.rasterizeForPrint()
        .then(function (canvas) {
            let a = document.createElement('a');
            canvas.toBlob(function (blob) {
                a.href = URL.createObjectURL(blob);
                a.download = filename + '.png';
                a.click();
            });
        });
}

PDp.saveAsFile = function(t, f, m) {
    try {
        var b = new Blob([t], {type: m});
        saveAs(b, f);
    } catch (e) {
        window.alert('Error in saving file: ' + e.message);
    }
}

PDp.exportJSON = function(filename) {
    if (!sourceJSON) {
        window.alert('Process model doesn\'t exist or it has no associated JSON file!');
    }
    this.saveAsFile(
        JSON.stringify(sourceJSON, null, 2),
        filename + ".json",
        "application/json;charset=utf-8"
    );
}

PDp.showCaseDetails = function() {
    let {left, top} = $('.ap-pd-logstats').offset();
    left -= 700; // width of caseDetail window
    this.zkSendEvent('$win', 'onCaseDetails', {top: top + 'px', left: left + 'px'});
}

PDp.showCaseVariantDetails = function() {
    let {left, top} = $('.ap-pd-logstats').offset();
    left -= 700; // width of caseDetail window
    this.zkSendEvent('$win', 'onCaseVariantDetails', {top: top + 'px', left: left + 'px'});
}

PDp.showPerspectiveDetails = function() {
    let {left, top} = $('.ap-pd-logstats').offset();
    left -= 700; // width of perspectiveDetail window
    this.zkSendEvent('$win', 'onPerspectiveDetails', {top: top + 'px', left: left + 'px'});
}

PDp.switchToAnimationView = function(setupDataJSON) {
    //jQueryExt.registerWheelStartStopEvents($j);
    let cy = this._private.cy;
    cy.nodes().ungrabify();
    let pd = this;
    $j('#' + pd._private.animationPanelContainerId).show();
    pd._private.logAnimationMapController = new GraphModelWrapper(cy);
    pd._private.logAnimation = this._private.logAnimation = new LogAnimation(
                                        pd._private.pluginExecutionId,
                                        this._private.logAnimationMapController,
                                        pd._private.processModelContainerId,
                                        pd._private.timelineContainerId,
                                        pd._private.speedControlContainerId,
                                        pd._private.progressContainerId,
                                        pd._private.logInfoContainerId,
                                        pd._private.clockContainerId,
                                        pd._private.buttonsContainerId,
                                        pd._private.playClassName,
                                        pd._private.pauseClassName);
    pd._private.logAnimation.initialize(setupDataJSON);
}

PDp.switchToInteractiveView = function() {
    let pd = this;
    this._private.cy.nodes().grabify();
    let la = pd._private.logAnimation;
    if (la) la.destroy();
    $j('#' + pd._private.animationPanelContainerId).hide();
    pd._private.logAnimation = null;
    pd._private.logAnimationMapController = null;
    pd.setupSearch(sourceJSON, true);
}

export default PDp;
