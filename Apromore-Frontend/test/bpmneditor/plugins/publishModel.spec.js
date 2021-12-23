import * as testFactory from "../testFactory";
import PublishModel from "../../../src/bpmneditor/plugins/publishModel";

describe('After the EditorApp has been initialized with a BPMN model with Publish model plugin', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The Publish model plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[8]).toBeInstanceOf(PublishModel);
    });

    it('Clicking on the Publish model button can activate the publish model action in the editor', async function() {
        // Mock Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel (the actual version calls to the server)
        if (!window.Apromore) window.Apromore = {};
        if (!window.Apromore.BPMNEditor) window.Apromore.BPMNEditor = {};
        if (!window.Apromore.BPMNEditor.Plugins) window.Apromore.BPMNEditor.Plugins = {};
        if (!window.Apromore.BPMNEditor.Plugins.PublishModel) window.Apromore.BPMNEditor.Plugins.PublishModel = {};
        window.Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel = function () {};

        spyOn(window.Apromore.BPMNEditor.Plugins.PublishModel, 'apromorePublishModel');
        let element = Ext.getCmp("ap-id-editor-publish-model-btn");
        element.handler.call(element.scope);
        expect(window.Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel).toHaveBeenCalled();
    });
});
