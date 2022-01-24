import * as testFactory from "./testFactory";

describe('After the EditorApp has been created with a custom configuration', function () {

    it('It can initialize without a Simulation Panel', async function () {
        let editorApp = testFactory.createEditorAppWithSimulationPanel();
        await testFactory.initEditorApp(editorApp, testFactory.bpmnSimpleXML, testFactory.pluginsConfigXML, true);
        expect(editorApp.getActivatedPlugins().length).toEqual(10);
    });

    it('It can initialize with limited toolbar buttons', async function () {
        let editorApp = testFactory.createEditorAppWithCustomToolbarButtons();
        await testFactory.initEditorApp(editorApp, testFactory.bpmnSimpleXML, testFactory.pluginsConfigXML, true);
        expect(editorApp.getActivatedPlugins().length).toEqual(10);
    });

    it('It can initialize with limited plugins', async function () {
        let editorApp = await testFactory.createEditorAppWithModelAndSimplePlugins().catch(err => fail(err));
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins.length).toEqual(2);
    });

    it('It can initialize without fullscreen', async function () {
        let editorApp = testFactory.createEditorAppWithoutFullscreenMode()
        await testFactory.initEditorApp(editorApp, testFactory.bpmnSimpleXML, testFactory.pluginsConfigXML, true);
        expect(editorApp.getActivatedPlugins().length).toEqual(10);
    });

});
