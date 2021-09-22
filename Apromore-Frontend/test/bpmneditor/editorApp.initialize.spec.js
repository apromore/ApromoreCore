import EditorApp from "../../src/bpmneditor/editorapp";
import Log from '../../src/bpmneditor/logger';

describe('After the EditorApp has been created', function () {
    let editorApp;

    beforeEach(async function () {
        $(window.document.body).empty();
        let editorAppContainer = $('<div id="editorAppContainer"></div>');
        $(window.document.body).append(editorAppContainer[0]);

        editorApp = new EditorApp({
            id: 'editorAppContainer',
            fullscreen: true
        });

    });

    it('It can initialize successfully with plugins', async function () {
        let pluginsConfig = require('./fixtures/plugins.xml').default;
        let parsedPlugins = new DOMParser().parseFromString(pluginsConfig, "text/xml");
        spyOn($, 'ajax').and.callFake(ajax_response(parsedPlugins, true));

        let bpmnXML = require('./fixtures/simpleMap.bpmn');
        await editorApp.init({
            xml: bpmnXML.default,
            callBack: () => {},
            preventFitDelay: true
        }).catch(err => {
            fail('Error in initializing EditorApp. Error: ' + err.message);
        });

        expect(editorApp.getActivatedPlugins().length).toEqual(8);
    });

    it('It can initialize successfully despite errors in Ajax request for plugin configuration', async function () {
        let parsedPlugins = new DOMParser().parseFromString('Error', "text/xml");
        spyOn($, 'ajax').and.callFake(ajax_response(parsedPlugins, false));
        let logErrorSpy = spyOn(Log, 'warn');

        let bpmnXML = require('./fixtures/simpleMap.bpmn');
        await editorApp.init({
            xml: bpmnXML.default,
            callBack: () => {},
            preventFitDelay: true
        }).catch(err => {
            fail('Error in initializing EditorApp. Error: ' + err);
        });

        expect(logErrorSpy).toHaveBeenCalled();
        expect(editorApp.getActivatedPlugins().length).toEqual(0);
    });

    it('It can initialize successfully despite errors in loading plugins from the plugin configuration', async function () {
        spyOn($, 'ajax').and.callFake(ajax_response('', true));
        let logErrorSpy = spyOn(Log, 'warn');

        let bpmnXML = require('./fixtures/simpleMap.bpmn');
        await editorApp.init({
            xml: bpmnXML.default,
            callBack: () => {},
            preventFitDelay: true
        }).catch(err => {
            fail('Error in initializing EditorApp. Error: ' + err.message);
        });

        expect(logErrorSpy).toHaveBeenCalled();
        expect(editorApp.getActivatedPlugins().length).toEqual(0);
    });

    // A simple way of mocking Ajax response for testing
    // To be more sophisticated, can use jasmine-ajax extension per need basis
    function ajax_response(response, success) {
        return function (params) {
            if (success) {
                params.success(response);
            } else {
                params.error(response);
            }
        };
    }
});
