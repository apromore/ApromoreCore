import * as testFactory from "../testFactory";
import File from "../../../src/bpmneditor/plugins/pdf";
import * as testSupport from '../testSupport';
import CONFIG from "../../../src/bpmneditor/config";
require('jasmine-ajax');

describe('After the EditorApp has been initialized with a BPMN model with PDF plugin', function () {
    let editorApp;
    let editor;

    it('The PDF plugin has been loaded', async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();

        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[4]).toBeInstanceOf(File);
    });

    it('The PDF plugin can catch error returning from the Editor', async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();

        spyOn(Promise, 'reject');
        spyOn(editor, 'getSVG2').and.callFake(async () => {throw new Error('Editor Error')});

        let plugin = editorApp.getActivatedPlugins()[4];
        await plugin.exportPDF().catch(err => {
            expect(err.message).toEqual('Editor Error');
        });
    });

    it('The PDF plugin can catch error while processing result from the editor', async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();

        spyOn(Promise, 'reject');
        spyOn(editor, 'getSVG2').and.callFake(async () => 1); // return a wrong type.

        let plugin = editorApp.getActivatedPlugins()[4];
        await plugin.exportPDF().catch(err => {
            expect(err.message).toContain('SVG to PDF error');
        });
    });

    let request;

    async function setUp () {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();

        let plugin = editorApp.getActivatedPlugins()[4];
        await plugin.exportPDF().catch(err => fail(err));

        request = jasmine.Ajax.requests.mostRecent();

        expect(request.url).toBe(CONFIG.PDF_EXPORT_URL);
        expect(request.method).toBe('POST');
        expect(request.responseType).toBe('blob');
    }

    it('On server response with success, the PDF plugin receives Ajax response', async function() {
        jasmine.Ajax.install();

        await setUp();

        // Mock response behavior
        const link = {
            click: () => {},
        };
        spyOn(document, 'createElement').and.callFake(() => {return link});
        spyOn(link, 'click');

        // Mock response content
        let testResponse = {
            readyState: 4,
            status: 200,
            response: new Blob(['responseText'], {
                type: 'text/plain'
            })
        }
        let request = jasmine.Ajax.requests.mostRecent();
        request.respondWith(testResponse);

        // Check response behavior
        expect(link.target).toEqual('_blank');
        expect(link.download).toEqual('diagram.pdf');
        //expect(link.href).toEqual(window.URL.createObjectURL()); // this is not tested because of new URL id created every time
        expect(link.click).toHaveBeenCalledTimes(1);

        jasmine.Ajax.uninstall();
    });

    it('On server response with errors, the PDF plugin can handle the error response', async function() {
        jasmine.Ajax.install();

        await setUp();

        spyOn(Promise, 'reject');

        // Mock response content
        let testResponse = {
            readyState: 4,
            status: 500,
            response: new Blob(['responseText'], {
                type: 'text/plain'
            })
        }
        let request = jasmine.Ajax.requests.mostRecent();
        request.respondWith(testResponse);

        expect(Promise.reject).toHaveBeenCalledTimes(1);
        jasmine.Ajax.uninstall();
    });

    it('On network issue, the PDF plugin can handle the issue', async function() {
        jasmine.Ajax.install();

        await setUp();

        spyOn(Ext.Msg, 'alert').and.callFake(() => {}); // error if not mocking ExtJs
        spyOn(Promise, 'reject');

        // Mock response content
        let testResponse = {
            readyState: 4,
            status: 200,
            response: new Blob(['responseText'], {
                type: 'text/plain'
            })
        }
        let request = jasmine.Ajax.requests.mostRecent();
        request.responseError();

        expect(Promise.reject).toHaveBeenCalledTimes(2); // Jasmin mock performs both onerror and onreadystatechange
        jasmine.Ajax.uninstall();
    });


});
