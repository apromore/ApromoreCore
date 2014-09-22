if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

/**
   Presents a dialog which can perform a query and select a subset of the displayed process model.
   @class ORYX.Plugins.SelectionExtension
   @constructor Creates a new instance
   @extends ORYX.Plugins.AbstractPlugin
*/
ORYX.Plugins.SelectionExtension = ORYX.Plugins.AbstractPlugin.extend({

    construct: function(){
        arguments.callee.$.construct.apply(this, arguments);
                
        this.facade.offer({
            name: "ORYX.I18N.SelectExtension.name",
            functionality: this.showDialog.bind(this),
            group: "ORYX.I18N.SelectionExtension.group",
            icon: ORYX.PATH + "images/selection_extension.png",
            description: "ORYX.I18N.SelectionExtension.desc",
            index: 0,
            minShape: 0,
            maxShape: 0
        });
    },
    
    showDialog: function(){

	var form = new Ext.form.FormPanel({
            baseCls: 'x-plain',
            labelWidth: 50,
            defaultType: 'textfield',
            items: [{
                text: "Selected variants:",
                style: 'font-size:12px;margin-bottom:10px;display:block;',
                anchor: '100%',
                xtype: 'label'
            }, {
                fieldLabel: "Variants",
                boxLabel: "BNE",
		hideLabel: true,
                name: 'variants',
                xtype: 'checkbox'
            }, {
                boxLabel: "MEL",
		hideLabel: true,
                name: 'variants',
                xtype: 'checkbox'
            }, {
                boxLabel: "OOL",
		hideLabel: true,
                name: 'variants',
                xtype: 'checkbox'
            }, {
                boxLabel: "PER",
		hideLabel: true,
                name: 'variants',
                xtype: 'checkbox'
            }, {
                boxLabel: "ROK",
		hideLabel: true,
                name: 'variants',
                xtype: 'checkbox'
            }]
        });

        var dialog = new Ext.Window({
            autoCreate: true,
            layout: 'fit',
            plain: true,
            bodyStyle: 'padding:5px;',
            title: "Make selection",
            height: 350,
            width: 500,
            modal: true,
            fixedcenter: true,
            shadow: true,
            proxyDrag: true,
            resizable: true,
            items: [form],
            buttons: [{
                text: "Select All",
                handler: function() {
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: "Selecting all"
                    });
                    loadMask.show();

                    window.setTimeout(function(){
                        var selectedVariants = [];
			if (form.items.items[1].getValue()) { selectedVariants.push("BNE"); }
			if (form.items.items[2].getValue()) { selectedVariants.push("MEL"); }
			if (form.items.items[3].getValue()) { selectedVariants.push("OOL"); }
			if (form.items.items[4].getValue()) { selectedVariants.push("PER"); }
			if (form.items.items[5].getValue()) { selectedVariants.push("ROK"); }
                        try {
                            this.selectVariants(selectedVariants);
                            dialog.close();
                        }
                        catch (error) {
                            Ext.Msg.alert(ORYX.I18N.JSONSupport.imp.syntaxError, error.message);
                        }
                        finally {
                            loadMask.hide();
                        }
                    }.bind(this), 100);

                }.bind(this)
            }, {
                text: "Select None",
                handler: function() {
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: "Removing selection"
                    });
                    loadMask.show();

                    window.setTimeout(function(){
                        //var json = form.items.items[2].getValue();
                        try {
                            this.selectVariants([]);
                            dialog.close();
                        }
                        catch (error) {
                            Ext.Msg.alert(ORYX.I18N.JSONSupport.imp.syntaxError, error.message);
                        }
                        finally {
                            loadMask.hide();
                        }
                    }.bind(this), 100);

                }.bind(this)
            }, {
                text: ORYX.I18N.JSONSupport.imp.btnClose,
                handler: function(){
                    dialog.close();
                }.bind(this)
            }]
        });

        dialog.show();

        form.items.items[1].getEl().dom.addEventListener('change', function(evt){
            var text = evt.target.files[0].getAsText('UTF-8');
            form.items.items[2].setValue(text);
        }, true)
    },

    selectVariants: function(selectedVariants){
	//alert("Select variants " + selectedVariants);
        try {
        	var stylesheet = this.facade.getCanvas().getHTMLContainer().ownerDocument.head.lastElementChild.sheet;
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			if (shape.hasProperty("variants") && shape.properties["oryx-variants"]) {
			    try {
				var variants = shape.properties["oryx-variants"].evalJSON();
				var match = false;
				var formatted = "";
				for (i = 0; i < variants.totalCount; i++) {
					formatted += variants.items[i].id;
                                        for (j = 0; j < selectedVariants.length; j++) {
                                            if (selectedVariants[j] == variants.items[i].id) {
						match = true;
					    }
					}
					if (i < variants.totalCount - 1) {
						formatted += ",";
					}
				}
				//alert(shape + " variants: " + formatted + " match: " + match);
				shape.setProperty("selected", match);

			    } catch (err) {
				alert(shape + " failed: " + err);
			    }

			}
		}.bind(this));
		this.facade.getCanvas().update();
        }
        catch (err) {
                alert("Unable to select variants " + variants + ": " + err.message);
                //Ext.Msg.alert("Unable to select variants " + variants + ": " + err.message);
        }
    },
});
