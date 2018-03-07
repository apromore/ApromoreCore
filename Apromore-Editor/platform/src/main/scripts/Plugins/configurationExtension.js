if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

/**
   Toggles the visibility of configuration annotations.
   @class ORYX.Plugins.ConfigurationExtension
   @constructor Creates a new instance
   @extends ORYX.Plugins.AbstractPlugin
*/
ORYX.Plugins.ConfigurationExtension = ORYX.Plugins.AbstractPlugin.extend({
    /**@private*/
    construct: function(){
        arguments.callee.$.construct.apply(this, arguments);
                
        this.active = false;
        this.raisedEventIds = [];
        
        this.facade.offer({
            'name': ORYX.I18N.ConfigurationExtension.name,
            'functionality': this.perform.bind(this),
            'group': ORYX.I18N.ConfigurationExtension.group,
            'icon': ORYX.PATH + "images/configuration_extension.png",
            'description': ORYX.I18N.ConfigurationExtension.desc,
            'index': 0,
            'toggle': true,
            'minShape': 0,
            'maxShape': 0
        });
        
        this.facade.registerOnEvent(ORYX.Plugins.ConfigurationExtension.TOGGLE_CONFIGURATION_ANNOTATION_VISIBILITY, this.toggleVisibility.bind(this));
    },
    
    perform: function(button, pressed){
	var stylesheet = this.facade.getCanvas().getHTMLContainer().ownerDocument.head.lastElementChild.sheet;
        try {
		// Remove any existing CSS rules
                while (stylesheet.cssRules.length > 0) {
                        stylesheet.deleteRule(0);
                }

		// Add a rule to either display or hide the extension elements
                if (pressed) {
                        stylesheet.insertRule(".configuration-extension { display: none }", 0);
                }
        }
        catch (err) {
                console.warn("Unable to insert CSS rule: " + err);
        }
    },
    
    /**
     * Sets the activated state of the plugin
     * @param {Ext.Button} Toolbar button
     * @param {Object} activated
     */
    setActivated: function(button, activated){
        button.toggle(activated);
        if(activated === undefined){
            this.active = !this.active;
        } else {
            this.active = activated;
        }
    },
    
    /**
     * Performs request to server to check for errors on current model.
     * @methodOf ORYX.Plugins.ConfigurationExtension.prototype
     * @param {Object} options Configuration hash
     * @param {String} context A context send to the syntax checker servlet
     * @param {Function} [options.onNoErrors] Raised when model has no errors.
     * @param {Function} [options.onErrors] Raised when model has errors.
     * @param {Function} [options.onFailure] Raised when server communcation failed.
     * @param {boolean} [options.showErrors=true] Display errors on nodes on canvas (by calling ORYX.Plugins.ConfigurationExtension.prototype.showErrors)
     */
    toggleVisibility: function(options){
	console.info ("ConfigurationExtension toggled");
    },
});

ORYX.Plugins.ConfigurationExtension.TOGGLE_CONFIGURATION_ANNOTATION_VISIBILITY = "toggleVisibility";
