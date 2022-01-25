import Log from '../../src/bpmneditor/logger';
import * as testFactory from "./testFactory";
import * as testSupport from "./testSupport"

describe('After the EditorApp has been created with a full configuration', function () {
    let editorApp;

    beforeEach(async function () {
        editorApp = testFactory.createEditorAppWithSimulationPanel();
    });

    it('It can initialize successfully with plugins', async function () {
        await testFactory.initEditorApp(editorApp, testFactory.bpmnSimpleXML, testFactory.pluginsConfigXML, true);
        expect(editorApp.getActivatedPlugins().length).toEqual(10);
    });

    it('It can initialize in case of errors in Ajax request for plugin configuration', async function () {
        await testFactory.initEditorApp(editorApp, testFactory.bpmnSimpleXML, testFactory.pluginsConfigXML, false);
        expect(editorApp.getActivatedPlugins().length).toEqual(0);
    });

    it('It can initialize successfully in case of errors during the plugin loading process', async function () {
        spyOn($, 'ajax').and.callFake(testSupport.createMockAjaxResponseFunction('', true));
        let logErrorSpy = spyOn(Log, 'warn');

        let bpmnXML = require('./fixtures/simpleMap.bpmn');
        await editorApp.init({
            xml: bpmnXML.default
        }).catch(err => {
            fail('Error in initializing EditorApp. Error: ' + err.message);
        });

        expect(logErrorSpy).toHaveBeenCalled();
        expect(editorApp.getActivatedPlugins().length).toEqual(0);
    });
});
