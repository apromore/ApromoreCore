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

ORYX.Plugins.File = Clazz.extend({

    facade: undefined,

    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': ORYX.I18N.File.print,
            'functionality': this.print.bind(this),
            'group': ORYX.I18N.File.group,
            'icon': ORYX.PATH + "images/printer.png",
            'description': ORYX.I18N.File.printDesc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });

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

    },

    print: function(){

    }

});

