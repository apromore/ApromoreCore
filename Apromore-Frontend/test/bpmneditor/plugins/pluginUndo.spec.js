import * as testFactory from "../testFactory";
import Undo from "../../../src/bpmneditor/plugins/undo";
import Editor from "../../../src/bpmneditor/editor";

describe('After the EditorApp has been initialized with a BPMN model with Undo plugin', function () {
    let editorApp;
    let editor;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        editor = editorApp.getEditor();

    });

    it('The Undo plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[1]).toBeInstanceOf(Undo);
    });

    it('Clicking on the Undo button can activate the Undo action in the editor', async function() {
        spyOn(editor, 'undo');
        let element = Ext.getCmp("ap-id-editor-undo-btn");
        element.handler.call(element.scope);
        expect(editor.undo).toHaveBeenCalled();
    });

    it('Clicking on the Undo button can activate the Undo action in the editor', async function() {
        spyOn(editor, 'redo');
        let element = Ext.getCmp("ap-id-editor-redo-btn");
        element.handler.call(element.scope);
        expect(editor.redo).toHaveBeenCalled();
    });

});
