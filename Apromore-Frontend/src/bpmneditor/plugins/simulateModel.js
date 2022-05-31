import CONFIG from './../config';

/**
 * The simulate model plugin offers functionality to create a log based on the simulation
 * parameters of the model.
 *
 * @class SimulateModel
 * @param {Object} facade The editor facade for plugins.
 */
export default class SimulateModel {

    constructor(facade) {
        this.facade = facade;

        /* Register simulate model */
        this.facade.offer({
            'btnId': 'ap-id-editor-simulate-model-btn',
            'name': window.Apromore.I18N.SimulationPanel.simulateModel,
            'functionality': this.simulateModel.bind(this),
            'group': window.Apromore.I18N.SimulationPanel.group,
            'description': window.Apromore.I18N.SimulationPanel.simulateModelDesc,
            'index': 1,
            'groupOrder': 4,
            'icon': CONFIG.PATH + "images/ap/simulate-model.svg",
            isEnabled : function(){ return facade.useSimulationPanel}.bind(this),
        });
    };

    /**
     * Shortcut for performing an expand or collapse based on the current state of the panel.
     */
    async simulateModel() {
        let xml = await this.facade.getXML().catch(err => {Log.error(err.message)});

        if (Apromore.BPMNEditor.Plugins.SimulateModel.apromoreSimulateModel) {
            Apromore.BPMNEditor.Plugins.SimulateModel.apromoreSimulateModel(xml);
        }
    }
};
