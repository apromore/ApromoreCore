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
if (!ORYX.Plugins) {
    ORYX.Plugins = new Object();
}

ORYX.Plugins.ApromoreBpstruct = Clazz.extend({

    facade: undefined,

    construct:function (facade) {
        this.facade = facade;

        this.facade.offer({
            'name':ORYX.I18N.BPStruct.upload,
            'functionality':this.showInfoDialog.bind(this, false),
            'group':ORYX.I18N.Bimp.group,
            'icon':ORYX.PATH + "images/BPStruct.png",
            'description':ORYX.I18N.BPStruct.uploadDesc,
            'index':1
        });
    },

    /** Show a dialog box with some info about what is going to happen! */
    showInfoDialog: function () {
        this.dialog = new Ext.Window({
            resizable: true,
            closeable: true,
            minimizable: false,
            modal: true,
            width: 600,
            minHeight: 300,
            layout: "anchor",
            bodyStyle: "background-color:white; padding: 10px; color: black; overflow: visible;",
            title: "BPStruct",
            html: '<style>.format p { margin-bottom: 10px; } </style><div class="format" style="width: 100%; position: relative; left: 0; top: 0; float: left;"><p style="text-align: justify;">BPStruct is a tool for transforming unstructured programs/service compositions/(business) process models (models of concurrency) into well-structured ones. A model is well-structured, if for every node with multiple outgoing arcs (a split) there is a corresponding node with multiple incoming arcs (a join), and vice versa, such that the fragment of the model between the split and the join forms a single-entry-single-exit (SESE) component; otherwise the model is unstructured. The transformation preserves concurrency in resulting well-structured models.</p></div><div style="clear: both;"></div>',
            buttons: [{
                text: "Transform",
                handler: this.bpstruct.bind(this)
            }, {
                text: "Cancel",
                handler: function () {
                    this.ownerCt.close()
                }
            }]
        });
        this.dialog.show()
    },

    bpstruct: function () {
        if (this.dialog) {
            this.dialog.close()
        }

        var json = this.facade.getSerializedJSON();
        if (this.facade.getCanvas().nodes.size() == 0) {
            Ext.Msg.show({
                title: "Info",
                msg: "There is nothing to structure.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.INFO
            }).getDialog().syncSize();
            return
        }

        var msg = Ext.Msg.wait("Waiting for BPStruct to process model.");
        new Ajax.Request('/editor/editor/bpstruct', {
            parameters: {'data': json, 'type': this.getDiagramType()},
            method: 'POST',
            asynchronous: true,

            onSuccess: function(data) {
                msg.hide();
                var responseJson = data.responseText.evalJSON(true);
                if (responseJson != null) {
                    if (responseJson.hasOwnProperty("errors")) {
                        this.showErrors(responseJson.errors)
                    } else {
                        if (responseJson.hasChanged) {
                            if (responseJson.hasOwnProperty("data_json")) {
                                this.replaceProcess(responseJson.data_json)
                            }
                        } else {
                            this.showCouldntStructure()
                        }
                    }
                }
            }.bind(this),

            onFailure: function (data) {
                msg.hide();
                Ext.Msg.show({
                    title: "Error",
                    msg: "The communication with the BPStruct failed.",
                    buttons: Ext.Msg.OK,
                    icon: Ext.Msg.ERROR
                }).getDialog().syncSize()
            }.bind(this)
        })
    },

    replaceProcess: function (newModel) {
        var comd = ORYX.Core.Command.extend({
            construct: function (newFacade, process) {
                this.facade = newFacade;
                this.oldProcess = newFacade.getJSON();
                this.newProcess = process
            },
            execute: function () {
                this.facade.getCanvas().getChildShapes().each(function (process) {
                    this.facade.getCanvas().remove(process)
                }.bind(this));
                this.facade.importJSON(this.newProcess)
            },
            rollback: function () {
                this.facade.getCanvas().getChildShapes().each(function (process) {
                    this.facade.getCanvas().remove(process)
                }.bind(this));
                this.facade.importJSON(this.oldProcess)
            }
        });

        this.facade.executeCommands([new comd(this.facade, newModel)]);
        this.facade.setSelection([]);
        Ext.Msg.show({
            title: "Result",
            msg: "Your process was structured successfully.",
            buttons: Ext.Msg.OK,
            icon: Ext.Msg.INFO
        }).getDialog().syncSize()
    },

    getDiagramType: function () {
        switch (this.facade.getCanvas().getStencil().namespace()) {
            case "http://b3mn.org/stencilset/bpmn1.1#":
                return("xpdl");
            case "http://b3mn.org/stencilset/bpmn2.0#":
                return("bpmn");
            case "http://b3mn.org/stencilset/epc#":
                return("epc");
            case "http://b3mn.org/stencilset/yawl2.2#":
                return("yawl");
            default:
                return("");
        }
    },

    showErrors: function (errors) {
        if (errors.size() == 0) {
            Ext.Msg.show({
                title: "Error",
                msg: "An error occured while structuring your process.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR
            }).getDialog().syncSize()
        } else {
            Ext.Msg.show({
                title: "Error",
                msg: "It was not possible to structure the process because of some unsupported content.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR
            }).getDialog().syncSize();
            var a = new Ext.ux.grid.ErrorGridPanel(errors, this.facade);
            var b = new Ext.Window({
                resizable: true,
                closeable: true,
                minimizable: false,
                width: 300,
                minHeight: 250,
                x: 0,
                layout: "anchor",
                bodyStyle: "background-color:white; color: black; overflow: visible;",
                title: "Errors that occurred",
                items: a,
                buttons: [{
                    text: "OK",
                    handler: function () {
                        this.ownerCt.close()
                    }
                }]
            });
            b.show()
        }
    },

    showCouldntStructure: function () {
        Ext.Msg.show({
            title: "Result",
            msg: "It was not possible to structure the process",
            buttons: Ext.Msg.OK,
            icon: Ext.Msg.INFO
        }).getDialog().syncSize()
    }
});
