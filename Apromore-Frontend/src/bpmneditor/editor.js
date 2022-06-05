/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

import Log from './logger';
import Utils from './utils';
// import { GLYPHS } from './assets';
import { SYMBOLS } from './assets';

/**
 * Editor is actually a wrapper around the true editor (e.g. BPMN.io)
 * It provides BPMN editing features while hiding the actual editor implementation provider.
 * The aim is to minimize the impact of the implementation changes with changes minimized to this
 * class only while the editor used in Apromore codebase is unchanged as they only access this Editor class.
 */
export default class Editor {
    constructor(options) {
        this.dirty = false;
        this.actualEditor = undefined;
        // this.preventFitDelay = options.preventFitDelay;

        if (!(options && options.width && options.height)) {
            Log.fatal("The editor is missing mandatory parameters options.width and options.height.");
            return;
        }

        this.className = "Apromore_Editor";
        this.rootNode = Utils.graft("http://www.w3.org/1999/xhtml", options.parentNode,
            ['div', {id: options.id, width: options.width, height: options.height}
            ]);
        //this.rootNode.addClassName(this.className);
        this.rootNode.classList.add(this.className);
    }

    setDirty(dirty) {
        this.dirty = dirty;
    }

    isDirty() {
        return this.dirty;
    }

    attachEditor(editor) {
        let me = this;
        this.actualEditor = editor; //This is a BPMNJS object
    }

    getSVGContainer() {
        //return $("div.Apromore_Editor div.bjs-container div.djs-container svg")[0];
        return $('#' + this.rootNode.id + " div.bjs-container div.djs-container svg")[0];
    }

    getSVGViewport() {
        //return $("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0];
        return $('#' + this.rootNode.id + " div.bjs-container div.djs-container svg g.viewport")[0];
    }

    getIncomingFlowId(nodeId) {
        if (!this.actualEditor || !this.actualEditor.getDefinitions()) return false;
        var foundId;
        var flowElements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.targetRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    }

    getOutgoingFlowId(nodeId) {
        if (!this.actualEditor || !this.actualEditor.getDefinitions()) return false;
        var foundId;
        var elements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.sourceRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    }

    toString() {
        return "EditorWrapper " + this.id;
    }

    /**
     * Import XML into the editor.
     * This method takes time depending on the complexity of the model
     * @param {String} xml: the BPMN XML
     * @param {Function} callback: callback function to call after the import finishes
     */
    async importXML(xml) {
        if (!this.actualEditor) return false;
        if (!(typeof xml === 'string')) throw new Error("Invalid XML input");
        xml = sanitizeXML(xml);

        await this.actualEditor.importXML(xml);

        //EXPERIMENTING WITH THE BELOW TO FIX ARROWS NOT SNAP TO EDGES WHEN OPENING MODELS
        //Some BPMN files are not compatible with bpmn.io
        var editor = this.actualEditor;
        var eventBus = editor.get('eventBus');
        var elementRegistry = editor.get('elementRegistry');
        var me = this;

        eventBus.on('elements.changed', function () {
            me.setDirty(true);
        });
        eventBus.on('elements.changedAux', function () {
            me.setDirty(true);
        });

        eventBus.on('selection.changed', function(context) {
          var newSelection = context.newSelection;

          me.updateFontSize(newSelection)
        });
        var connections = elementRegistry.filter(function(e) {return e.waypoints;});
        var connectionDocking = editor.get('connectionDocking');
        connections.forEach(function(connection) {
            connection.waypoints = connectionDocking.getCroppedWaypoints(connection);
        });
        eventBus.fire('elements.changed', { elements: connections });

        /**
         * Any clean up needs to be done on the raw XML
         *
         * @todo: There might be a structured way to do this (e.g. XML parser),
         * but this should be the fastest fix for now
         */
        function sanitizeXML(xml) {
            if (!(typeof xml === 'string')) return;
            var REMOVE_LIST = [
                // Empty label element breaks label editing in bpmn.io
                /<bpmndi:BPMNLabel\s*\/>/ig,
                /<bpmndi:BPMNLabel\s*>\s*<\/bpmndi:BPMNLabel\s*>/ig
            ];

            REMOVE_LIST.forEach(function(regex) {
                xml = xml.replace(regex, '');
            })
            return xml;
        }
    }

    async getXML() {
        if (!this.actualEditor) return '';
        const result = await this.actualEditor.saveXML({ format: true }).catch(err => {throw err;});
        let {xml} = result;
        if (xml) {
            xml = xml.replace(/>\s*/g, '>');
            xml = xml.replace(/\s*</g, '<');
        }
        return xml;
    }

