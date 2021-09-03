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

//if(!Apromore){ var Apromore = {} }
//if(!Apromore.Plugins){ Apromore.Plugins = {} }

import CONFIG from './../config';

export default class ApromoreSave {

    constructor (facade) {
        this.changeSymbol = "*";
        this.facade = facade;

        this.facade.offer({
            'name':window.Apromore.I18N.Save.save,
            'btnId': 'ap-id-editor-save-btn',
            'functionality':this.save.bind(this, false),
            'group':window.Apromore.I18N.Save.group,
            'icon':CONFIG.PATH + "images/ap/save.svg",
            'description':window.Apromore.I18N.Save.saveDesc,
            'index':1,
            'minShape':0,
            'maxShape':0,
            keyCodes:[
                {
                    metaKeys:[CONFIG.META_KEY_META_CTRL],
                    keyCode:83, // s-Keycode
                    keyAction:CONFIG.KEY_ACTION_UP
                }
            ]
        });

        this.facade.offer({
            'name':window.Apromore.I18N.Save.saveAs,
            'functionality':this.save.bind(this, true),
            'group':window.Apromore.I18N.Save.group,
            'icon':CONFIG.PATH + "images/ap/save-as.svg",
            'description':window.Apromore.I18N.Save.saveAsDesc,
            'btnId': 'ap-id-editor-save-as-btn',
            'index':2,
            'minShape':0,
            'maxShape':0
        });
    }

    updateTitle() {}

    onUnLoad() {}

    /**
     * Saves the current process to the server.
     */
    async save(forceNew, event) {
        if (this.saving) {
            return false;
        }

        this.saving = true;

        var xml = await this.facade.getXML();
        var svg = await this.facade.getSVG();

        if (forceNew) {
            if (Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSaveAs) {
                Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSaveAs(xml, svg);
            } else {
                alert("Apromore Save As method is missing!");
            }
        } else {
            if (Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSave) {
                Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSave(xml, svg);
            } else {
                alert("Apromore Save method is missing!");
            }
        }

        this.saving = false;
        return true;
    }

};
