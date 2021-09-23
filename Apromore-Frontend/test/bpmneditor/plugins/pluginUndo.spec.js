import * as testFactory from "../testFactory";
import Undo from "../../../src/bpmneditor/plugins/undo";
import Editor from "../../../src/bpmneditor/editor";

describe('After the EditorApp has been initialized with a BPMN model with Undo plugin', function () {
    let editorApp;
    let editor;

    beforeEach(async function() {
        spyOn(Editor.prototype, 'undo');
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();
    });

    it('The Undo plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[1]).toBeInstanceOf(Undo);
    });

    it('The Undo button works OK if pressed', async function() {
        let pluginUndo = editorApp.getActivatedPlugins()[1];
        let element = Ext.getCmp("ap-id-editor-undo-btn");
        console.log('element', element);
        element.handler.call(element.scope);
        expect(editor.undo).toHaveBeenCalled();
    });

});
