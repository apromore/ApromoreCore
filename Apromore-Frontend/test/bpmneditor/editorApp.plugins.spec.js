import * as testFactory from "./testFactory"

describe('After the EditorApp has been initialized with a BPMN model and loaded plugins', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('It was able to load all plugins correctly', async function() {
        // let plugins = editorApp.getActivatedPlugins();
    });

    it('It is able to set up initial status of toolbar buttons correctly', async function() {
        // expect(plugins[1]).toBeInstanceOf(Undo);

    });
});