    async processAux(svg) {
        const me = this;

        function getBase64(url) {
            return new Promise(resolve => {
                const img = new Image();

                img.setAttribute('crossOrigin', 'anonymous');
                img.onload = function () {
                    const canvas = document.createElement("canvas");
                    canvas.width =this.width;
                    canvas.height =this.height;
                    const ctx = canvas.getContext("2d");
                    ctx.drawImage(this, 0, 0);
                    const dataURL = canvas.toDataURL("image/png");
                    resolve({
                        dataURL,
                        img2: img
                    });
                };
                img.src = url;
          });
        }

        async function getBase64Async(url) {
          const result = await loadImage(url);
          return result;
        }

        function nsResolver(prefix) {
            switch (prefix) {
                case 'xlink':
                    return 'http://www.w3.org/1999/xlink';
                case 'svg':
                    return 'http://www.w3.org/2000/svg';
                default:
                    return 'http://www.w3.org/2000/svg';
            }
        }

        const PULL_EXT = false;
        const USE_SYMBOLS = true;
        const parser = new DOMParser();
        const doc = parser.parseFromString(svg, 'image/svg+xml');
        const overlays = this.actualEditor.get('overlays');
        const auxes = overlays.get({ type: 'aux'});
        let minTop = 0, minLeft = 0, maxBottom = 0, maxRight = 0;
        const hyperlinks = [];
        auxes.forEach(async (aux) => {
            const overlay = $(aux.htmlContainer);
            const containerId = overlay.parent().data('containerId');
            const parentLeft = parseInt(overlay.parent().css('left'));
            const parentTop = parseInt(overlay.parent().css('top'));
            const left = parseInt(overlay.css('left'));
            const top = parseInt(overlay.css('top'));
            const width = parseInt(overlay.css('width'));
            const height = parseInt(overlay.css('height'));
            const right = left + width;
            const bottom = top + height;
            if (minLeft > left) { minLeft = left; }
            if (minTop > top) { minTop = top; }
            if (maxRight < right) { maxRight = right; }
            if (maxBottom < bottom) { maxBottom = bottom; }

            const context = doc.evaluate("//svg:g[@data-element-id='" + containerId + "']",
                doc, nsResolver, XPathResult.ANY_TYPE, null);
            const container = context.iterateNext();
            const auxImage = $('.aux-image', overlay);
            const img = $('.aux-image img', overlay)[0];

            let auxContent = '';
            let yOffset = 0;
            let img2, dataURL;

            // Image handling
            if (container && img && img.complete && img.naturalWidth && img.naturalHeight) {
                var href = img.currentSrc;
                if (href.startsWith('http') && PULL_EXT) {
                    let result = await getBase64Async(href);
                    dataURL = result.dataURL;
                    img2 = result.img2;
                } else {
                    dataURL = href;
                    img2 = img;
                }
                const ratio = img2.naturalHeight / img2.naturalWidth;
                const imgWidth = width - 8;
                const imgHeight = ratio * imgWidth;
                auxContent += `<image href="${dataURL}" width="${imgWidth}" height="${imgHeight}"/>`;
                yOffset += imgHeight + 20;
                const caption = $('.caption', auxImage);
                if (caption) {
                    const aLink = $('a', caption)[0];
                    if (aLink) {
                        hyperlinks.push({
                            href: aLink.href,
                            left: parentLeft + left,
                            top: parentTop + top + yOffset,
                            width: aLink.textContent.length,
                            height: 1
                        });
                        auxContent += `<a href="${aLink.href}">
                            <text x="0" y="${yOffset}" lineHeight="1.2" style="font-family: Arial, sans-serif; font-size: 12px; font-weight: normal; fill: black;">
                                ${aLink.textContent}
                            </text>
                        </a>`
                        yOffset += 18.5;
                    }
                }
            }

            // Icons handling
            const iconItems = $('.aux-icon-item', overlay);
            iconItems.each((index, iconItem) => {
                let char = ''
                let icon = $('.aux-icon', iconItem);
                icon = icon && icon[0];
                if (icon) {
                   let span = $('span', icon);
                   let className = span.attr('class');
                   if (USE_SYMBOLS) {
                       if (className && className.length) {
                           auxContent += `<use href="#${className}" width="20" height="24" x="0" y="${yOffset - 12}" />`;
                       }
                   } else {
                       if (span.hasClass('ap-icon-computer')) {
                          char = '&#xEA01;'
                       } else {
                          char = window.getComputedStyle(span[0],':before').content || '';
                          char = char.replaceAll('"', '');
                       }
                        auxContent += `<text lineHeight="1.2" style="font-family: icomoon; font-size: 20px; font-weight: normal; fill: black;">
                           <tspan x="0" y="${yOffset}">${char}</tspan>
                       </text>`;
                   }
                }
                let iconLink = $('.aux-icon-link', iconItem);
                iconLink = iconLink && iconLink[0];
                if (iconLink) {
                    const iconTextLink = iconLink.textContent;
                    const aIconLink = $('a', iconLink)[0];
                    if (aIconLink) {
                        hyperlinks.push({
                            href: aIconLink.href,
                            left: parentLeft + left + 30,
                            top: parentTop + top + yOffset,
                            width: iconTextLink.length,
                            height: 1
                        });
                        auxContent += `<a href="${aIconLink.href}">
                            <text lineHeight="20px" style="font-family: Arial, sans-serif; font-size: 12px; font-weight: normal; fill: black;">
                                <tspan x="30" y="${yOffset}">${iconTextLink}</tspan>
                            </text></a>`
                    } else {
                        auxContent += `<text lineHeight="20px" style="font-family: Arial, sans-serif; font-size: 12px; font-weight: normal; fill: black;">
                                <tspan x="30" y="${yOffset}">${iconTextLink}</tspan>
                            </text>`
                    }
                }
                yOffset += 26;
            })
            if (auxContent.length) {
                auxContent = `<g transform="matrix(1 0 0 1 ${left} ${top})">${auxContent}</g>`;
                const parser2 = new DOMParser();
                const auxTarget = parser2.parseFromString(auxContent, 'image/svg+xml');
                container.appendChild(auxTarget.childNodes[0])
           }
        });
        // Fix viewBox
        const svgDummy = doc.evaluate("//svg:svg", doc, nsResolver, XPathResult.ANY_TYPE, null);
        const svgRoot = svgDummy.iterateNext();
        const viewBox = svgRoot.getAttribute('viewBox');
        const coords = viewBox.split(' ').map((x) => parseInt(x));
        coords[0] = coords[0] + minLeft;
        coords[1] = coords[1] + minTop;
        coords[2] = coords[2] + (maxRight - minLeft);
        coords[3] = coords[3] + (maxBottom - minTop);
        svgRoot.setAttribute('viewBox', coords.join(' '));
        svgRoot.setAttribute('width', '' + coords[2]);
        svgRoot.setAttribute('height', '' + coords[3]);
        let raw = new XMLSerializer().serializeToString(doc);
        // For glyph-based approach, uncomment the following
        // However this is not supported by Apache SVG2PDF transcoder
        // raw = raw.replaceAll('<defs>', `<defs>${GLYPHS}`);
        raw = raw.replaceAll('<defs>', `<defs>${SYMBOLS}`);
        raw = raw.replaceAll('xmlns=""', ''); // Remove empty namespace
        raw = raw.replaceAll('<use href=', '<use xlink:href='); // Apache transcoder expect xlink ns
        return {
            raw,
            hyperlinks
        }
    }

