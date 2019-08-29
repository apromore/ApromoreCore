/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


Ext.form.FileUploadField = Ext.extend(Ext.form.TextField,  {
    /**
     * @cfg {String} buttonText The button text to display on the upload button (defaults to
     * 'Browse...').  Note that if you supply a value for {@link #buttonCfg}, the buttonCfg.text
     * value will be used instead if available.
     */
    buttonText: 'Browse...',
    /**
     * @cfg {Boolean} buttonOnly True to display the file upload field as a button with no visible
     * text field (defaults to false).  If true, all inherited TextField members will still be available.
     */
    buttonOnly: false,
    /**
     * @cfg {Number} buttonOffset The number of pixels of space reserved between the button and the text field
     * (defaults to 3).  Note that this only applies if {@link #buttonOnly} = false.
     */
    buttonOffset: 3,
    /**
     * @cfg {Object} buttonCfg A standard {@link Ext.Button} config object.
     */

    // private
    readOnly: true,
    
    /**
     * @hide 
     * @method autoSize
     */
    autoSize: Ext.emptyFn,
    
    // private
    initComponent: function(){
        Ext.form.FileUploadField.superclass.initComponent.call(this);
        
        this.addEvents(
            /**
             * @event fileselected
             * Fires when the underlying file input field's value has changed from the user
             * selecting a new file from the system file selection dialog.
             * @param {Ext.form.FileUploadField} this
             * @param {String} value The file value returned by the underlying file input field
             */
            'fileselected'
        );
    },
    
    // private
    onRender : function(ct, position){
        Ext.form.FileUploadField.superclass.onRender.call(this, ct, position);
        
        this.wrap = this.el.wrap({cls:'x-form-field-wrap x-form-file-wrap'});
        this.el.addClass('x-form-file-text');
        this.el.dom.removeAttribute('name');
        
        this.fileInput = this.wrap.createChild({
            id: this.getFileInputId(),
            name: this.name||this.getId(),
            cls: 'x-form-file',
            tag: 'input', 
            type: 'file',
            size: 1
        });
        
        var btnCfg = Ext.applyIf(this.buttonCfg || {}, {
            text: this.buttonText
        });
        this.button = new Ext.Button(Ext.apply(btnCfg, {
            renderTo: this.wrap,
            cls: 'x-form-file-btn' + (btnCfg.iconCls ? ' x-btn-icon' : '')
        }));
        
        if(this.buttonOnly){
            this.el.hide();
            this.wrap.setWidth(this.button.getEl().getWidth());
        }
        
        this.fileInput.on('change', function(){
            var v = this.fileInput.dom.value;
            this.setValue(v);
            this.fireEvent('fileselected', this, v);
        }, this);
    },
    
    // private
    getFileInputId: function(){
        return this.id+'-file';
    },
    
    // private
    onResize : function(w, h){
        Ext.form.FileUploadField.superclass.onResize.call(this, w, h);
        
        this.wrap.setWidth(w);
        
        if(!this.buttonOnly){
            var w = this.wrap.getWidth() - this.button.getEl().getWidth() - this.buttonOffset;
            this.el.setWidth(w);
        }
    },
    
    // private
    preFocus : Ext.emptyFn,
    
    // private
    getResizeEl : function(){
        return this.wrap;
    },

    // private
    getPositionEl : function(){
        return this.wrap;
    },

    // private
    alignErrorIcon : function(){
        this.errorIcon.alignTo(this.wrap, 'tl-tr', [2, 0]);
    }
    
});
Ext.reg('fileuploadfield', Ext.form.FileUploadField);
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



