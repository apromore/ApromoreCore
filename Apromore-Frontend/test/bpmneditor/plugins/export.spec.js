import * as testFactory from "../testFactory";
import Export from "../../../src/bpmneditor/plugins/export";

describe('After the EditorApp has been initialized with a BPMN model with Export plugin', function () {
    let editorApp;
    let editor;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();
    });

    it('The Export plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[3]).toBeInstanceOf(Export);
    });

    it('Clicking on the Export XML button can export XML from the Editor', async function() {
        spyOn(editor, 'getXML');
        let pluginExport = editorApp.getActivatedPlugins()[3];
        await pluginExport.exportBPMN();
        expect(editor.getXML).toHaveBeenCalled();
    });

    it('Clicking on the Export SVG button can export SVG from the Editor', async function() {
        spyOn(editor, 'getSVG');
        let pluginExport = editorApp.getActivatedPlugins()[3];
        await pluginExport.exportSVG();
        expect(editor.getSVG).toHaveBeenCalled();
    });
});
