/**
 * Copyright (c) 2009
 * Kai Schlichting
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

/**
 * Enables exporting and importing current model in JSON.
 */
ORYX.Plugins.JSONSupport = Clazz.extend({

    construct: function(facade){
        // Call super class constructor
        //arguments.callee.$.construct.apply(this, arguments);
        this.facade = facade;
        this.facade.offer({
            name: ORYX.I18N.JSONSupport.exp.name,
            functionality: this.exportJSON.bind(this),
            group: ORYX.I18N.JSONSupport.exp.group,
            dropDownGroupIcon: ORYX.PATH + "images/export2.png",
			icon: ORYX.PATH + "images/page_white_javascript.png",
            description: ORYX.I18N.JSONSupport.exp.desc,
            index: 0,
            minShape: 0,
            maxShape: 0
        });

        this.facade.offer({
            name: ORYX.I18N.JSONSupport.imp.name,
            functionality: this.showImportDialog.bind(this),
            group: ORYX.I18N.JSONSupport.imp.group,
            dropDownGroupIcon: ORYX.PATH + "images/import.png",
			icon: ORYX.PATH + "images/page_white_javascript.png",
            description: ORYX.I18N.JSONSupport.imp.desc,
            index: 1,
            minShape: 0,
            maxShape: 0
        });
    },

    exportJSON: function(){

    },

    /**
     * Opens a upload dialog.
     */
    showImportDialog: function(successCallback){

    }

});
