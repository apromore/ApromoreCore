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
            name: ORYX.I18N.SelectionExtension.name,
            functionality: this.showDialog.bind(this),
            group: ORYX.I18N.SelectionExtension.group,
            icon: ORYX.PATH + "images/selection_extension.png",
            description: ORYX.I18N.SelectionExtension.desc,
            index: 1,
            minShape: 0,
            maxShape: 0
        });

	// This array remembers which variants were selected during any previous selection; initially, empty
	this.selectedVariants = [];
    },
    
    showDialog: function(){

	// Find all the variants occurring in this model
	var variants = this.findAllVariants();

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
		var cb = new Ext.form.Checkbox({
                        boxLabel: variant,
                        hideLabel: true,
                        name: 'variants'
                });
		cb.setValue(this.selectedVariants.indexOf(variant) > -1);
		form.add(cb);
	}.bind(this));

	form.add(new Ext.form.NumberField({
		allowDecimals: false,
		allowNegative: false,
		fieldLabel: "Minimum",
		name: "min",
		value: 1
	}));

	form.add(new Ext.form.NumberField({
		allowDecimals: false,
		allowNegative: false,
		fieldLabel: "Maximum",
		name: "max",
		value: variants.length
	}));

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
                        msg: "Selecting checked variants"
                    });
                    loadMask.show();

                    window.setTimeout(function(){
                        var selectedVariants = [];
			for (k = 0; k < variants.length; k++) {
				if (form.items.items[k+1].getValue()) {
					selectedVariants.push(variants[k]);
				}
			}
			this.selectedVariants = selectedVariants;
			var minimumFrequency = form.items.items[variants.length + 1].getValue();
			var maximumFrequency = form.items.items[variants.length + 2].getValue();
                        try {
                            this.selectVariants(selectedVariants, minimumFrequency, maximumFrequency);
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
                        try {
			    this.selectedVariants = [];
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

    // Return an array containing the shapes which participate in the specified variants
    selectVariants: function(selectedVariants, minFrequency, maxFrequency){
	try {
		// A map from shapes, indexed by resourceId, to an array of the selected variants in which the shape participates
		var variantMap = Object.create(null);

		this.facade.getCanvas().getChildShapes().each(function (shape) {
			variantMap[shape.resourceId] = [];
		}.bind(this));

		selectedVariants.each(function(variant) {
			var shapes = this.findElementsInVariant([variant]);
			shapes.each(function (shape) {
				variantMap[shape.resourceId].push(variant);
			}.bind(this));
		}.bind(this));
		// variantMap is now populated

		// Set selection properties for shapes which occur in variantMap with the right frequency
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			var count = variantMap[shape.resourceId].length;
			shape.setProperty("selected", minFrequency <= count && count <= maxFrequency);
		}.bind(this));
		
		// selection properties are now all correct, so update the display
		this.facade.getCanvas().update();
        }
        catch (err) {
                alert("Unable to select variants " + variants + ": " + err.message);
                //Ext.Msg.alert("Unable to select variants " + variants + ": " + err.message);
        }
    },

    // Deselect all process model elements.
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
    },

    //
    // Beyond this point, members are intended to be private
    //

    startStencilIds: [
	"http://b3mn.org/stencilset/bpmn2.0#StartCompensationEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartConditionalEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartErrorEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartEscalationEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartMessageEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartMultipleEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartParallelMultipleEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartNoneEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartSignalEvent",
	"http://b3mn.org/stencilset/bpmn2.0#StartTimerEvent"],

    endStencilIds: [
	"http://b3mn.org/stencilset/bpmn2.0#EndCancelEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndCompensationEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndErrorEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndEscalationEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndMessageEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndMultipleEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndNoneEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndSignalEvent",
	"http://b3mn.org/stencilset/bpmn2.0#EndTerminateEvent"],

    // Return a sorted array of all the variant ids that occur within the process model
    findAllVariants: function() {
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
	return variants;
    },

    // Returns an array containing the shapes with any of the given array of stencilId strings
    findElementsWithStencilIds: function(stencilIds) {
	var matchingElements = [];
	this.facade.getCanvas().getChildShapes().each(function (shape) {
		if (stencilIds.indexOf(shape.getStencil().id()) > -1) {
			matchingElements.push(shape);
		}
	}.bind(this));
	return matchingElements;
    },

    // Select all process model elements which participate in the specified variants
    findElementsInVariant: function(selectedVariants) {

		// Find all the BPMN start and end events in the model
		var startEvents = this.findElementsWithStencilIds(this.startStencilIds);
		var endEvents   = this.findElementsWithStencilIds(this.endStencilIds);

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
		var elements = [];
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			if (startable.indexOf(shape) > -1 && endable.indexOf(shape) > -1) {
				elements.push(shape);
			}
		}.bind(this));

		return elements;
    },
});
