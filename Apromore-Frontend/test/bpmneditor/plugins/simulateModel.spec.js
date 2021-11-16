import * as testFactory from "../testFactory";
import SimulateModel from "../../../src/bpmneditor/plugins/simulateModel";
import Editor from "../../../src/bpmneditor/editor";

describe('After the EditorApp has been initialized with a BPMN model with the Simulate model plugin', function () {
    let editorApp;
    let spy;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The Simulate model plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[7]).toBeInstanceOf(SimulateModel);
    });

    // Unlike other tests, this test can't start from calling ExtJs button click programmatically
    // because SimulateModel's simulateModel method is async. There's no way for this test to wait for the
    // simulateModel method to resolve after calling the button click. So, it has to start from calling
    // SimulateModel.simulateModel method.
    it('Clicking on the Simulate model button can activate the simulateModel action in the editor', async function() {
        // Mock Apromore.BPMNEditor.Plugins.SimulateModel.apromoreSimulateModel (the actual version calls to the server)
        if (!window.Apromore) window.Apromore = {};
        if (!window.Apromore.BPMNEditor) window.Apromore.BPMNEditor = {};
        if (!window.Apromore.BPMNEditor.Plugins) window.Apromore.BPMNEditor.Plugins = {};
        if (!window.Apromore.BPMNEditor.Plugins.ApromoreSave) window.Apromore.BPMNEditor.Plugins.SimulateModel = {};
        window.Apromore.BPMNEditor.Plugins.SimulateModel.apromoreSimulateModel = function (xml) {};

        spyOn(window.Apromore.BPMNEditor.Plugins.SimulateModel, 'apromoreSimulateModel');
        let pluginSimulateModel = editorApp.getActivatedPlugins()[7];
        await pluginSimulateModel.simulateModel();
        expect(window.Apromore.BPMNEditor.Plugins.SimulateModel.apromoreSimulateModel).toHaveBeenCalled();
    });
});
