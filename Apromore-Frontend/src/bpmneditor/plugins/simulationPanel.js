import CONFIG from './../config';

/**
 * The simulation panel plugin offers functionality to change model simulation parameters over the
 * simulation parameters panel.
 *
 * @class SimulationPanel
 * @param {Object} facade The editor facade for plugins.
 */
export default class SimulationPanel {

    constructor(facade) {
        this.facade = facade;

        /* Register toggle simulation panel */
        this.facade.offer({
            'btnId': 'ap-id-editor-simulation-btn',
            'name': window.Apromore.I18N.SimulationPanel.toggleSimulationDrawer,
            'functionality': this.toggleSimulationDrawer.bind(this),
            'group': window.Apromore.I18N.SimulationPanel.group,
            'description': window.Apromore.I18N.SimulationPanel.toggleSimulationDrawerDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0,
            'icon': CONFIG.PATH + "images/ap/simulate-model.svg",
            isEnabled : function(){ return facade.useSimulationPanel}.bind(this),
        });
    };

    /**
     * Shortcut for performing an expand or collapse based on the current state of the panel.
     */
    toggleSimulationDrawer() {
        this.facade.getSimulationDrawer().toggleCollapse(true);
    }
};
