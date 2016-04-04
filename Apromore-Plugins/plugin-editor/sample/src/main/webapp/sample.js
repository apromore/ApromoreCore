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

ORYX.Plugins.Sample = ORYX.Plugins.AbstractPlugin.extend({

    construct: function () {

        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);

        this.contextPath = "/sample";
        this.servletPath = "/sample/servlet";

        this.facade.offer({
            'name': 'Example',
            'functionality': this.measure.bind(this),
            'group': 'Example',
            'icon': this.contextPath + "/images/sample.png",
            'description': 'Example plug-in',
            'index': 1
        });
    },

    measure: function () {

        var msg = Ext.Msg.wait("Waiting for sample servlet to return result ...");
        new Ajax.Request(this.servletPath, {
            parameters: {'type': "sample"},
            method: 'POST',
            asynchronous: true,

            onSuccess: function (data) {
                msg.hide();
                Ext.Msg.show({
                    title: "Success",
                    msg: data.responseText,
                    buttons: Ext.Msg.OK,
                    icon: Ext.Msg.ERROR
                }).getDialog().syncSize()
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

    }

});
