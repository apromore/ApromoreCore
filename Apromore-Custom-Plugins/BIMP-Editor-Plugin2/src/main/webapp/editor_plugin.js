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
if(!ORYX){ var ORYX = {} }
if(!ORYX.Plugins){ ORYX.Plugins = {} }

ORYX.Plugins.BIMPSimulator = Clazz.extend({

    construct: function (facade) {

        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);

        this.facade = facade;
        this.facade.offer({
            'name': 'Simulate in BIMP',
            'functionality': this.simulate.bind(this),
            'group': 'Configuration',
            'icon': "/bimp2/icon.png",
            'description': 'Upload file to BIMP Simulator (plugin)',
            'index': 1
        });
    },

    simulate: function() {
        var bimpWindow = window.open('/bimp2/simulator.html','bimp');
        bimpWindow.apromoreBPMN = this.facade.getXML();

        // Import new process model
        // new Ajax.Request('/editor/editor/bpmnexport', {
        //     parameters: {
        //         'data': this.facade.getSerializedJSON()
        //     },
        //     method: 'POST',
        //
        //     onSuccess: function(transport) {
        //         var bimpWindow = window.open('/bimp2/simulator.html','bimp');
        //         bimpWindow.apromoreBPMN = transport.responseText;
        //     }.bind(this),
        //
        //     onFailure: function(transport) {
        //         console.log("Failure");
        //         console.dir(transport);
        //         alert("Unable to export BPMN model from editor: " + transport.responseText);
        //     }.bind(this)
        // });
    }
});
