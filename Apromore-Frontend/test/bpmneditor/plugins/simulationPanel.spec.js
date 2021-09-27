import * as testFactory from "../testFactory";
import * as testSupport from "../testSupport";
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

        // 1st click on the Simulation toggle button
        await testSupport.clickButton("ap-id-editor-simulation-btn");
        expect(simulationPanel.toggleCollapse).toHaveBeenCalled();

        // 2nd click
        spy.calls.reset(); //reset the previous spy action to prepare for the new one below.
        await testSupport.clickButton("ap-id-editor-simulation-btn");
        expect(simulationPanel.toggleCollapse).toHaveBeenCalled();
    });
});
