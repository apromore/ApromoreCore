import EditorApp from "../../src/bpmneditor/editorapp";
import Editor from "../../src/bpmneditor/editor";
import CONFIG from "../../src/bpmneditor/config";
import Utils from "../../src/bpmneditor/utils";
import BpmnJS from "../../src/bpmneditor/editor/bpmnio/bpmn-modeler.development";

let pluginsConfig = require('./fixtures/plugins.xml');
let pluginsConfigSimple = require('./fixtures/pluginsSimple.xml');
let bpmnSimple = require('./fixtures/simpleMap.bpmn');

export async function createEditorAppWithModelAndPlugins() {
    return await createEditorApp(pluginsConfig.default, bpmnSimple.default);
}

export function createEmptyEditorApp() {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorAppFixture.html');

    let editorApp = new EditorApp({
        id: 'editorAppContainer',
        fullscreen: true
    });

    return editorApp;
}

export async function createEditorAppWithModelAndSimplePlugins() {
    return await createEditorApp(pluginsConfigSimple.default, bpmnSimple.default);
}

async function createEditorApp(pluginsConfigXML, bpmnXML) {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorAppFixture.html');

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

export function createEmptyEditor() {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorFixture.html');

    let editor =  new Editor({
        width: CONFIG.CANVAS_WIDTH,
        height: CONFIG.CANVAS_HEIGHT,
        id: Utils.provideId(),
        parentNode: $('#editorContainer')[0],
        preventFitDelay: true
    });

    return editor;
}

export async function createEditorWithBPMNIO() {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorFixture.html');

    let editor =  new Editor({
        width: CONFIG.CANVAS_WIDTH,
        height: CONFIG.CANVAS_HEIGHT,
        id: Utils.provideId(),
        parentNode: $('#editorContainer')[0],
        preventFitDelay: true
    });

    await editor.attachEditor(new BpmnJS({
        container: '#' + editor.rootNode.id,
        langTag: 'en',
        propertiesPanel: { parent: '#js-properties-panel' }
    }));

    return editor;
}

export async function createEditorWithSimpleMap() {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorFixture.html');

    let editor =  new Editor({
        width: CONFIG.CANVAS_WIDTH,
        height: CONFIG.CANVAS_HEIGHT,
        id: Utils.provideId(),
        parentNode: $('#editorContainer')[0],
        preventFitDelay: true
    });

    await editor.attachEditor(new BpmnJS({
        container: '#' + editor.rootNode.id,
        langTag: 'en',
        propertiesPanel: { parent: '#js-properties-panel' }
    }));

    let bpmn = require('./fixtures/simpleMap.bpmn');
    await editor.importXML(bpmn.default, () => {}).catch(err => fail(err));

    return editor;
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
