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
if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

Apromore.Plugins.Export = Clazz.extend({

    facade: undefined,

    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': window.Apromore.I18N.File.svg,
            'functionality': this.exportSVG.bind(this),
            'group': window.Apromore.I18N.File.group,
            'icon': Apromore.PATH + "images/exportsvg.png",
            'description': window.Apromore.I18N.File.svgDesc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });

        this.facade.offer({
            'name': window.Apromore.I18N.File.bpmn,
            'functionality': this.exportBPMN.bind(this),
            'group': window.Apromore.I18N.File.group,
            'icon': Apromore.PATH + "images/exportbpmn.png",
            'description': window.Apromore.I18N.File.bpmnDesc,
            'index': 4,
            'minShape': 0,
            'maxShape': 0
        });

    },

    exportSVG: function() {
        var svg = this.facade.getSVG();
        var hiddenElement = document.createElement('a');
        hiddenElement.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(svg);
        hiddenElement.target = '_blank';
        hiddenElement.download = 'diagram.svg';
        hiddenElement.click();
    },

    exportBPMN: function() {
        var xml = this.facade.getXML();
        var hiddenElement = document.createElement('a');
        hiddenElement.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(xml);
        hiddenElement.target = '_blank';
        hiddenElement.download = 'diagram.bpmn';
        hiddenElement.click();
    }

});

