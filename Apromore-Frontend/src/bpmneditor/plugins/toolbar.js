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

import Log from './../logger';

export default class Toolbar {
    /**
     *
     * @param facade: EditorApp facade
     * @param ownPluginData
     *  {
     *      source: 'source file',
     *      name: 'plugin name'
     *  }
     */
    constructor(facade, ownPluginData) {
        this.plugs = [];
        this.facade = facade;

        Ext.QuickTips.init();

        this.buttons = [];
    }

    /**
     * Callback from EditorApp to register pluginsData
     * Provide an oppertunity for a plugin to work on other plugins, like this Toolbar
     * @param pluginsData: array of plugin data which is provided from each plugin
     *
     [
            {
                'name': window.Apromore.I18N.View.zoomFitToModel,
                'btnId': 'ap-id-editor-zoomFit-btn',
                'functionality': this.zoomFitToModel.bind(this),
                'icon': CONFIG.PATH + "images/ap/zoom-to-fit.svg",
                'description': window.Apromore.I18N.View.zoomFitToModelDesc,
                'index': 4,
                'groupOrder': the index of the group this button belongs to
                isDisabled: function()
             }
             ...
     ]
     */
    registryChanged(pluginsData) {
        // Sort plugins by group and index
        var plugs =  pluginsData.sortBy((function(value) {
            let compareKey = value.groupOrder * 100 + value.index;
            return compareKey;
        }).bind(this));
        if(plugs.length<1) return;

        this.buttons = [];

        Log.trace("Creating a toolbar.");

        if(!this.toolbar){
            this.toolbar = new Ext.ux.SlicedToolbar({height: 24});
            this.facade.addToRegion("north", this.toolbar, "Toolbar");
        }

        var currentGroupsOrder = this.plugs.last() ? this.plugs.last().group: plugs[0].groupOrder;

        plugs.each((function(plugin) {
            if(!plugin.name) {return}
            this.plugs.push(plugin);

            // Add separator if new group begins
            if(currentGroupsOrder != plugin.groupOrder) {
                this.toolbar.add('-');
                currentGroupsOrder = plugin.groupOrder;
            }

            var options = {
                icon:           plugin.icon,         // icons can also be specified inline
                cls:            'x-btn-icon',       // Class who shows only the icon
                itemId:         plugin.id,
                tooltip:        plugin.description,  // Set the tooltip
                tooltipType:    'title',            // Tooltip will be shown as in the html-title attribute
                handler:        plugin.toggle ? null : plugin.functionality,  // Handler for mouse click
                enableToggle:   plugin.toggle, // Option for enabling toggling
                toggleHandler:  plugin.toggle ? plugin.functionality : null // Handler for toggle (Parameters: button, active)
            }

            if (plugin.btnId) options.id = plugin.btnId
            var button = new Ext.Toolbar.Button(options);
            this.toolbar.add(button);
            button.getEl().onclick = function() {this.blur()}
            plugin['buttonInstance'] = button;
            this.buttons.push(plugin);
        }).bind(this));

        this.enableButtons();
    }

    /**
     * Get toolbar button from the button ordered index on the toolbar
     * @param buttonIndex
     * @returns button data or undefined
     */
    getButtonByIndex(buttonIndex) {
        return (buttonIndex >=0 && buttonIndex < this.buttons.length) ? this.buttons[buttonIndex] : undefined;
    }

    /**
     * Get button from button ID
     * @param buttonId
     * @returns button data or undefined
     */
    getButtonById(buttonId) {
        return this.buttons.find(pluginButton => {return pluginButton.btnId && pluginButton.btnId === buttonId});
    }

    getNumberOfButtons() {
        return this.buttons.length;
    }

    enableButtons() {
        let plugin = this;
        this.buttons.each(function(pluginButton) {
            plugin._enable(pluginButton);

            if (pluginButton.isDisabled && pluginButton.isDisabled()) {
                plugin._disable(pluginButton);
            }

            // Initial state for Undo/Redo buttons is disabled
            if (['ap-id-editor-undo-btn', 'ap-id-editor-redo-btn'].includes(pluginButton.btnId)) {
                plugin._disable(pluginButton);
            }
        });
    }

    /**
     * Notified by the EditorApp about the changes in the command stack of the editor
     * Allow toolbar to change its button status accordingly with the changes in the editor
     * @param canUndo: true if the editor has undo actions, canRedo: true if the editor has redo actions
     */
    editorCommandStackChanged(canUndo, canRedo) {
        let plugin = this;
        this.buttons.each((function(pluginButton){
            if (pluginButton.btnId === 'ap-id-editor-undo-btn') {
                canUndo ? plugin._enable(pluginButton) : plugin._disable(pluginButton);
            }

            if (pluginButton.btnId === 'ap-id-editor-redo-btn') {
                canRedo ? plugin._enable(pluginButton) : plugin._disable(pluginButton);
            }
        }).bind(this));
    }

    _disable(button) {
        button.buttonInstance.disable();
        $('#' + button.btnId).addClass('disabled')
    }

    _enable(button) {
        button.buttonInstance.enable();
        $('#' + button.btnId).removeClass('disabled')
    }
};

Ext.ns("Ext.ux");
Ext.ux.SlicedToolbar = Ext.extend(Ext.Toolbar, {
    currentSlice: 0,
    iconStandardWidth: 22, //22 px
    seperatorStandardWidth: 2, //2px, minwidth for Ext.Toolbar.Fill
    toolbarStandardPadding: 2,

    initComponent: function(){
        Ext.apply(this, {
        });
        Ext.ux.SlicedToolbar.superclass.initComponent.apply(this, arguments);
    },

    onRender: function(){
        Ext.ux.SlicedToolbar.superclass.onRender.apply(this, arguments);
    },

    onResize: function(){
        Ext.ux.SlicedToolbar.superclass.onResize.apply(this, arguments);
    },

});
