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

Apromore.Plugins.File = Clazz.extend({

    facade: undefined,

    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': window.Apromore.I18N.File.pdf,
            'functionality': this.exportPDF.bind(this),
            'group': window.Apromore.I18N.File.group,
            'icon': Apromore.PATH + "images/page_white_acrobat.png",
            'description': window.Apromore.I18N.File.pdfDesc,
            'index': 5,
            'minShape': 0,
            'maxShape': 0
        });
    },

    exportPDF: function() {
        var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
        myMask.show();

        var resource = location.href;

        // Get the serialized svg image source
        var svgClone = this.facade.getEditor().getSVG();
        //var svgDOM = DataManager.serialize(svgClone);

        // Send the svg to the server.
        new Ajax.Request(Apromore.CONFIG.PDF_EXPORT_URL, {
            method: 'POST',
            parameters: {
                resource: resource,
                data: svgClone,
                format: "pdf"
            },
            onSuccess: (function(request){
                myMask.hide();
                // Because the pdf may be opened in the same window as the
                // process, yet the process may not have been saved, we're
                // opening every other representation in a new window.
                // location.href = request.responseText
                window.open(request.responseText);
            }).bind(this),
            onFailure: (function(){
                myMask.hide();
                Ext.Msg.alert(window.Apromore.I18N.Apromore.title, window.Apromore.I18N.File.genPDFFailed);
            }).bind(this)
        });
    }

});