ORYX.Plugins.LogAnimation = ORYX.Plugins.AbstractPlugin.extend({

    construct: function () {

        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);

        this.facade.offer({
            'name': 'Animate',
            'functionality': this.showDialog.bind(this),
            'group': 'Configuration',
            'icon': "/loganimation/images/icon.svg",
            'description': 'Animate logs...',
            'index': 1
        });
    },

    showDialog: function(){

        var json = this.facade.getJSON();
        json = Ext.encode(json);

        var element = document.createElement("input");
        element.setAttribute("type", "hidden");
        element.setAttribute("value", json);
        element.setAttribute("id", "jsonForAnimation");
        document.body.appendChild(element);

        // Create the form to present
        var fp = new Ext.FormPanel({
            //renderTo: 'fi-form',
            fileUpload: true,
            width: 500,
            frame: true,
            autoHeight: true,
            bodyStyle: 'padding: 10px 10px 0 10px;',
            labelWidth: 50,
            defaults: {
                anchor: '95%',
                allowBlank: false,
                msgTarget: 'side'
            },
            items: [new Ext.form.Hidden({
                id: 'json',
                name: 'json',
                value: element.getAttribute("value")
            }), new Ext.form.FileUploadField({
                id: 'form-file',
                emptyText: 'Select a process log',
                fieldLabel: 'Log file',
                name: 'log',
                buttonCfg: {
                    text: '',
                    iconCls: 'upload-icon'
                }
            }), new Ext.ux.ColorField({
                fieldLabel: 'Color',
                name: "color",
                value: "#7777FF"
            })]
        });

        // Present the form to the user
        var dialog = new Ext.Window({
            autoCreate: true,
            layout: 'fit',
            plain: true,
            bodyStyle: 'padding:5px;',
            title: 'Select log files for animation',
            height: 350,
            width: 500,
            modal: true,
            fixedcenter: true,
            shadow: true,
            proxyDrag: true,
            resizable: true,
            items: [fp],
            buttons: [{
                text: 'Animate',
                handler: function(){
                    if(fp.getForm().isValid()){  // TODO: find a better war to prevent isValid from being undefined
                        fp.getForm().submit({
                            url: '/loganimation/bpmnanimation',
                            params: {
                                newStatus: 'delivered'
                            },
                            waitMsg: 'Uploading process log(s)...',
                            success: function(fp, action) {
                                action.result.success = undefined;  // only had this property to humor Ext2JS's file uploader
                                var data = Ext.encode(action.result);
                                var newWindow = window.open('/loganimation/animateLogInSignavio.zul' + location.search);
                                newWindow.animationData = data;
                                window.close();
                            },
                            failure:  function(fp, action) {
                                switch (action.failureType) {
                                case Ext.form.Action.CLIENT_INVALID:
                                    Ext.Msg.alert("Failure", "Form fields may not be submitted with invalid values");
                                    break;
                                case Ext.form.Action.CONNECT_FAILURE:
                                    Ext.Msg.alert("Failure", "Ajax communication failed");
                                    break;
                                case Ext.form.Action.SERVER_INVALID:
                                    Ext.Msg.alert("Failure", "Server Error. Please read the server log."); //action.result.errors.errormsg);
                                    break;
                                default:
                                    Ext.Msg.alert("Failure", "Unknown server failure code: " + action.failureType);
                                }
                            }
                        });
                    }
                }
            }, {
                text: 'Add log',
                handler: function(){
                    fp.add(new Ext.form.FileUploadField({
                                emptyText: 'Select a process log',
                                fieldLabel: 'Log file',
                                name: 'log',
                                buttonCfg: {
                                    text: '',
                                    iconCls: 'upload-icon'
                                }
                        })
                    );
                    fp.add(new Ext.ux.ColorField({
                                fieldLabel: 'Color',
                                name: "color",
                                value: (dialog.defaultLogColors.length > 0) ? dialog.defaultLogColors.pop() : "black"
                        })
                    );
                    fp.doLayout(false);
                }
            }, {
                text: 'Reset',
                handler: function(){
                    fp.getForm().reset();
                }
            }, {
                text: ORYX.I18N.JSONSupport.imp.btnClose,
                handler: function(){
                    dialog.close();
                }.bind(this)
            }]
        });

        dialog.defaultLogColors = ["#FF00FF" /* fuschia */, "#00CCFF" /* azure */, "FFCC00" /* amber */, "#00CC00" /* green */];
        dialog.show();
    }
});
