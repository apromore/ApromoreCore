import * as testFactory from "../testFactory";
import Export from "../../../src/bpmneditor/plugins/export";

describe('After the EditorApp has been initialized with a BPMN model with Export plugin', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The toolbar plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[3]).toBeInstanceOf(Export);
    });
});
