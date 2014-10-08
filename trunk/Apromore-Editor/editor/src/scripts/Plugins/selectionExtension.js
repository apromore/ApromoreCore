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

	// The color of the highlighting
	this.color = "#7777FF";

	// This array remembers which variants were selected during any previous selection; initially, empty
	this.selectedVariants = [];
    },
    
    showDialog: function(){

	// Find all the variants occurring in this model
	var variants = this.findAllVariants();

	if (this.minFrequency === undefined) {
		this.minFrequency = 1;
	}

	if (this.maxFrequency === undefined) {
		this.maxFrequency = variants.length;
	}

	// Create the form to present
	var form = new Ext.form.FormPanel({
            baseCls: 'x-plain',
	    layout: 'table',
	    layoutConfig: {
		columns: 2
	    },
            labelWidth: 50,
            defaultType: 'textfield',
            items: [new Ext.form.Label({
                html: "<h4>Selected variants</h4>",
		colspan: 2,
                style: 'font-size:12px;margin-bottom:10px;display:block;',
                anchor: '100%'
            })]
        });

	// Populate the form with a checkbox for each variant
	form.variantCheckboxFields = [];
	var variantColorFields = [];
	variants.each(function(variant) {
		var cb = new Ext.form.Checkbox({
                        boxLabel: variant,
                        hideLabel: true,
                        name: 'variants'
                });
		cb.setValue(this.selectedVariants.indexOf(variant) > -1);
		form.variantCheckboxFields.push(cb);
		form.add(cb);

		var variantColor = this.color;
		if (this.variantColorFields) {
			variantColor = this.variantColorFields[variants.indexOf(variant)].getValue();
		}
		var c = new Ext.ux.ColorField({
			value: variantColor
		});
		variantColorFields.push(c);
		form.add(c);
	}.bind(this));
	this.variantColorFields = variantColorFields;

	form.add(new Ext.form.Label({
		text: "More than one",
		style: "padding-right: 0.5em"
	}));

	form.add(form.colorField = new Ext.ux.ColorField({ 
		fieldLabel: "Selection color",
		name: "color",
		value: this.color
	}));

	form.add(new Ext.Button({
		text: "All",
		style: "padding: 0.5em 1em",
		handler: function() {
			form.variantCheckboxFields.each(function(field) {
				field.setValue(true);
			}.bind(this));
		}
	}));

	form.add(new Ext.Button({
		text: "Clear",
		style: "padding: 0.5em 1em",
		handler: function() {
			form.variantCheckboxFields.each(function(field) {
				field.setValue(false);
			}.bind(this));
		}
	}));

	var maxFrequencyField, minFrequencyField;
	var form2 = new Ext.form.FormPanel({
            baseCls: 'x-plain',
            labelWidth: 50,
            defaultType: 'textfield',
            items: [
		new Ext.form.Label({
			html: "<h4>Frequency range</h4>",
			colspan: 2,
			style: 'font-size:12px;margin-bottom:10px;margin-top:10px;display:block;',
			anchor: '100%'
		}),
		minFrequencyField = new Ext.form.NumberField({
			allowDecimals: false,
			allowNegative: false,
			fieldLabel: "Minimum",
			minValue: 1,
			msgTarget: "side",
			name: "min",
			validator: function(candidate) {
				if (candidate > maxFrequencyField.getValue()) {
					return "Minimum frequency cannot be greater than maximum frequency";
				}
				return true;
			},
			value: this.minFrequency,
			width: 50
		}),
		maxFrequencyField = new Ext.form.NumberField({
			allowDecimals: false,
			allowNegative: false,
			fieldLabel: "Maximum",
			maxValue: variants.length,
			msgTarget: "side",
			name: "max",
			validator: function(candidate) {
				if (candidate < minFrequencyField.getValue()) {
					return "Maximum frequency cannot be less than minimum frequency";
				}
				return true;
			},
			value: this.maxFrequency,
			width: 50
		})
            ]
        });


	// Present the form to the user
        var dialog = new Ext.Window({
            autoCreate: true,
            layout: 'table',
	    layoutConfig: { columns: 1 },
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
            items: [form, form2],
            buttons: [{
                text: "Select",
                handler: function() {
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: "Selecting checked variants"
                    });
                    loadMask.show();

                    window.setTimeout(function(){
                        var selectedVariants = [];
			for (k = 0; k < variants.length; k++) {
				if (form.variantCheckboxFields[k].getValue()) {
					selectedVariants.push(variants[k]);
				}
			}
			this.selectedVariants = selectedVariants;
			this.minFrequency = minFrequencyField.getValue();
			this.maxFrequency = maxFrequencyField.getValue();
			this.color = form.colorField.getValue();
                        try {
                            this.selectVariants(selectedVariants, this.minFrequency, this.maxFrequency);
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
                text: "Reset",
                handler: function() {
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: "Removing selection"
                    });
                    loadMask.show();

                    window.setTimeout(function(){
                        try {
                            this.resetSelection();
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
			var shapeVariants = variantMap[shape.resourceId];
			var count = shapeVariants.length;
			shape.setProperty("selected", minFrequency <= count && count <= maxFrequency);
			shape.setProperty("selectioncolor",
				(count == 1)
				? (this.variantColorFields[this.findAllVariants().indexOf(shapeVariants[0])].getValue())
				: this.color
			);
			if (shape.getProperty("selected")) {
				shape.node.removeAttributeNS(null, "style");
			} else {
				shape.node.setAttributeNS(null, "style", "opacity: 0.25");
			}
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
    resetSelection: function(){
	try {
		this.facade.getCanvas().getChildShapes().each(function (shape) {
			shape.setProperty("selected", false);
			shape.node.removeAttributeNS(null, "style");
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
	"http://b3mn.org/stencilset/bpmn2.0#IntermediateLinkEventCatching",
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
	"http://b3mn.org/stencilset/bpmn2.0#EndTerminateEvent",
	"http://b3mn.org/stencilset/bpmn2.0#IntermediateLinkEventThrowing"],

    artifactStencilIds: [
	"http://b3mn.org/stencilset/bpmn2.0#ConfigurationAnnotation",
	"http://b3mn.org/stencilset/bpmn2.0#DataObject",
	"http://b3mn.org/stencilset/bpmn2.0#DataStore",
	"http://b3mn.org/stencilset/bpmn2.0#ITSystem",
	"http://b3mn.org/stencilset/bpmn2.0#Message",
	"http://b3mn.org/stencilset/bpmn2.0#TextAnnotation"],

    associationStencilIds: [
	"http://b3mn.org/stencilset/bpmn2.0#Association_Undirected",
	"http://b3mn.org/stencilset/bpmn2.0#Association_Unidirectional",
	"http://b3mn.org/stencilset/bpmn2.0#Association_Bidirectional",
	"http://b3mn.org/stencilset/bpmn2.0#MessageFlow"],

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
		var startEvents  = this.findElementsWithStencilIds(this.startStencilIds);
		var endEvents    = this.findElementsWithStencilIds(this.endStencilIds);
		var artifacts    = this.findElementsWithStencilIds(this.artifactStencilIds);
		var associations = this.findElementsWithStencilIds(this.associationStencilIds);


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
			if (isInSelectedVariants(shape) && startable.indexOf(shape) == -1 && associations.indexOf(shape) == -1) {
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
			if (isInSelectedVariants(shape) && endable.indexOf(shape) == -1 && associations.indexOf(shape) == -1) {
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

		// Select associated artifacts
                associations.each(function (association) {
                        var f = function(sources, targets) {
                                sources.each(function(source) {
                                        if (elements.indexOf(source) > -1) {
                                                targets.each(function(target) {
                                                        if (artifacts.indexOf(target) > -1) {
                                                                if (elements.indexOf(association) == -1) { elements.push(association); }
                                                                if (elements.indexOf(target) == -1) { elements.push(target); }
                                                        }
							else if (elements.indexOf(target) > -1) {
                                                                if (elements.indexOf(association) == -1) { elements.push(association); }
							}
                                                }.bind(this));
                                        }
                                }.bind(this));
                        }
                        f(association.getIncomingShapes(), association.getOutgoingShapes());
                        f(association.getOutgoingShapes(), association.getIncomingShapes());
                }.bind(this));

		return elements;
    },
});
