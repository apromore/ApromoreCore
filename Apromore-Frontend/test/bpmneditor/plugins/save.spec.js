import * as testFactory from "../testFactory";
import ApromoreSave from "../../../src/bpmneditor/plugins/apromoreSave";

describe('After the EditorApp has been initialized with a BPMN model with Save plugin', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The Save plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[2]).toBeInstanceOf(ApromoreSave);
    });
});
