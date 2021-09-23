import * as testFactory from "../testFactory";
import Share from "../../../src/bpmneditor/plugins/share";

describe('After the EditorApp has been initialized with a BPMN model with Share plugin', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The Share plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[6]).toBeInstanceOf(Share);
    });
});
