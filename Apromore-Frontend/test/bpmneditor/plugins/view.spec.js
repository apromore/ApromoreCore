import * as testFactory from "../testFactory";
import View from "../../../src/bpmneditor/plugins/view";
import Editor from "../../../src/bpmneditor/editor";

describe('After the EditorApp has been initialized with a BPMN model with View plugin', function () {
    let editorApp;
    let editor;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();
    });

    it('The View plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[5]).toBeInstanceOf(View);
    });

    it('Clicking on the ZoomIn button can activate Zoom In action in the editor', async function() {
        spyOn(editor, 'zoomIn');
        let element = Ext.getCmp("ap-id-editor-zoomIn-btn");
        element.handler.call(element.scope);
        expect(editor.zoomIn).toHaveBeenCalled();
    });

    it('Clicking on the ZoomOut button can activate zoom fit action in the editor', async function() {
        spyOn(editor, 'zoomOut');
        let element = Ext.getCmp("ap-id-editor-zoomOut-btn");
        element.handler.call(element.scope);
        expect(editor.zoomOut).toHaveBeenCalled();
    });

    it('Clicking on the ZoomFit button can activate zoom fit action in the editor', async function() {
        spyOn(editor, 'zoomFitToModel');
        let element = Ext.getCmp("ap-id-editor-zoomFit-btn");
        element.handler.call(element.scope);
        expect(editor.zoomFitToModel).toHaveBeenCalled();
    });
});
