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
if(!ORYX){ var ORYX = {} }
if(!ORYX.Plugins){ ORYX.Plugins = {} }

ORYX.Plugins.ApromoreMeasurement = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,

    construct: function (facade) {
        this.facade = facade;

        this.facade.offer({
            'name': 'Calculate Metrics of a BPMN proces model',
            'functionality': this.measure.bind(this, false),
            'group': 'bpmn-toolkit',
            'icon': ORYX.PATH + "images/Measurement.png",
            'description': 'Calculate Metrics of a BPMN proces model',
            'index': 1
        });
    },

    measure: function () {
        var json = this.facade.getSerializedJSON();
        if (this.facade.getCanvas().nodes.size() == 0) {
            Ext.Msg.show({
                title: "Info",
                msg: "There is nothing to measure.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.INFO
            }).getDialog().syncSize();
            return;
        }

        var msg = Ext.Msg.wait("Waiting for Measurement servlet to process model.");
        new Ajax.Request(ORYX.CONFIG.MEASUREMENT_URL, {
            parameters: {'data': json, 'type': this.getDiagramType()},
            method: 'POST',
            asynchronous: true,

            onSuccess: function (data) {
                msg.hide();
                var responseJson = data.responseText.evalJSON(true);
                if (responseJson != null) {
                    if (responseJson.hasOwnProperty("errors")) {
                        this.showErrors(responseJson.errors);
                    } else {
                        this.showMeasurements(responseJson);
                    }
                }
            }.bind(this),

            onFailure: function (data) {
                msg.hide();
                Ext.Msg.show({
                    title: "Error",
                    msg: "The communication with the measurement servlet failed.",
                    buttons: Ext.Msg.OK,
                    icon: Ext.Msg.ERROR
                }).getDialog().syncSize()
            }.bind(this)
        })
    },

    showMeasurements: function (responseJson) {

        var measures = [];
        if (responseJson.hasOwnProperty('size')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'Size',
                                    value: responseJson['size'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('cfc')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'CFC',
                                    value: responseJson['cfc'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('acd')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'ACD',
                                    value: responseJson['acd'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('mcd')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'MCD',
                                    value: responseJson['mcd'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('cnc')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'CNC',
                                    value: responseJson['cnc'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('density')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'Density',
                                    value: responseJson['density'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('structuredness')) {
                    measures.push( new Ext.form.TextField({
                                            fieldLabel: 'Structuredness',
                                            value: responseJson['structuredness'],
                                            width: 150
                                        }));
        }
        if (responseJson.hasOwnProperty('bonds')) {
                    measures.push( new Ext.form.TextField({
                                            fieldLabel: '#Bonds',
                                            value: responseJson['bonds'],
                                            width: 150
                                        }));
        }
        if (responseJson.hasOwnProperty('rigids')) {
                    measures.push( new Ext.form.TextField({
                                            fieldLabel: '#Rigids',
                                            value: responseJson['rigids'],
                                            width: 150
                                        }));
        }
        if (responseJson.hasOwnProperty('separability')) {
                            measures.push( new Ext.form.TextField({
                                                    fieldLabel: 'separability',
                                                    value: responseJson['separability'],
                                                    width: 150
                                                }));
        }
        if (responseJson.hasOwnProperty('duplicates')) {
                            measures.push( new Ext.form.TextField({
                                                    fieldLabel: '#Duplicates',
                                                    value: responseJson['duplicates'],
                                                    width: 150
                                                }));
        }


        var formPanel = new Ext.form.FormPanel({
            baseCls: 'x-plain',
            labelWidth: 100,
            defaultType: 'textfield',
            items: measures
        });

        var formWindow = new Ext.Window({
            resizable: true,
            closeable: true,
            minimizable: false,
            width: 300,
            minHeight: 250,
            layout: "anchor",
            bodyStyle: "background-color:white; color: black; overflow: visible;",
            title: "Measurements",
            items: [formPanel],
            buttons: [
                {
                    text: "OK",
                    handler: function () {
                        this.ownerCt.close()
                    }
                }
            ]
        });
        formWindow.show()
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
                msg: "An error occured while measuring your process.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR
            }).getDialog().syncSize()
        } else {
            Ext.Msg.show({
                title: "Error",
                msg: "It was not possible to measure the process because of some unsupported content.",
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
                buttons: [
                    {
                        text: "OK",
                        handler: function () {
                            this.ownerCt.close()
                        }
                    }
                ]
            });
            b.show()
        }
    }
});
