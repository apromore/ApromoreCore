import * as testFactory from "../testFactory";
import View from "../../../src/bpmneditor/plugins/view";
import * as testSupport from "../testSupport";

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
        await testSupport.clickButton("ap-id-editor-zoomIn-btn");
        expect(editor.zoomIn).toHaveBeenCalled();
    });

    it('Clicking on the ZoomOut button can activate zoom fit action in the editor', async function() {
        spyOn(editor, 'zoomOut');
        await testSupport.clickButton("ap-id-editor-zoomOut-btn");
        expect(editor.zoomOut).toHaveBeenCalled();
    });

    it('Clicking on the ZoomFit button can activate zoom fit action in the editor', async function() {
        spyOn(editor, 'zoomFitToModel');
        await testSupport.clickButton("ap-id-editor-zoomFit-btn");
        expect(editor.zoomFitToModel).toHaveBeenCalled();
    });
});


describe('After the EditorApp has been initialized for animation', function () {
    let editorApp;
    let editor;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppForAnimation();
        editor = editorApp.getEditor();
    });

    it('Clicking on the ZoomFit button can activate the zoom to the original view in the editor', async function() {
        spyOn(editor, 'zoomFitOriginal');
        await testSupport.clickButton("ap-id-editor-zoomFit-btn");
        expect(editor.zoomFitOriginal).toHaveBeenCalledTimes(1);
    });
});