    async getSVG2() {
        if (!this.actualEditor) return '';
        const result = await this.actualEditor.saveSVG({ format: true }).catch(err => {throw err;});
        const { svg } = result;
        return await this.processAux(svg);
    }

    async getSVG() {
        if (!this.actualEditor) return '';
        const result = await this.actualEditor.saveSVG({ format: true }).catch(err => {throw err;});
        const { svg } = result;
        const { raw } = await this.processAux(svg);
        return raw;
    }

    zoomFitToModel() {
        if (!this.actualEditor) return false;
        let canvas = this.actualEditor.get('canvas');
        canvas.viewbox(false); // trigger recalculate the viewbox
        canvas.zoom('fit-viewport', 'auto'); // zoom to fit full viewport
        if (!this.originViewbox) this.originViewbox = canvas.viewbox();
        return true;
    }

    zoomFitOriginal() {
        if (!this.actualEditor || !this.originViewbox) return false;
        this.actualEditor.get('canvas').viewbox(this.originViewbox);
        return true;
    }

    zoomIn() {
        if (!this.actualEditor) return false;
        var canvas = this.actualEditor.get('canvas');
        canvas.zoom(canvas.zoom() * 1.1);
        return true;
    }

    zoomOut() {
        if (!this.actualEditor) return false;
        var canvas = this.actualEditor.get('canvas');
        canvas.zoom(canvas.zoom() * 0.9);
        return true;
    }

    zoomDefault() {
        if (!this.actualEditor) return false;
        var canvas = this.actualEditor.get('canvas');
        canvas.zoom(1);
        return true;
    }

    undo() {
        if (!this.actualEditor) return false;
        this.actualEditor.get('commandStack').undo();
        return true;
    }

    canUndo() {
        if (!this.actualEditor) {
            return false;
        } else {
            return this.actualEditor.get('commandStack').canUndo();
        }
    }

