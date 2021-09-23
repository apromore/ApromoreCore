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

    it('Clicking on the Simulate button can collapse or open the Simulation Panel in the editor', async function() {
        let simulationRegion = editorApp.getEastRegion();
        let element = Ext.getCmp("ap-id-editor-simulation-btn");
        console.log(simulationRegion);

        simulationRegion.addListener('collapse', function(a) {
           console.log('test');
        });

        simulationRegion.addListener('expand', function(a) {
            console.log('test');
        });

        // 1st click
        element.handler.call(element.scope);
        expect(simulationRegion.collapsed).toBeFalse();

        // 2nd click
        element.handler.call(element.scope);
        expect(simulationRegion.collapsed).toBeTrue();
    });
});
