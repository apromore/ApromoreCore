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

ORYX.Plugins.Edit = Clazz.extend({

    construct: function(facade){

        this.facade = facade;
        this.clipboard = new ORYX.Plugins.Edit.ClipBoard();

        //this.facade.registerOnEvent(ORYX.CONFIG.EVENT_KEYDOWN, this.keyHandler.bind(this));

        this.facade.offer({
         name: ORYX.I18N.Edit.cut,
         description: ORYX.I18N.Edit.cutDesc,
         icon: ORYX.PATH + "images/cut.png",
		 keyCodes: [{
				metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
				keyCode: 88,
				keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
			}
		 ],
         functionality: this.callEdit.bind(this, this.editCut),
         group: ORYX.I18N.Edit.group,
         index: 1,
         minShape: 1
         });

        this.facade.offer({
         name: ORYX.I18N.Edit.copy,
         description: ORYX.I18N.Edit.copyDesc,
         icon: ORYX.PATH + "images/page_copy.png",
		 keyCodes: [{
				metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
				keyCode: 67,
				keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
			}
		 ],
         functionality: this.callEdit.bind(this, this.editCopy, [true, false]),
         group: ORYX.I18N.Edit.group,
         index: 2,
         minShape: 1
         });

        this.facade.offer({
         name: ORYX.I18N.Edit.paste,
         description: ORYX.I18N.Edit.pasteDesc,
         icon: ORYX.PATH + "images/page_paste.png",
		 keyCodes: [{
				metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
				keyCode: 86,
				keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
			}
		 ],
         functionality: this.callEdit.bind(this, this.editPaste),
         isEnabled: this.clipboard.isOccupied.bind(this.clipboard),
         group: ORYX.I18N.Edit.group,
         index: 3,
         minShape: 0,
         maxShape: 0
         });

        this.facade.offer({
            name: ORYX.I18N.Edit.del,
            description: ORYX.I18N.Edit.delDesc,
            icon: ORYX.PATH + "images/cross.png",
			keyCodes: [{
					metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
					keyCode: 8,
					keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
				},
				{
					keyCode: 46,
					keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
				}
			],
            functionality: this.callEdit.bind(this, this.editDelete),
            group: ORYX.I18N.Edit.group,
            index: 4,
            minShape: 1
        });
    },

	callEdit: function(fn, args){

	},

	/**
	 * Handles the mouse down event and starts the copy-move-paste action, if
	 * control or meta key is pressed.
	 */
	handleMouseDown: function(event) {

	},


    /**
     * Returns a list of shapes which should be considered while copying.
     * Besides the shapes of given ones, edges and attached nodes are added to the result set.
     * If one of the given shape is a child of another given shape, it is not put into the result.
     */
    getAllShapesToConsider: function(shapes){

    },

    /**
     * Performs the cut operation by first copy-ing and then deleting the
     * current selection.
     */
    editCut: function(){
        //TODO document why this returns false.
        //TODO document what the magic boolean parameters are supposed to do.

        this.editCopy(false, true);
        this.editDelete(true);
        return false;
    },

    /**
     * Performs the copy operation.
     * @param {Object} will_not_update ??
     */
    editCopy: function( will_update, useNoOffset ){

    },

    /**
     * Performs the paste operation.
     */
    editPaste: function(){

    },

    /**
     * Performs the delete operation. No more asking.
     */
    editDelete: function(){

    }
});

ORYX.Plugins.Edit.ClipBoard = Clazz.extend({
    construct: function(){

    },
    isOccupied: function(){

    },
    refresh: function(selection, shapes, namespace, useNoOffset){

    }
});
