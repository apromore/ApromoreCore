/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
(function() {
  const LAYOUT_MANUAL_BEZIER = 0;
  const LAYOUT_DAGRE_LR = 1;
  const LAYOUT_DAGRE_TB = 2;
  const LAYOUT_BREADTH_FIRST = 3;
  const NAME_PROP = 'oriname';
  const MAX_AUTOFIT_ZOOM = 1;

  let SIGNATURE = '/themes/ap/common/img/brand/logo-colour.svg';

  const layouters = {
    [LAYOUT_MANUAL_BEZIER]: function() {
      cy.style().selector('edge').style({
        'curve-style': function(ele) {
          return ele.data('edge-style');
        },
        'edge-distances': 'intersection',
        'control-point-distances': function(ele) {
          if (ele.data('edge-style') == 'unbundled-bezier') {
            return ele.data('point-distances');
          } else {return '0';}
        },
        'control-point-weights': function(ele) {
          if (ele.data('edge-style') == 'unbundled-bezier') {
            return ele.data('point-weights');
          } else {return '0.5';}
        },
      }).update();

      cy.elements().layout({
        name: 'preset',
      }).run();
    },
    [LAYOUT_DAGRE_LR]: function() {
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
    [LAYOUT_DAGRE_TB]: function(randomize) {
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
    [LAYOUT_BREADTH_FIRST]: function() {
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
  let cy = null;
  let vizBridgeId = '$vizBridge';
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
  let style = [
    {
      selector: 'node',
      style: {
        'background-color': 'data(color)',
        'border-color': 'black',
        'border-width': 'data(borderwidth)',
        'color': 'data(textcolor)',
        'content': 'data(name)',
        'font-size': 'data(textsize)',
        'height': 'data(height)',
        'padding': 0,
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
        'color': 'data(color)',
        'control-point-step-size': 60,
        'curve-style': 'bezier',
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
      selector: ':selected',
      style: {
        'border-color': '#ffa500',
        'border-width': '2px',
        'color': '#ffa500',
        'line-color': '#ffa500',
        'line-style': 'solid',
        'target-arrow-color': '#ffa500',
      },
    }];
  let elements = {
    nodes: [],
    edges: [],
  };

  let currentLayout = 0;
  let isCtrlPressed = false;
  let isAltPressed = false;

  let currentNodeTooltip;
  let currentZoomLevel = 1;
  let currentPanPosition;
  let isTraceMode = false; // source trace or source full log

  function init() {

    SIGNATURE = `/themes/${Ap.theme}/common/img/brand/logo-colour.svg`
    container = document.getElementById('ap-pd-process-model')
    cy = cytoscape(Object.assign(options, {
      container,
      style,
      elements,
    }));
    window.cy = cy;

    cy.on('cxttap', 'edge', function(source) {
      if (!isTraceMode) {
        removeEdge(source);
      }
    });
    cy.on('cxttap', 'node', function(source) {
      if (!isTraceMode) {
        removeNode(source);
      }
    });
    cy.on('pan', function(event) {
      if (!isTraceMode) {
        currentPanPosition = cy.pan();
      }
    });
    cy.on('zoom', function(event) {
      if (!isTraceMode) {
        currentZoomLevel = cy.zoom();
      }
    });

    cy.on('mouseover', 'node', function(event) {
      let node = event.target;
      if (node.data(NAME_PROP)) {
        currentNodeTooltip = makeTippy(node, node.data(NAME_PROP));
        currentNodeTooltip.show();
      } else {
        currentNodeTooltip = undefined;
      }
    });

    cy.on('mouseout', 'node', function(event) {
      if (currentNodeTooltip) currentNodeTooltip.hide();
    });

    $(document).keydown(function(evt) {
      if (evt.ctrlKey || 17 === evt.keyCode || 17 === evt.which) {
        isCtrlPressed = true;
      }
      if (evt.altKey || 18 === evt.keyCode || 18 === evt.which) {
        isAltPressed = true;
      }
      if (evt.ctrlKey && evt.which === 90) {
        cy.undoRedo().undo();
      } else if (evt.ctrlKey && evt.which === 89) {
        cy.undoRedo().redo();
      }
    })

    $(document).keyup(function() {
      isAltPressed = isCtrlPressed = false;
    });
  }

  function makeTippy(node, text) {
    return tippy(node.popperRef(), {
      content: function() {
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

  function reset() {
    cy.destroy();
  }

  function loadLog(json, layoutType, retain) {
    currentLayout = layoutType;

    // Need to set the current zoom/pan level again as the reset/zoom/pan actions
    // will generate zoom and pan events and change them.
    let zoom = currentZoomLevel;
    let pan = currentPanPosition;

    isTraceMode = false;
    reset();
    init();
    cy.add($.parseJSON(json));
    layout(layoutType);

    if (retain) {
      cy.zoom(zoom);
      cy.pan(pan);
      currentZoomLevel = zoom;
      currentPanPosition = pan;
    } else {
      fit(layoutType);
    }

    cy.edgeBendEditing({
      bendShapeSizeFactor: 6,
      enabled: true,
      initBendPointsAutomatically: false,
      undoable: true,
    });
  }

  function loadTrace(json) {
    isTraceMode = true;
    reset();
    init();
    cy.add($.parseJSON(json));
    layout(LAYOUT_MANUAL_BEZIER);
    fit(1);
  }

  function zoomIn() {
    cy.zoom(cy.zoom() + 0.1);
    cy.center();
  }

  function zoomOut() {
    cy.zoom(cy.zoom() - 0.1);
    cy.center();
  }

  function fit(layoutType) {
    cy.fit();
    if (cy.zoom() > MAX_AUTOFIT_ZOOM) {
      cy.zoom(MAX_AUTOFIT_ZOOM);
      cy.center();
    }
    //moveTop(layoutType);
  }

  function center(layoutType) {
    cy.center();
    //moveTop(layoutType);
  }

  function resize() {
    cy.resize();
    fit();
  }

  function moveTop(layoutType) {
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
        cy.center(cy.nodes().filter(function(ele) {
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

  function layout(layoutType) {
    let layouter = layouters[layoutType];
    if (layouter) {
      layouter(true);
    }
  }

  function pos(source, b) {
    let c = 0,
        e = 0,
        d;
    for (d in source.incomers().sources().outgoers().targets()) c += d.position()[0], e += 1;
    return 0 == e ? 0 : c / e;
  }

  function removeEdge(evt) {
    let evTarget = evt.target;
    let source = evTarget.source().data(NAME_PROP);
    let target = evTarget.target().data(NAME_PROP);
    if (source === '') { source = '|>'; }
    if (target === '') { target = '[]'; }
    let payload = source.concat(' => ', target);
    if (isCtrlPressed) {
      zkSendEvent(vizBridgeId, 'onEdgeRetained', payload);
    } else {
      zkSendEvent(vizBridgeId, 'onEdgeRemoved', payload);
    }
  }

  function removeNode(evt) {
    let evTarget = evt.target;
    let data = evTarget.data(NAME_PROP);
    if (data !== '') {
      if (isCtrlPressed || isAltPressed) {
        if (isCtrlPressed && !isAltPressed) {
          zkSendEvent(vizBridgeId, 'onNodeRetainedTrace', data);
        } else if (!isCtrlPressed && isAltPressed) {
          zkSendEvent(vizBridgeId, 'onNodeRemovedEvent', data);
        } else {
          zkSendEvent(vizBridgeId, 'onNodeRetainedEvent', data);
        }
      } else {
        zkSendEvent(vizBridgeId, 'onNodeRemovedTrace', data);
      }
    }

  }

  function zkSendEvent(widgetId, event, payload) {
    zAu.send(new zk.Event(zk.Widget.$(widgetId), event, payload));
  }

  function rediscover() {
    zkSendEvent(vizBridgeId, 'onNodeFiltered', cy.json());
  }

  function animate() {
    zkSendEvent(vizBridgeId, 'onAnimate', cy.json());
  }

  function exportFitted() {
    zkSendEvent('$exportFitted', 'onExport', cy.json());
  }

  function exportUnfitted() {
    zkSendEvent('$exportUnfitted', 'onExport', cy.json());
  }

  function loadImage(src) {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.addEventListener("load", () => resolve(img));
      img.addEventListener("error", err => reject(err));
      img.src = src;
    });
  };

  const SIGN_HEIGHT = 100;
  const MARGIN = 100;

  function rasterizeForPrint() {
    return Promise.all([
      loadImage(SIGNATURE),
      loadImage('data:image/png;base64,' + cy.png({
        full: true,
        output: 'base64',
        scale: 1.0,
        quality: 1.0,
      }))
    ]).then(function([sign, graph]) {
      let canvas = document.createElement('canvas');
      let context = canvas.getContext('2d');
      let signHeight = SIGN_HEIGHT
      let signWidth = signHeight * sign.width / sign.height;
      sign.width = signWidth;
      sign.height = signHeight;
      canvas.width = graph.width + 2 * MARGIN;
      canvas.height = graph.height + signHeight + 2 * MARGIN;
      context.fillStyle = 'white';
      context.fillRect(0, 0, canvas.width, canvas.height);
      context.drawImage(sign, MARGIN, MARGIN, signWidth, signHeight);
      context.drawImage(graph, MARGIN, signHeight + MARGIN);
      return canvas
    })
  }

  function exportPDF(filename) {
    rasterizeForPrint()
      .then(function(canvas) {
        let pdf = new jsPDF2('l', 'px', [canvas.width, canvas.height], false, true);
        loadImage(canvas.toDataURL())
          .then(function (raster) {
            pdf.addImage(raster, 'PNG', 0, 0, canvas.width, canvas.height, NaN, 'FAST');
            pdf.save(filename + '.pdf', {returnPromise: true});
          })
      })
  }

  function exportPNG(filename) {
    rasterizeForPrint()
      .then(function(canvas) {
        let a = document.createElement('a');
        canvas.toBlob(function(blob) {
          a.href = URL.createObjectURL(blob);
          a.download = filename + '.png';
          a.click();
        });
      })
  }

  Object.assign(Ap.pd, {
    init,
    zkSendEvent,
    exportUnfitted,
    exportPDF,
    exportPNG,
    loadLog,
    loadTrace,
    animate,
    fit,
    center,
    zoomIn,
    zoomOut,
    resize
  })

})();
