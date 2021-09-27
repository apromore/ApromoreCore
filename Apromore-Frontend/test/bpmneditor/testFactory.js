import EditorApp from "../../src/bpmneditor/editorapp";
let pluginsConfig = require('./fixtures/plugins.xml');
let pluginsConfigSimple = require('./fixtures/pluginsSimple.xml');
let bpmnSimple = require('./fixtures/simpleMap.bpmn');

export async function createEditorAppWithModelAndPlugins() {
    return await createEditorApp(pluginsConfig.default, bpmnSimple.default);
}

export async function createEditorAppWithModelAndSimplePlugins() {
    return await createEditorApp(pluginsConfigSimple.default, bpmnSimple.default);
}

async function createEditorApp(pluginsConfigXML, bpmnXML) {
    $(window.document.body).empty(); // Clear the HTML document to avoid conflicts with the content loaded in other tests
    $(window.document.body).append($('<div id="editorAppContainer"></div>')[0]);

    let editorApp = new EditorApp({
        id: 'editorAppContainer',
        fullscreen: true
    });

    let parsedPlugins = new DOMParser().parseFromString(pluginsConfigXML, "text/xml");
    let spy = spyOn($, 'ajax').and.callFake(ajax_response(parsedPlugins, true));

    await editorApp.init({
        xml: bpmnXML,
        callBack: () => {},
        preventFitDelay: true
    }).catch(err => {
        fail('Error in initializing EditorApp. Error: ' + err.message);
    });

    spy.calls.reset();

    return editorApp;
}

export async function clickButton(buttonId, callBack) {
    let element = Ext.getCmp(buttonId);
    element.handler.call(element.scope);
    // element.fireEvent('click', element);
}


// A simple way of mocking Ajax response for testing
// To be more sophisticated, can use jasmine-ajax
function ajax_response(response, success) {
    return function (params) {
        if (success) {
            params.success(response);
        } else {
            params.error(response);
        }
    }
}
