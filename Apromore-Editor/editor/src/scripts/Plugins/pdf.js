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
if (!ORYX.Plugins)
    ORYX.Plugins = new Object();

ORYX.Plugins.File = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,

    construct: function(facade){
        this.facade = facade;

//        this.facade.offer({
//            'name': ORYX.I18N.File.print,
//            'functionality': this.print.bind(this),
//            'group': ORYX.I18N.File.group,
//            'icon': ORYX.PATH + "images/printer.png",
//            'description': ORYX.I18N.File.printDesc,
//            'index': 3,
//            'minShape': 0,
//            'maxShape': 0
//        });

        this.facade.offer({
            'name': ORYX.I18N.File.pdf,
            'functionality': this.exportPDF.bind(this),
            'group': ORYX.I18N.File.group,
            'icon': ORYX.PATH + "images/page_white_acrobat.png",
            'description': ORYX.I18N.File.pdfDesc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });

    },

    exportPDF: function() {
        this.facade.raiseEvent({ type:ORYX.CONFIG.EVENT_LOADING_ENABLE, text:ORYX.I18N.File.genPDF });
        var resource = location.href;

        // Get the serialized svg image source
        var svgClone = this.facade.getCanvas().getSVGRepresentation(true);
        var svgDOM = DataManager.serialize(svgClone);

        // Send the svg to the server.
        new Ajax.Request(ORYX.CONFIG.PDF_EXPORT_URL, {
            method: 'POST',
            parameters: {
                resource: resource,
                data: svgDOM,
                format: "pdf"
            },
            onSuccess: (function(request){
                this.facade.raiseEvent({ type:ORYX.CONFIG.EVENT_LOADING_DISABLE });
                // Because the pdf may be opened in the same window as the
                // process, yet the process may not have been saved, we're
                // opening every other representation in a new window.
                // location.href = request.responseText
                window.open(request.responseText);
            }).bind(this),
            onFailure: (function(){
                this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
                Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.File.genPDFFailed);
            }).bind(this)
        });
    }

//    print: function(){
//        Ext.Msg.show({
//            title		: ORYX.I18N.File.printTitle,
//            msg			: ORYX.I18N.File.printMsg,
//            buttons		: Ext.Msg.YESNO,
//            icon		: Ext.MessageBox.QUESTION,
//            fn			: function(btn) {
//                if (btn == "yes") {
//                    // Set all options for the new window
//                    var option = $H({
//                        width: 300,
//                        height: 400,
//                        toolbar: "no",
//                        status: "no",
//                        menubar: "yes",
//                        dependent: "yes",
//                        resizable: "yes",
//                        scrollbars: "yes"
//                    });
//
//                    // Create the new window with all the settings
//                    var newWindow = window.open("", "PrintWindow", option.invoke('join', '=').join(','));
//
//                    // Add a style tag to the head and hide all controls
//                    var head = newWindow.document.getElementsByTagName('head')[0];
//                    var style = document.createElement("style");
//                    style.innerHTML = " body {padding:0px; margin:0px} .svgcontainer { display:none; }";
//                    head.appendChild(style);
//
//                    // Clone the svg-node and append this to the new body
//                    newWindow.document.getElementsByTagName('body')[0].appendChild(this.facade.getCanvas().getSVGRepresentation());
//                    var svg = newWindow.document.getElementsByTagName('body')[0].getElementsByTagName('svg')[0];
//
//                    // Set the width and height
//                    svg.setAttributeNS(null, 'width', 1100);
//                    svg.setAttributeNS(null, 'height', 1400);
//
//                    // Set the correct size and rotation
//                    svg.lastChild.setAttributeNS(null, 'transform', 'scale(0.47, 0.47) rotate(270, 1510, 1470)');
//
//                    var markers = ['marker-start', 'marker-mid', 'marker-end'];
//                    var path = $A(newWindow.document.getElementsByTagName('path'));
//                    path.each(function(pathNode){
//                        markers.each(function(marker){
//                            // Get the marker value
//                            var url = pathNode.getAttributeNS(null, marker);
//                            if (!url) {
//                                return
//                            }
//
//                            // Replace the URL and set them new
//                            url = "url(about:blank#" + url.slice(5);
//                            pathNode.setAttributeNS(null, marker, url);
//                        });
//                    });
//
//                    // Get the print dialog
//                    newWindow.print();
//                    return true;
//                }
//            }.bind(this)
//        });
//    }
});

