import * as testFactory from "../testFactory";
import Toolbar from "../../../src/bpmneditor/plugins/toolbar";

describe('After the EditorApp has been initialized with a BPMN model with Toolbar plugin', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The toolbar plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[0]).toBeInstanceOf(Toolbar);
    });
});
