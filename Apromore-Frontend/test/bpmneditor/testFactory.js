import EditorApp from "../../src/bpmneditor/editorapp";

export async function createEditorAppWithModelAndPlugins() {
    $(window.document.body).empty();
    $(window.document.body).append($('<div id="editorAppContainer"></div>')[0]);
    let editorApp = new EditorApp({
        id: 'editorAppContainer',
        fullscreen: true
    });

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

    return editorApp;
}

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
