/**
 * Copyright (c) 2008
 * Stefan Krumnow
 * 
 * Copyright (c) 2012
 * Felix Mannhardt
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 *  Location of Import and Export Servlet
 *  This was located in config.js in Oryx  
 **/
ORYX.CONFIG.EPMLIMPORTURL = ORYX.CONFIG.ROOT_PATH + "epmlimport";

/**
 * Supports EPCs by offering export and import ability..
 */
ORYX.Plugins.EPCSupport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		this.facade = facade;
		
		// Progress dialog on export and import
		this.progressDialog = null;		
		
		/*
		this.facade.offer({
			'name':ORYX.I18N.EPCSupport.exp,
			'functionality': this.exportEPC.bind(this),
			'group': ORYX.I18N.EPCSupport.group,
			'icon': ORYX.PATH + "images/epml_export_icon.png",
			'description': ORYX.I18N.EPCSupport.expDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0});
			
		 */
		
		this.facade.offer({
			'name':ORYX.I18N.EPCSupport.imp,
			'functionality': this.importEPC.bind(this),
			'group': ORYX.I18N.EPCSupport.group,
			'icon': ORYX.PATH + "images/epml_import_icon.png",
			'description': ORYX.I18N.EPCSupport.impDesc,
			'index': 2,
			'minShape': 0,
			'maxShape': 0});
	
	},

	
	/**
	 * Imports an AML or EPML description
	 */
	importEPC: function(){
		this._showImportDialog();
	},		

	
	/**
	 * Exports the diagram into an AML or EPML file
	 */
	exportEPC: function(){

		//TODO
		
    },

	/**
	 * Show the Import Dialog
	 */		
	_showImportDialog : function() {

		var form = new Ext.form.FormPanel({
			baseCls : 'x-plain',
			labelWidth : 50,
			defaultType : 'textfield',
			items : [ {
				text : ORYX.I18N.EPCSupport.selectFile,
				style : 'font-size:12px;margin-bottom:10px;display:block;',
				anchor : '100%',
				xtype : 'label'
			}, {
				fieldLabel : ORYX.I18N.EPCSupport.file,
				name : 'subject',
				inputType : 'file',
				style : 'margin-bottom:10px;display:block;',
				itemCls : 'ext_specific_window_overflow'
			}, {
				xtype : 'textarea',
				hideLabel : true,
				name : 'msg',
				anchor : '100% -63'
			} ]
		});

		// Create the panel
		var dialog = new Ext.Window({
			autoCreate : true,
			layout : 'fit',
			plain : true,
			bodyStyle : 'padding:5px;',
			title : ORYX.I18N.EPCSupport.imp,
			height : 350,
			width : 500,
			modal : true,
			fixedcenter : true,
			shadow : true,
			proxyDrag : true,
			resizable : true,
			items : [ form ],
			buttons : [ {
				text : ORYX.I18N.EPCSupport.impBtn,
				handler : function() {

					this.progressDialog = Ext.MessageBox.progress(ORYX.I18N.EPCSupport.progressImp);					
					
					window.setTimeout(function() {

						var epmlString = form.items.items[2].getValue();
						
						Ext.Ajax.request({
							url : ORYX.CONFIG.EPMLIMPORTURL,
							method : 'POST',
							success : function(request) {
								
								this.facade.importJSON(request.responseText);
								this.progressDialog.hide();
									
							}.bind(this),
							failure : function() {
								this.progressDialog.hide();
								Ext.Msg.alert(ORYX.I18N.EPCSupport.error);
							},
							params : {
								data : epmlString,
								action : "Import"
							}
						});

					}.bind(this), 100);
					
					dialog.hide();

				}.bind(this)
			}, {
				text : ORYX.I18N.EPCSupport.close,
				handler : function() {

					dialog.hide();

				}.bind(this)
			} ]
		});

		// Destroy the panel when hiding
		dialog.on('hide', function() {
			dialog.destroy(true);
			delete dialog;
		});

		// Show the panel
		dialog.show();

		// Adds the change event handler to
		form.items.items[1].getEl().dom.addEventListener('change',
				function(evt) {
					var reader = new FileReader();	
					var file = evt.target.files[0];
					//Bugfix for Chrome
					if(typeof(FileReader.prototype.addEventListener) === "function") {
						reader.addEventListener("loadend", function(evt) {						
							form.items.items[2].setValue(evt.target.result);
						}, false);
						
					} else {
						reader.onload = function(evt) {						
							form.items.items[2].setValue(evt.target.result);
						};						
					}
					reader.readAsText(file,"UTF-8");
				}, true)

	}
    
	
});