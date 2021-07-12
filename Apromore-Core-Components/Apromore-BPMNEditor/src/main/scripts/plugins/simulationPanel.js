/**
 * @namespace Apromore name space for plugins
 * @name Apromore.Plugins
 */
if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

/**
 * The simulation panel plugin offers functionality to change model simulation parameters over the
 * simulation parameters panel.
 *
 * @class Apromore.Plugins.SimulationPanel
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
 */
Apromore.Plugins.SimulationPanel = Clazz.extend({
    /** @lends Apromore.Plugins.SimulationPanel.prototype */
    facade: undefined,

    construct: function (facade) {
        this.facade = facade;

        /* Register toggle simulation panel */
        this.facade.offer({
            'name': window.Apromore.I18N.SimulationPanel.toggleSimulationDrawer,
            'functionality': this.toggleSimulationDrawer.bind(this),
            'group': window.Apromore.I18N.SimulationPanel.group,
            'description': window.Apromore.I18N.SimulationPanel.toggleSimulationDrawerDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0,
            'icon': Apromore.PATH + "images/ap/simulate-model.svg",
            isEnabled : function(){ return facade.useSimulationPanel}.bind(this),
        });
    },

    /**
     * Shortcut for performing an expand or collapse based on the current state of the panel.
     */
    toggleSimulationDrawer: function () {
        this.facade.getSimulationDrawer().toggleCollapse(true);
    }
});