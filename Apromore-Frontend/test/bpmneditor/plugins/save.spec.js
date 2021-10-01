import * as testFactory from "../testFactory";
import ApromoreSave from "../../../src/bpmneditor/plugins/apromoreSave";
import Editor from "../../../src/bpmneditor/editor";

describe('After the EditorApp has been initialized with a BPMN model with Save plugin', function () {
    let editorApp;
    let spy;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The Save plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[2]).toBeInstanceOf(ApromoreSave);
    });

    // Unlike other tests, this test can't start from calling ExtJs button click programmatically
    // because ApromoreSave's save method is async. There's no way for this test to wait for the save method to
    // resolve after calling the button click. So, it has to start from calling ApromoreSave.save method.
    it('Clicking on the Save button can activate the Undo action in the editor', async function() {
        // Mock Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSave (the actual version calls to the server)
        if (!window.Apromore) window.Apromore = {};
        if (!window.Apromore.BPMNEditor) window.Apromore.BPMNEditor = {};
        if (!window.Apromore.BPMNEditor.Plugins) window.Apromore.BPMNEditor.Plugins = {};
        if (!window.Apromore.BPMNEditor.Plugins.ApromoreSave) window.Apromore.BPMNEditor.Plugins.ApromoreSave = {};
        window.Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSave = function (xml, svg) {};

        spyOn(window.Apromore.BPMNEditor.Plugins.ApromoreSave, 'apromoreSave');
        let pluginSave = editorApp.getActivatedPlugins()[2];
        await pluginSave.save();
        expect(window.Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSave).toHaveBeenCalled();
    });
});
