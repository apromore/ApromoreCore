import * as testFactory from "./testFactory";

describe('After the EditorApp has been initialized with simple BPMN model and plugins', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndSimplePlugins().catch(err => fail(err));
    });

    it('It can get the XML representation of the model', async function() {
        let xml = await editorApp.getXML().catch(err => fail(err));
        expect(xml).toContain('<?xml');
    });

    it('It can get the SVG representation of the model', async function() {
        let svg = await editorApp.getSVG().catch(err => fail(err));
        expect(svg).toContain('<svg');
    });

    it('It can load all the plugins', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins.length).toEqual(2);
    });
});
