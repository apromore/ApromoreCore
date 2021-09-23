import * as testFactory from "../testFactory";
import View from "../../../src/bpmneditor/plugins/view";
import Editor from "../../../src/bpmneditor/editor";

describe('After the EditorApp has been initialized with a BPMN model with View plugin', function () {
    let editorApp;
    let editor;

    beforeEach(async function() {
        spyOn(Editor.prototype, 'zoomIn');
        spyOn(Editor.prototype, 'zoomOut');
        spyOn(Editor.prototype, 'zoomFitToModel');
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();
    });

    it('The View plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[5]).toBeInstanceOf(View);
    });

    it('The ZoomIn button works OK if pressed', async function() {
        console.log(editorApp.getActivatedPlugins()[5]);
        let element = Ext.getCmp("ap-id-editor-zoomIn-btn");
        element.handler.call(element.scope);
        expect(editor.zoomIn).toHaveBeenCalled();
    });

    it('The ZoomOut button works OK if pressed', async function() {
        console.log(editorApp.getActivatedPlugins()[5]);
        let element = Ext.getCmp("ap-id-editor-zoomOut-btn");
        element.handler.call(element.scope);
        expect(editor.zoomOut).toHaveBeenCalled();
    });

    it('The ZoomFit button works OK if pressed', async function() {
        console.log(editorApp.getActivatedPlugins()[5]);
        let element = Ext.getCmp("ap-id-editor-zoomFit-btn");
        element.handler.call(element.scope);
        expect(editor.zoomFitToModel).toHaveBeenCalled();
    });
});
