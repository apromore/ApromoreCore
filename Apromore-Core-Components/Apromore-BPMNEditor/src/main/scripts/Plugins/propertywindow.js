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


if(!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}

ORYX.Plugins.PropertyWindow = {

	facade: undefined,

	construct: function(facade) {
		// Reference to the Editor-Interface
		this.facade = facade;
		this.init();
	},

	init: function(){

		// The parent div-node of the grid
		this.node = ORYX.Utils.graft("http://www.w3.org/1999/xhtml",
			null,
			['div']);

		// the properties array
		this.popularProperties = [];
		this.properties = [];

		/* The currently selected shapes whos properties will shown */
		this.shapeSelection = new Hash();
		this.shapeSelection.shapes = [];
		this.shapeSelection.commonProperties = [];
		this.shapeSelection.commonPropertiesValues = new Hash();

		this.suppressedProperties = new Hash();
		this.updaterFlag = false;

        // var metaData = this.facade.getModelMetaData();
        // var langs = (metaData.languages || []).sort(function (k, h) {
        //     return k.position - h.position
        // }).pluck("rel");
        // this.defaultLanguage = langs.first();
        // this.languages = langs;

        // creating the store for the model.
        	this.dataSource = new Ext.data.GroupingStore({
			proxy: new Ext.data.MemoryProxy(this.properties),
			reader: new Ext.data.ArrayReader({}, [
				{name: 'popular'},
				{name: 'name'},
				{name: 'value'},
				{name: 'icons'},
				{name: 'gridProperties'}
			]),
			sortInfo: {field: 'popular', direction: "ASC"},
			sortData : function(f, direction){
		        direction = direction || 'ASC';
		        var st = this.fields.get(f).sortType;
		        var fn = function(r1, r2){
		            var v1 = st(r1.data[f]), v2 = st(r2.data[f]);
					var p1 = r1.data['popular'], p2  = r2.data['popular'];
		            return p1 < p2 ? -1 : (p1 > p2 ? 1 : (v1 > v2 ? 1 : (v1 < v2 ? -1 : 0)));
		        };
		        this.data.sort(direction, fn);
		        if(this.snapshot && this.snapshot != this.data){
		            this.snapshot.sort(direction, fn);
				}
		    },
			groupField: 'popular'
        	});
		this.dataSource.load();
		var dataSource = this.dataSource;
		var parent = this;

		// creating the column model of the grid.
        this.columnModel = new Ext.grid.ColumnModel({
		    columns: [
			{
				//id: 'name',
				header: ORYX.I18N.PropertyWindow.name,
				dataIndex: 'name',
				width: 90,
				sortable: true,
				renderer: this.tooltipRenderer.bind(this)
			}, {
				//id: 'value',
				header: ORYX.I18N.PropertyWindow.value,
				dataIndex: 'value',
				id: 'propertywindow_column_value',
				width: 110,
				editor: new Ext.form.TextField({
					allowBlank: false
				}),
				renderer: this.renderer.bind(this)
			},
			{
				header: "Pop",
				dataIndex: 'popular',
				hidden: true,
				sortable: true
			}
		    ],
		    isCellEditable: function(col,row) {
			var record = dataSource.getAt(row);
			if (parent.suppressedProperties[record.data.gridProperties.propId]) { return false; }
			return Ext.grid.ColumnModel.prototype.isCellEditable.call(this, col, row);
		    }
		});

		this.grid = new Ext.grid.EditorGridPanel({
			clicksToEdit: 1,
			stripeRows: true,
			autoExpandColumn: "propertywindow_column_value",
			width:'auto',
			// the column model
			colModel: this.columnModel,
			enableHdMenu: false,
			view: new Ext.grid.GroupingView({
				forceFit: true,
				groupTextTpl: '{[values.rs.first().data.popular == 1 ? ORYX.I18N.PropertyWindow.oftenUsed : values.rs.first().data.popular == 2 ? "Configuration attributes" : ORYX.I18N.PropertyWindow.moreProps]}'
			}),

			// the data store
			store: this.dataSource

		});

		region = this.facade.addToRegion('east', new Ext.Panel({
			width: 220,
			layout: "fit",
			border: false,
			title: 'Properties',
			items: [
				this.grid
			]
		}), ORYX.I18N.PropertyWindow.title);

		// Register on Events
		this.grid.on('beforeedit', this.beforeEdit, this, true);
		this.grid.on('afteredit', this.afterEdit, this, true);
		this.grid.view.on('refresh', this.hideMoreAttrs, this, true);

		//this.grid.on(ORYX.CONFIG.EVENT_KEYDOWN, this.keyDown, this, true);

		// Renderer the Grid
		this.grid.enableColumnMove = false;
		//this.grid.render();

		// Sort as Default the first column
		//this.dataSource.sort('name');

	},

    reduceLanguage: function (lang) {
        return lang
    },

    getActiveLanguage: function () {
        return this.facade.getCanvas().getLanguage()
    },

    getAllLanguages: function () {

    },

    // Select the Canvas when the editor is ready
	selectDiagram: function() {

	},

	specialKeyDown: function(field, event) {

	},

	tooltipRenderer: function(value, p, record) {

	},

	renderer: function(value, p, record) {

	},

	beforeEdit: function(option) {

	},

	afterEdit: function(option) {

	},

	// Cahnges made in the property window will be shown directly
	editDirectly:function(key, value){

	},

	// if a field becomes invalid after editing the shape must be restored to the old value
	updateAfterInvalid : function(key) {

	},

	// extended by Kerstin (start)
	dialogClosed: function(data) {

	},
	// extended by Kerstin (end)

	/**
	 * Changes the title of the property window panel according to the selected shapes.
	 */
	setPropertyWindowTitle: function() {

	},
	/**
	 * Sets this.shapeSelection.commonPropertiesValues.
	 * If the value for a common property is not equal for each shape the value
	 * is left empty in the property window.
	 */
	setCommonPropertiesValues: function() {

    },

    getStencilSetOfSelection: function () {

    },

    /**
     * Identifies the common Properties of the selected shapes.
     */
    identifyCommonProperties: function() {

    },

    isTranslation: function (stensil) {

    }
};

ORYX.Plugins.PropertyWindow = Clazz.extend(ORYX.Plugins.PropertyWindow);
