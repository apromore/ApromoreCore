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

ORYX.Plugins.Metrics = Clazz.extend({

    facade: undefined,

    construct: function (facade) {

        arguments.callee.$.construct.apply(this, arguments);

        this.contextPath = "/metrics2";
        this.servletPath = "/metrics2/servlet";

        this.facade = facade;
        this.facade.offer({
            'name': 'Metrics Calculator',
            'functionality': this.measure.bind(this, false),
            'group': 'bpmn-toolkit',
            'icon': this.contextPath +  "/images/metrics.png",
            'description': 'Calculate BPMN process model metrics',
            'index': 1
        });
    },

    measure: function () {
        var bpmnXML = this.facade.getXML();
        if (this.facade.getCanvas().getAllElementIds().length == 0) {
            Ext.Msg.show({
                title: "Info",
                msg: "There is nothing to measure.",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.INFO
            }).getDialog().syncSize();
            return;
        }

        if (this.getDiagramType() != 'bpmn') {
                    Ext.Msg.show({
                        title: "Info",
                        msg: "The process must be a BPMN model, current model is " + this.getDiagramType(),
                        buttons: Ext.Msg.OK,
                        icon: Ext.Msg.INFO
                    }).getDialog().syncSize();
                    return;
        }

        var msg = Ext.Msg.wait("Waiting for Metrics servlet to process model.");
        new Ajax.Request(this.servletPath, {
            parameters: {'data': bpmnXML, 'type': this.getDiagramType()},
            method: 'POST',
            asynchronous: true,

            onSuccess: function (data) {
                msg.hide();
                var responseJson = data.responseText.evalJSON(true);
                if (responseJson != null) {
                    if (responseJson.hasOwnProperty("errors")) {
                        this.showErrors(responseJson.errors);
                    } else {
                        this.showMetrics(responseJson);
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

    showMetrics: function (responseJson) {

        var measures = [];
        if (responseJson.hasOwnProperty('Size')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'Size',
                                    value: responseJson['Size'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('CFC')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'CFC',
                                    value: responseJson['CFC'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('ACD')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'ACD',
                                    value: responseJson['ACD'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('MCD')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'MCD',
                                    value: responseJson['MCD'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('CNC')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'CNC',
                                    value: responseJson['CNC'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('Density')) {
            measures.push( new Ext.form.TextField({
                                    fieldLabel: 'Density',
                                    value: responseJson['Density'],
                                    width: 150
                                }));
        }
        if (responseJson.hasOwnProperty('Structuredness')) {
                    measures.push( new Ext.form.TextField({
                                            fieldLabel: 'Structuredness',
                                            value: responseJson['Structuredness'],
                                            width: 150
                                        }));
        }
        if (responseJson.hasOwnProperty('Bonds')) {
                    measures.push( new Ext.form.TextField({
                                            fieldLabel: '#Bonds',
                                            value: responseJson['Bonds'],
                                            width: 150
                                        }));
        }
        if (responseJson.hasOwnProperty('Rigids')) {
                    measures.push( new Ext.form.TextField({
                                            fieldLabel: '#Rigids',
                                            value: responseJson['Rigids'],
                                            width: 150
                                        }));
        }
        if (responseJson.hasOwnProperty('Separability')) {
                            measures.push( new Ext.form.TextField({
                                                    fieldLabel: 'Separability',
                                                    value: responseJson['Separability'],
                                                    width: 150
                                                }));
        }
        if (responseJson.hasOwnProperty('Duplicates')) {
                            measures.push( new Ext.form.TextField({
                                                    fieldLabel: '#Duplicates',
                                                    value: responseJson['Duplicates'],
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
            title: "Metrics",
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
        return("bpmn");
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
