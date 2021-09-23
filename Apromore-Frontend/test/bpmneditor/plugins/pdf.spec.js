import * as testFactory from "../testFactory";
import File from "../../../src/bpmneditor/plugins/pdf";

describe('After the EditorApp has been initialized with a BPMN model with File plugin', function () {
    let editorApp;
    let editor;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The File plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[4]).toBeInstanceOf(File);
    });
});
