import * as testFactory from "../testFactory";
import Share from "../../../src/bpmneditor/plugins/share";

describe('After the EditorApp has been initialized with a BPMN model with Share plugin', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('The Share plugin has been loaded', async function() {
        let plugins = editorApp.getActivatedPlugins();
        expect(plugins[6]).toBeInstanceOf(Share);
    });

    it('Clicking on the Share button can activate the Undo action in the editor', async function() {
        // Mock Apromore.BPMNEditor.Plugins.Share.shareExt (the actual version calls to the server)
        if (!window.Apromore) window.Apromore = {};
        if (!window.Apromore.BPMNEditor) window.Apromore.BPMNEditor = {};
        if (!window.Apromore.BPMNEditor.Plugins) window.Apromore.BPMNEditor.Plugins = {};
        if (!window.Apromore.BPMNEditor.Plugins.Share) window.Apromore.BPMNEditor.Plugins.Share = {};
        window.Apromore.BPMNEditor.Plugins.Share.shareExt = function () {};

        spyOn(window.Apromore.BPMNEditor.Plugins.Share, 'shareExt');
        let element = Ext.getCmp("ap-id-editor-share-btn");
        element.handler.call(element.scope);
        expect(window.Apromore.BPMNEditor.Plugins.Share.shareExt).toHaveBeenCalled();
    });
});
