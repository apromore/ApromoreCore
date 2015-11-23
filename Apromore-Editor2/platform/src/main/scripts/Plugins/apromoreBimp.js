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

ORYX.Plugins.ApromoreBimp = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,

    construct:function (facade) {
        this.facade = facade;

        this.facade.offer({
            'name':ORYX.I18N.Bimp.upload,
            'functionality':this.sendToBimp.bind(this, false),
            'group':ORYX.I18N.Bimp.group,
            'icon':ORYX.PATH + "images/BIMP.png",
            'description':ORYX.I18N.Bimp.uploadDesc,
            'index':1
        });
    },

    /** Sends the current process to the BIMP Simulator. */
    sendToBimp:function () {
        if (this.getDiagramType() == 'xpdl' || this.getDiagramType() == 'bpmn') {
            this.form = new Ext.form.FormPanel({
                url: ORYX.CONFIG.BIMP_URL,
                method: "POST",
                items: new Ext.form.Hidden({
                    name: "file"
                }),
                bodyStyle: "background-color:white; border: none;",
                submit: function () {
                    var dom = this.getForm().getEl().dom;
                    var doc = document.createAttribute("target");
                    doc.nodeValue = "_blank";
                    dom.setAttributeNode(doc);
                    dom.action = this.getForm().url;
                    dom.submit()
                }
            });

            var formPane = new Ext.Panel({
                bodyStyle: "background-color:white; border: none;",
                layout: "anchor",
                html: '<style type="text/css"> .format p { margin-bottom: 10px; }</style><div class="format" style="width: 100%; position: relative; left: 0; top: 0; float: left;"><p style="text-align: justify;">Web based business process simulator is easy to use and can be accessed directly from a web browser.</p> <p style="text-align: justify;">You must have a business process diagram in BPMN 2.0 or Visio 2013 BPMN (VSDX) format. With the web based simulator interface you can upload your business process diagram and add the additional information that is required for simulation. Additional information like the number of process instances, their arrival rate, activity durations, amount of various resources, path execution probabilities and etc is essential for the simulation.</p> <p style="text-align: justify;">After the simulation data has been entered, the simulation can be started. You can also enable MXML log creation to produce the logs in a standard XML format that can be downloaded and analyzed further with other process mining tools.</p> <p style="text-align: justify;">From the results you can analyze the performance of the business process. You can download the process model with the simulation data to be simulated later again. You can always go back modify the entered simulation specific information and run the simulation again.</p> <p style="text-align: justify;">Online Business Process Simulator is free for academic use, contact us for commercial use.</p> </div><div style="clear:both;"></div>'
            });

            this.window = new Ext.Window({
                title: "BIMP Simulator",
                bodyStyle: "background-color:white; padding: 10px; color: black; overflow: visible;",
                resizable: true,
                closeable: true,
                minimizable: false,
                modal: true,
                width: 600,
                minHeight: 300,
                items: [formPane, this.form],
                buttons: [{
                    text: "Simulate",
                    disabled: true,
                    handler: function () {
                        this.ownerCt.items.item(1).submit();
                        this.ownerCt.close()
                    }
                }, {
                    text: "Cancel",
                    handler: function () {
                        this.ownerCt.close()
                    }
                }]
            });

            new Ajax.Request(this.getExportUrl(this.getDiagramType()), {
                method: "POST",
                parameters: {'data': this.facade.getSerializedJSON()},
                asynchronous: true,
                encoding: "UTF-8",
                requestHeaders: {
                    Accept: "application/json"
                },

                onSuccess: function (transport) {
                    if (transport != null) {
                        this.form.items.item(0).getEl().dom.value = transport.responseText;
                        this.window.buttons[0].enable()
                    } else {
                        Ext.Msg.show({
                            title: "Error",
                            msg: "An error occured while loading your process.",
                            buttons: Ext.Msg.OK,
                            icon: Ext.Msg.ERROR
                        }).getDialog().syncSize();
                        this.window.close()
                    }
                }.bind(this),

                onFailure: function (data) {
                    Ext.Msg.show({
                        title: "Error",
                        msg: "An error occured while loading your process.",
                        buttons: Ext.Msg.OK,
                        icon: Ext.Msg.ERROR
                    }).getDialog().syncSize();
                    this.window.close()
                }.bind(this)
            });

            this.window.show()
        } else {
            Ext.Msg.show({
                title: "Error",
                msg: "The BIMP Simulator only works with BPMN models!",
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR
            }).getDialog().syncSize();
        }
    },

    getDiagramType: function() {
        switch (this.facade.getCanvas().getStencil().namespace()) {
            case "http://b3mn.org/stencilset/bpmn1.1#":
                return("xpdl");
            case "http://b3mn.org/stencilset/bpmn2.0#":
                return("bpmn");
            case "http://b3mn.org/stencilset/epc#":
                return("epml");
            case "http://b3mn.org/stencilset/yawl2.2#":
                return("yawl");
            default:
                return("");
        }
    },

    getExportUrl: function(namespace) {
        switch (namespace) {
            case "xpdl":
                return ORYX.CONFIG.EDITOR_PATH + '/xpdlexport';
            case "bpmn":
                return ORYX.CONFIG.EDITOR_PATH + '/bpmnexport';
            case "epml":
                return ORYX.CONFIG.EDITOR_PATH + '/epmlexport';
            case "yawl":
                return ORYX.CONFIG.EDITOR_PATH + '/yawlexport';
            default:
                return("");
        }
    }

});