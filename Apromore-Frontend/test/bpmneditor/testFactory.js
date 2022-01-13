import EditorApp from "../../src/bpmneditor/editorapp";
import Editor from "../../src/bpmneditor/editor";
import CONFIG from "../../src/bpmneditor/config";
import Utils from "../../src/bpmneditor/utils";
import BpmnJS from "../../src/bpmneditor/editor/bpmnio/bpmn-modeler.development";
import * as testSupport from "./testSupport"

export let pluginsConfigXML = require('./fixtures/plugins.xml').default;
export let pluginsConfigSimpleXML = require('./fixtures/pluginsSimple.xml').default;
export let bpmnSimpleXML = require('./fixtures/simpleMap.bpmn').default;

export async function createEditorAppWithModelAndPlugins() {
    let editorApp = createEditorAppWithSimulationPanel();
    await initEditorApp(editorApp, bpmnSimpleXML, pluginsConfigXML, true);
    return editorApp;
}

export async function createEditorAppWithModelAndSimplePlugins() {
    let editorApp = createEditorAppWithSimulationPanel();
    await initEditorApp(editorApp, bpmnSimpleXML, pluginsConfigSimpleXML, true);
    return editorApp;
}

export async function createEditorAppWithDataAndCustomButtons() {
    let editorApp = createEditorAppWithCustomToolbarButtons();
    await initEditorApp(editorApp, bpmnSimpleXML, pluginsConfigXML, true);
    return editorApp;
}

export async function createEditorAppForAnimation() {
    let editorApp = createEditorAppWithoutSimulationPanel();
    await initEditorApp(editorApp, bpmnSimpleXML, pluginsConfigXML, true);
    return editorApp;
}

export async function initEditorApp(editorApp, bpmnXML, pluginsConfigXML, pluginSuccess) {
    if (!editorApp) fail('EditorApp must be created first');

    let parsedPlugins = new DOMParser().parseFromString(pluginsConfigXML, "text/xml");
    let spy = spyOn($, 'ajax').and.callFake(testSupport.createMockAjaxResponseFunction(parsedPlugins, pluginSuccess));

    await editorApp.init({
        xml: bpmnXML
    }).catch(err => {
        fail('Error in initializing EditorApp. Error: ' + err.message);
    });
}

export function createEditorAppWithCustomToolbarButtons() {
    return createEditorApp({
        id: 'editorAppContainer',
        fullscreen: true,
        useSimulationPanel: true,
        viewOnly: false,
        langTag: 'en',
        disabledButtons: [
            window.Apromore.I18N.Save.save,
            window.Apromore.I18N.Save.saveAs,
            window.Apromore.I18N.File.svg,
            window.Apromore.I18N.File.bpmn,
            window.Apromore.I18N.File.pdf,
            window.Apromore.I18N.Undo.undo,
            window.Apromore.I18N.Undo.redo,
            window.Apromore.I18N.Share.share,
            window.Apromore.I18N.Share.publish,
            window.Apromore.I18N.SimulationPanel.simulateModel
        ]
    });
}

export function createEditorAppWithoutSimulationPanel() {
    return createEditorApp({
        id: 'editorAppContainer',
        fullscreen: true,
        useSimulationPanel: false,
        viewOnly: false,
        langTag: 'en'
    });
}

export function createEditorAppWithSimulationPanel() {
    return createEditorApp({
        id: 'editorAppContainer',
        fullscreen: true,
        useSimulationPanel: true,
        viewOnly: false,
        langTag: 'en'
    });
}

export function createEditorAppWithoutFullscreenMode() {
    return createEditorApp({
        id: 'editorAppContainer',
        fullscreen: false,
        useSimulationPanel: true,
        viewOnly: false,
        langTag: 'en'
    });
}

function createEditorApp(config) {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorAppFixture.html');
    return new EditorApp(config);
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

export function createEditorWithoutData() {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorFixture.html');

    let editor =  new Editor({
        width: CONFIG.CANVAS_WIDTH,
        height: CONFIG.CANVAS_HEIGHT,
        id: Utils.provideId(),
        parentNode: $('#editorContainer')[0]
    });

    editor.attachEditor(new BpmnJS({
        container: '#' + editor.rootNode.id,
        langTag: 'en',
        propertiesPanel: { parent: '#js-properties-panel' }
    }));

    return editor;
}

export async function createEditorWithData() {
    jasmine.getFixtures().fixturesPath = 'base/test/bpmneditor/fixtures';
    loadFixtures('editorFixture.html');

    let editor =  new Editor({
        width: CONFIG.CANVAS_WIDTH,
        height: CONFIG.CANVAS_HEIGHT,
        id: Utils.provideId(),
        parentNode: $('#editorContainer')[0]
    });

    editor.attachEditor(new BpmnJS({
        container: '#' + editor.rootNode.id,
        langTag: 'en',
        propertiesPanel: { parent: '#js-properties-panel' }
    }));

    await editor.importXML(bpmnSimpleXML).catch(err => fail(err));

    return editor;
}
