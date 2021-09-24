import * as testFactory from "../testFactory";
import Toolbar from "../../../src/bpmneditor/plugins/toolbar";

describe('After the EditorApp has been initialized with a BPMN model with Toolbar plugin', function () {

    it('The Toolbar plugin can be loaded from a simple configuration', async function() {
        let editorApp = await testFactory.createEditorAppWithModelAndSimplePlugins();
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[0]).toBeInstanceOf(Toolbar);
    });

    it('The Toolbar plugin has correct buttons created from a simple configuration', async function() {
        let editorApp = await testFactory.createEditorAppWithModelAndSimplePlugins();
        let toolbarPlugin = editorApp.getActivatedPlugins()[0];
        expect(toolbarPlugin.getNumberOfButtons()).toEqual(2);

        expect(toolbarPlugin.getButtonByIndex(0).btnId).toEqual('ap-id-editor-undo-btn');
        expect(toolbarPlugin.getButtonByIndex(0).buttonInstance).toBeInstanceOf(Ext.Button);
        expect(toolbarPlugin.getButtonByIndex(1).btnId).toEqual('ap-id-editor-redo-btn');
        expect(toolbarPlugin.getButtonByIndex(1).buttonInstance).toBeInstanceOf(Ext.Button);
    });

    it('The Toolbar plugin can be loaded from a complex configuration', async function() {
        let editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[0]).toBeInstanceOf(Toolbar);
    });

    it('The Toolbar plugin has correct buttons created from a complex configuration', async function() {
        let editorApp = await testFactory.createEditorAppWithModelAndPlugins();
        let toolbarPlugin = editorApp.getActivatedPlugins()[0];
        expect(toolbarPlugin.getNumberOfButtons()).toEqual(12);

        expect(toolbarPlugin.getButtonByIndex(0).btnId).toEqual('ap-id-editor-save-btn');
        expect(toolbarPlugin.getButtonByIndex(0).buttonInstance).toBeInstanceOf(Ext.Button);
        expect(toolbarPlugin.getButtonByIndex(11).btnId).toEqual('ap-id-editor-share-btn');
        expect(toolbarPlugin.getButtonByIndex(11).buttonInstance).toBeInstanceOf(Ext.Button);
        expect(toolbarPlugin.getButtonById('ap-id-editor-undo-btn').buttonInstance).toBeInstanceOf(Ext.Button);
    });
});
