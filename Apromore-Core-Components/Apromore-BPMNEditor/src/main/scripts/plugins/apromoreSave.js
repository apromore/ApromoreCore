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
if(!Apromore){ var Apromore = {} }
if(!Apromore.Plugins){ Apromore.Plugins = {} }

Apromore.Plugins.ApromoreSave = Clazz.extend({

    facade:undefined,

    changeSymbol:"*",

    construct:function (facade) {
        this.facade = facade;

        this.facade.offer({
            'name':window.Apromore.I18N.Save.save,
            'functionality':this.save.bind(this, false),
            'group':window.Apromore.I18N.Save.group,
            'icon':Apromore.PATH + "images/disk.png",
            'description':window.Apromore.I18N.Save.saveDesc,
            'index':1,
            'minShape':0,
            'maxShape':0,
            keyCodes:[
                {
                    metaKeys:[Apromore.CONFIG.META_KEY_META_CTRL],
                    keyCode:83, // s-Keycode
                    keyAction:Apromore.CONFIG.KEY_ACTION_UP
                }
            ]
        });

        this.facade.offer({
            'name':window.Apromore.I18N.Save.saveAs,
            'functionality':this.save.bind(this, true),
            'group':window.Apromore.I18N.Save.group,
            'icon':Apromore.PATH + "images/disk_multi.png",
            'description':window.Apromore.I18N.Save.saveAsDesc,
            'index':2,
            'minShape':0,
            'maxShape':0
        });
    },

    updateTitle:function () {

    },

    onUnLoad:function () {

    },

    /**
     * Saves the current process to the server.
     */
    save:function (forceNew, event) {
        if (this.saving) {
            return false;
        }

        this.saving = true;

        var xml = this.facade.getXML();
        var svg = this.facade.getSVG();

        if (forceNew) {
            if (Apromore.Plugins.ApromoreSave.apromoreSaveAs) {
                Apromore.Plugins.ApromoreSave.apromoreSaveAs(xml, svg);
            } else {
                alert("Apromore Save As method is missing!");
            }
        } else {
            if (Apromore.Plugins.ApromoreSave.apromoreSave) {
                Apromore.Plugins.ApromoreSave.apromoreSave(xml, svg);
            } else {
                alert("Apromore Save method is missing!");
            }
        }

        this.saving = false;
        return true;
    }

});


