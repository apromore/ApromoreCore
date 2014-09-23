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

	// Find all the variants occurring in this model
	var variants = [];
	this.facade.getCanvas().getChildShapes().each(function (shape) {
		if (shape.hasProperty("variants") && shape.properties["oryx-variants"]) {
			var shapeVariants = shape.properties["oryx-variants"].evalJSON();
			for (i = 0; i < shapeVariants.totalCount; i++) {
				var item = shapeVariants.items[i].id;
				if (variants.indexOf(item) == -1) {
					variants.push(item);
				}
			}
		}
	}.bind(this));
        variants.sort();

	// Create the form to present
	var form = new Ext.form.FormPanel({
            baseCls: 'x-plain',
            labelWidth: 50,
            defaultType: 'textfield',
            items: [new Ext.form.Label({
                text: "Selected variants:",
                style: 'font-size:12px;margin-bottom:10px;display:block;',
                anchor: '100%'
            })]
        });

	// Populate the form with a checkbox for each variant
	variants.each(function(variant) {
		form.add(new Ext.form.Checkbox({
			boxLabel: variant,
			hideLabel: true,
			name: 'variants'
		}));
	}.bind(this));

	// Present the form to the user
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
                text: "Select Variants",
                handler: function() {
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: "Selecting all"
                    });
                    loadMask.show();

                    window.setTimeout(function(){
                        var selectedVariants = [];
			for (k = 0; k < variants.length; k++) {
				if (form.items.items[k+1].getValue()) {
					selectedVariants.push(variants[k]);
				}
			}
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
                            this.selectNone();
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
        try {
		
		// Find all the BPMN start and end events in the model
		var startEvents = [];
		var endEvents = [];
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			switch (shape.getStencil().id()) {
			case "http://b3mn.org/stencilset/bpmn2.0#StartCompensationEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartConditionalEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartErrorEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartEscalationEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartMessageEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartMultipleEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartParallelMultipleEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartNoneEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartSignalEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#StartTimerEvent":
				startEvents.push(shape);
				break;
			
			case "http://b3mn.org/stencilset/bpmn2.0#EndCancelEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndCompensationEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndErrorEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndEscalationEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndMessageEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndMultipleEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndNoneEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndSignalEvent":
			case "http://b3mn.org/stencilset/bpmn2.0#EndTerminateEvent":
				endEvents.push(shape);
				break;
			}
		}.bind(this));

		// Returns a boolean which is false only if there is a variant list, and it doesn't include any of the selectedVariants
		// We use this to block reachability via sequence flows that don't occur in the selected variants
		var isInSelectedVariants = function(shape) {
			if (shape.hasProperty("variants") && shape.properties["oryx-variants"]) {
				var variants = shape.properties["oryx-variants"].evalJSON();
                                var match = false;
                                for (i = 0; i < variants.totalCount; i++) {
                                        for (j = 0; j < selectedVariants.length; j++) {
                                            if (selectedVariants[j] == variants.items[i].id) {
                                                match = true;
                                            }
                                        }
                                }
				return match;
			} else {
				return true;
			}
		}

		// Every element reachable from a start element is startable
		var startable = [];
		var traverseStartables = function(shape) {
			if (isInSelectedVariants(shape) && startable.indexOf(shape) == -1) {
				startable.push(shape);
				shape.getOutgoingShapes().each(function (outgoing) {
					traverseStartables(outgoing);
				}.bind(this));
			}
		}
		startEvents.each(function (shape) {
			traverseStartables(shape);
		}.bind(this));

		// Every element reachable from an end element is endable
		var endable = [];
		var traverseEndables = function(shape) {
			if (isInSelectedVariants(shape) && endable.indexOf(shape) == -1) {
				endable.push(shape);
				shape.getIncomingShapes().each(function (incoming) {
					traverseEndables(incoming);
				}.bind(this));
			}
		}
		endEvents.each(function (shape) {
			traverseEndables(shape);
		}.bind(this));

		// Select every shape that is both startable and endable
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			shape.setProperty("selected", (startable.indexOf(shape) > -1) && (endable.indexOf(shape) > -1));
		}.bind(this));

		// selection properties are now all correct, so update the display
		this.facade.getCanvas().update();
        }
        catch (err) {
                alert("Unable to select variants " + variants + ": " + err.message);
                //Ext.Msg.alert("Unable to select variants " + variants + ": " + err.message);
        }
    },

    selectNone: function(){
	try {
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			shape.setProperty("selected", false);
		}.bind(this));
		this.facade.getCanvas().update();
	}
	catch (err) {
		alert("Unable to clear selection: " + err.message);
	}
    }
});
