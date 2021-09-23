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
});
