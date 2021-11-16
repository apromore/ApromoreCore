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
        expect(editorApp.getActivatedPlugins().length).toEqual(8);
        expect(toolbarPlugin.getNumberOfButtons()).toEqual(12);

        expect(toolbarPlugin.getButtonByIndex(0).btnId).toEqual('ap-id-editor-save-btn');
        expect(toolbarPlugin.getButtonByIndex(0).buttonInstance).toBeInstanceOf(Ext.Button);
        expect(toolbarPlugin.getButtonByIndex(11).btnId).toEqual('ap-id-editor-share-btn');
        expect(toolbarPlugin.getButtonByIndex(11).buttonInstance).toBeInstanceOf(Ext.Button);
        expect(toolbarPlugin.getButtonById('ap-id-editor-undo-btn').buttonInstance).toBeInstanceOf(Ext.Button);
    });

    it('The Toolbar plugin has correct button status from a custom configuration', async function() {
        let editorApp = await testFactory.createEditorAppWithDataAndCustomButtons();
        let toolbarPlugin = editorApp.getActivatedPlugins()[0];
        expect(toolbarPlugin.getNumberOfButtons()).toEqual(12);
        expect(toolbarPlugin.getButtonById('ap-id-editor-save-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-save-as-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-export-svg-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-export-bpmn-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-export-pdf-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-undo-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-redo-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-simulate-model-btn').buttonInstance.disabled).toBeTruthy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-share-btn').buttonInstance.disabled).toBeTruthy();

        expect(toolbarPlugin.getButtonById('ap-id-editor-zoomIn-btn').buttonInstance.disabled).toBeFalsy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-zoomOut-btn').buttonInstance.disabled).toBeFalsy();
        expect(toolbarPlugin.getButtonById('ap-id-editor-zoomFit-btn').buttonInstance.disabled).toBeFalsy();
    });
});
