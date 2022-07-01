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

import CONFIG from './../config';

export default class Export {

    constructor(facade) {
        this.facade = facade;

        this.facade.offer({
            'btnId': 'ap-id-editor-export-svg-btn',
            'name': window.Apromore.I18N.File.svg,
            'functionality': this.exportSVG.bind(this),
            'icon': CONFIG.PATH + "images/ap/export-svg.svg",
            'description': window.Apromore.I18N.File.svgDesc,
            "groupOrder": 0,
            'index': 3
        });

        this.facade.offer({
            'btnId': 'ap-id-editor-export-bpmn-btn',
            'name': window.Apromore.I18N.File.bpmn,
            'functionality': this.exportBPMN.bind(this),
            'icon': CONFIG.PATH + "images/ap/export-bpmn.svg",
            'description': window.Apromore.I18N.File.bpmnDesc,
            "groupOrder": 0,
            'index': 4
        });

    }

    async exportSVG() {
        var svg = await this.facade.getSVG();
        var hiddenElement = document.createElement('a');
        hiddenElement.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(svg);
        hiddenElement.target = '_blank';
        hiddenElement.download = this.facade.app.getProcessName() + '.svg';
        hiddenElement.click();
    }

    async exportBPMN() {
        var xml = await this.facade.getXML();

        if (Apromore.BPMNEditor.Plugins.Export.exportXML) {
            Apromore.BPMNEditor.Plugins.Export.exportXML(xml)
        } else {
            var hiddenElement = document.createElement('a');
            hiddenElement.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(xml);
            hiddenElement.target = '_blank';
            hiddenElement.download = this.facade.app.getProcessName() + '.bpmn';
            hiddenElement.click();
        }
    }

};
