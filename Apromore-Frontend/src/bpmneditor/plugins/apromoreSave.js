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
import Log from "../logger";

export default class ApromoreSave {

    constructor (facade) {
        this.changeSymbol = "*";
        this.facade = facade;

        this.facade.offer({
            'name':window.Apromore.I18N.Save.save,
            'btnId': 'ap-id-editor-save-btn',
            'functionality':this.save.bind(this, false),
            "groupOrder": 0,
            'icon':CONFIG.PATH + "images/ap/save.svg",
            'description':window.Apromore.I18N.Save.saveDesc,
            'index':1,
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
            'icon':CONFIG.PATH + "images/ap/save-as.svg",
            'description':window.Apromore.I18N.Save.saveAsDesc,
            'btnId': 'ap-id-editor-save-as-btn',
            "groupOrder": 0,
            'index':2
        });
    }

    /**
     * Saves the current process to the server.
     */
    async save(forceNew, event) {
        if (this.saving) {
            return false;
        }

        this.saving = true;

        let xml = await this.facade.getXML().catch(err => {Log.error(err.message)});

        let svg = await this.facade.getSVG().catch(err => {Log.error(err.message)});

        try {
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
        }
        catch (err) {
            Log.error(`Error in calling method of Apromore.BPMNEditor.Plugins.ApromoreSave. Error: ${err.message}`);
            throw err;
        }

        this.saving = false;
        return true;
    }

};