    redo() {
        if (!this.actualEditor) return false;
        this.actualEditor.get('commandStack').redo();
        return true;
    }

    canRedo() {
        if (!this.actualEditor) {
            return false;
        } else {
            return this.actualEditor.get('commandStack').canRedo();
        }
    }

    addCommandStackChangeListener(callback) {
        if (!this.actualEditor) return false;
        this.actualEditor.on('commandStack.changed', callback);
        return true;
    }

    addEventBusListener(eventCode, callback) {
        if (!this.actualEditor) return false;
        this.actualEditor.get('eventBus').on(eventCode, callback);
        return true;
    }


    ////////////////////// Modelling Elements Editing Methods /////////////////////////////////

    createShape(type, x, y, w, h) {
        var modelling = this.actualEditor.get('modeling');
        var parent = this.actualEditor.get('canvas').getRootElement();
        var shape = modelling.createShape({type:type, width:w, height:h}, {x:x, y:y}, parent);
        return shape.id;
    }

    createSequenceFlow(source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:SequenceFlow'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        var flow = modelling.connect(registry.get(source), registry.get(target), attrs2);
        return flow.id;
    }

    removeShapes(shapeIds) {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        var shapes = [];
        shapeIds.forEach(function(shapeId) {
            shapes.push(registry.get(shapeId));
        });
        modelling.removeElements(shapes);
    }

    updateProperties(elementId, properties) {
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        modelling.updateProperties(registry.get(elementId), properties);
    }

    colorElements(elementIds, color) {
        var elements = [];
        var registry = this.actualEditor.get('elementRegistry');
        elementIds.forEach(function(elementId) {
            elements.push(registry.get(elementId));
        });
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(elements, {stroke:color});
    }

    normalizeAll() {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(registry.getAll(), {stroke:'black'});
    }

    getCenter(shapeId) {
        var shape = this.actualEditor.get('elementRegistry').get(shapeId);
        return {
            x: shape.x + (shape.width || 0) / 2,
            y: shape.y + (shape.height || 0) / 2
        }
    }

    getDimensions(shapeId) {
        var shape = this.actualEditor.get('elementRegistry').get(shapeId);
        return {
            x: shape.x,
            y: shape.y,
            width: shape.width,
            height: shape.height
        }
    }

    async refreshAll() {
        if (!this.actualEditor) return ;
        var eventBus = this.actualEditor.get('eventBus');
        var elementRegistry = editor.get('elementRegistry');
        var elements = elementRegistry.getAll();
        eventBus.fire('elements.changed', { elements });
    }

    async changeGlobalFontSize(size) {
        const modeler = this.actualEditor
        const config = modeler.get('config');
        if (!config) return;

        config.textRenderer = {
            defaultStyle:
            {
              fontSize: size
            },
            externalStyle: {
              fontSize: size
            }
        };
        modeler.get('textRenderer').setFontSize(size);
        let elementRegistry = modeler.get('elementRegistry');
        let elements = elementRegistry.getAll();
        let eventBus = modeler.get('eventBus');

        eventBus.fire('commandStack.changed', { elements, type: 'commandStack.changed'});
        eventBus.fire('elements.changed', { elements, type: 'elements.changed' });
    }

    updateFontSize(selection) {
      const DEFAULT_SIZE = 16;
      let selectedFontSize = -1;
      try  {
        for (let i = 0; i < selection.length; i++) {
            const element = selection[i]
            const bo = element.businessObject;
            let size = parseInt(bo["aux-font-size"])
            if (isNaN(size)) {
                size = DEFAULT_SIZE
            }
            if (selectedFontSize === -1) {
                selectedFontSize = size
            } else {
                if (selectedFontSize !== size) {
                    selectedFontSize = -1
                    break;
                }
            }
        }
      } catch (r) {
        // pass
      }
      if (selectedFontSize === -1) {
        selectedFontSize = DEFAULT_SIZE;
      }
      Apromore.BPMNEditor.updateFontSize(selectedFontSize);
    }

    async changeFontSize(size) {
        const modeler = this.actualEditor
        if (!modeler) return;

        let eventBus = modeler.get('eventBus');
        let elements = modeler.get('selection').get();
        if (!elements || !elements.length) {
            // this.changeGlobalFontSize(size);
            let elementRegistry = modeler.get('elementRegistry');
            elements = elementRegistry.getAll();
        }
        elements.forEach((element) => {
            const bo = element.businessObject;
            bo["aux-font-size"] = size+"px";
        });
        eventBus.fire('commandStack.changed', { elements, type: 'commandStack.changed'});
        eventBus.fire('elements.changed', { elements, type: 'elements.changed' });
    }

};
