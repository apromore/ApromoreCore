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
import Log from './../logger';

export default class Toolbar {
    /**
     *
     * @param facade: EditorApp facade
     * @param ownPluginData
     *  {
     *      source: 'source file',
     *      name: 'plugin name',
     *      properties: [{group: 'groupName', index: 'groupOrderNumber'}, {...}] // this is all groups from the configuration file
     *  }
     */
    constructor(facade, ownPluginData) {
        this.plugs = [];
        this.facade = facade;

        this.groupIndex = new Hash(); // key-value pairs
        ownPluginData.properties.each((function(value){
            if(value.group && value.index != undefined) {
                this.groupIndex[value.group] = value.index
            }
        }).bind(this));

        Ext.QuickTips.init();

        this.buttons = [];
    }

    /**
     * Callback from EditorApp to register pluginsData
     * Provide an oppertunity for a plugin to work on other plugins, like this Toolbar
     * @param pluginsData: array of plugin data which is provided from each plugin
     *
         // [
         //     {   (Toolbar) object
         //                 type: 'plugin name',
         //                 engage: true
         //                 },
         //
         //     {  (Undo) object
         //                 type: 'plugin name',
         //                 engage: true
         //                 },
         //     ...
         // ]
     */
    registryChanged(pluginsData) {
        // Sort plugins by group and index
        //TODO: Update this to look through the plugins list
        var newPlugs =  pluginsData.sortBy((function(value) {
            // groupIndex + groupName + buttonIndex, e.g. 1undo1, 1undo2, 1undo3, 2zoom1, 2zoom2
            let compareKey = ((this.groupIndex[value.group] != undefined ? this.groupIndex[value.group] : "" ) + value.group + "" + value.index).toLowerCase();
            return compareKey;
        }).bind(this));

        // Search all plugins that are defined as plugin toolbar buttons or undefined target (meaning for all)
        var plugs = $A(newPlugs).findAll(function(plugin){
                                        return !this.plugs.include(plugin) && (!plugin.target || plugin.target === Apromore.Plugins.Toolbar)
                                    }.bind(this));
        if(plugs.length<1) return;

        this.buttons = [];

        Log.trace("Creating a toolbar.");

        if(!this.toolbar){
            this.toolbar = new Ext.ux.SlicedToolbar({height: 24});
            this.facade.addToRegion("north", this.toolbar, "Toolbar");
        }

        var currentGroupsName = this.plugs.last() ? this.plugs.last().group: plugs[0].group;

        plugs.each((function(plugin) {
            if(!plugin.name) {return}
            this.plugs.push(plugin);

            // Add separator if new group begins
            if(currentGroupsName != plugin.group) {
                this.toolbar.add('-');
                currentGroupsName = plugin.group;
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
        if (buttonIndex >=0 && buttonIndex < this.buttons.length) {
            return this.buttons[buttonIndex];
        }
    }

    /**
     * Get button from button ID
     * @param buttonId
     * @returns button data or undefined
     */
    getButtonById(buttonId) {
        this.buttons.each(function(pluginButton) {
            if (pluginButton.btnId && pluginButton.btnId === buttonId) {
                return pluginButton;
            }
        });
    }

    getNumberOfButtons() {
        return this.buttons.length;
    }

    enableButtons() {
        // Show the Buttons
        this.buttons.each((function(pluginButton){
            pluginButton.buttonInstance.enable();

            // // If there is less elements than minShapes
            // if(pluginButton.minShape && pluginButton.minShape > elements.length)
            //     pluginButton.buttonInstance.disable();
            // // If there is more elements than minShapes
            // if(pluginButton.maxShape && pluginButton.maxShape < elements.length)
            //     pluginButton.buttonInstance.disable();

            // If the plugin button is not enabled
            if(pluginButton.isEnabled && !pluginButton.isEnabled(pluginButton.buttonInstance)) {
                pluginButton.buttonInstance.disable();
            }

            // Initial state for Undo/Redo buttons
            if (['ap-id-editor-undo-btn', 'ap-id-editor-redo-btn'].includes(pluginButton.btnId)) {
                pluginButton.buttonInstance.disable();
            }

        }).bind(this));
    }

    /**
     * Notified by the EditorApp about the changes in the command stack of the editor
     * Allow toolbar to change its button status accordingly with the changes in the editor
     * @param canUndo: true if the editor has undo actions, canRedo: true if the editor has redo actions
     */
    editorCommandStackChanged(canUndo, canRedo) {
        this.buttons.each((function(pluginButton){
            if (pluginButton.btnId === 'ap-id-editor-undo-btn') {
                canUndo ? pluginButton.buttonInstance.enable() : pluginButton.buttonInstance.disable();
            }
            if (pluginButton.btnId === 'ap-id-editor-redo-btn') {
                canRedo ? pluginButton.buttonInstance.enable() : pluginButton.buttonInstance.disable();
            }
        }).bind(this));
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
