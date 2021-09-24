import * as testFactory from "../testFactory";
import SimulationPanel from "../../../src/bpmneditor/plugins/simulationPanel";

describe('After the EditorApp has been initialized with a BPMN model with SimulationPanel plugin', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The SimulationPanel plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[7]).toBeInstanceOf(SimulationPanel);
    });

    // Need to investigate Ext.Panel and event handling to do proper testing
    it('Clicking on the Simulate button can collapse or open the Simulation Panel in the editor', async function() {
        let simulationPanel = editorApp.getEastRegion();
        let spy = spyOn(simulationPanel, 'toggleCollapse');

        let element = Ext.getCmp("ap-id-editor-simulation-btn");

        // 1st click
        element.handler.call(element.scope);
        expect(simulationPanel.toggleCollapse).toHaveBeenCalled();
        //let plugin = editorApp.getActivatedPlugins()[7];
        //expect(simulationRegion.getEl().hasClass('x-panel-collapsed')).toBeTrue();

        // 2nd click
        spy.calls.reset(); //reset the previous spy action to prepare for the new one below.
        element.handler.call(element.scope);
        expect(simulationPanel.toggleCollapse).toHaveBeenCalled();
    });
});
