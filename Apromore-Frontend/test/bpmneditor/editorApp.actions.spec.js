import * as testFactory from "./testFactory";

describe('After the EditorApp has been created with a BPMN model and plugins', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins().catch(err => fail(err));
    });

    it('It can load all the plugins', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins.length).toEqual(10);
    });

    it('It can get the XML representation of the model', async function() {
        let xml = await editorApp.getXML().catch(err => fail(err));
        expect(xml).toContain('<?xml');
    });

    it('It can get the SVG representation of the model', async function() {
        let svg = await editorApp.getSVG().catch(err => fail(err));
        expect(svg).toContain('<svg');
    });

    it('It can communicate editor command stack changes to other parts', async function() {
        let toolbarPlugin = editorApp.getActivatedPlugins()[0];
        let undoButton = toolbarPlugin.getButtonById('ap-id-editor-undo-btn');
        spyOn(undoButton.buttonInstance, 'enable');
        let redoButton = toolbarPlugin.getButtonById('ap-id-editor-redo-btn');
        spyOn(redoButton.buttonInstance, 'enable');

        // Make a change to the model by adding a task shape, the Undo button is expected to be enabled.
        let editor = editorApp.getEditor();
        var modelling = editor.actualEditor.get('modeling');
        var parent = editor.actualEditor.get('canvas').getRootElement();
        var shape = modelling.createShape({type:'bpmn:Task', width:10, height:10}, {x:0, y:0}, parent);
        expect(undoButton.buttonInstance.enable).toHaveBeenCalled();

        // Click on the Undo button, the Redo button is expected to be enabled.
        let element = Ext.getCmp("ap-id-editor-undo-btn");
        element.handler.call(element.scope);
        expect(redoButton.buttonInstance.enable).toHaveBeenCalled();
    });

    it('It can communicate publish state changes to other parts', async function() {
        let publishModelPlugin = editorApp.getActivatedPlugins()[8];
        spyOn(publishModelPlugin, 'onPublishStateUpdate');
        //Call the update method
        editorApp._onPublishStateUpdate(true);
        expect(publishModelPlugin.onPublishStateUpdate).toHaveBeenCalled();
    });
});
