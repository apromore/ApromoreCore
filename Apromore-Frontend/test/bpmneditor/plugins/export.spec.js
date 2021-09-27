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
        const link = {
            click: () => {},
        };
        spyOn(document, 'createElement').and.callFake(() => {return link});
        spyOn(link, 'click');

        let exportXML = await editorApp.getXML();

        let pluginExport = editorApp.getActivatedPlugins()[3];
        await pluginExport.exportBPMN();

        expect(link.target).toEqual('_blank');
        expect(link.download).toEqual('diagram.bpmn');
        expect(link.href).toEqual('data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(exportXML));
        expect(link.click).toHaveBeenCalledTimes(1);
    });

    it('Clicking on the Export SVG button can export SVG from the Editor', async function() {
        const link = {
            click: () => {
            },
        };
        spyOn(document, 'createElement').and.callFake(() => {return link});
        spyOn(link, 'click');

        let exportSVG = await editorApp.getSVG();

        let pluginExport = editorApp.getActivatedPlugins()[3];
        await pluginExport.exportSVG();

        expect(link.target).toEqual('_blank');
        expect(link.download).toEqual('diagram.svg');
        expect(link.href).toEqual('data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(exportSVG));
        expect(link.click).toHaveBeenCalledTimes(1);
    });
});
