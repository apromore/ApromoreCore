import EditorApp from "../../src/bpmneditor/editorapp";
import CONFIG from "../../src/bpmneditor/config";
import Utils from "../../src/bpmneditor/utils";

describe('After the EditorApp has been initialized with a BPMN model', function () {
    let editorApp;

    beforeEach(async function() {
        $(window.document.body).empty();
        let editorAppContainer = $('<div id="editorAppContainer"></div>');
        $(window.document.body).append(editorAppContainer[0]);

        editorApp = new EditorApp({
            id: 'editorAppContainer',
            fullscreen: true
        });

        CONFIG.PLUGINS_ENABLED = false;
        let bpmnXML = require('./fixtures/simpleMap.bpmn');
        await editorApp.init({
            xml: bpmnXML.default,
            callBack: () => {},
            preventFitDelay: true
        }).catch (err => {
            fail('Error in initializing EditorApp. Error: ' + err.message);
        });
    });

    it('It can get the XML representation of the model', async function() {
        let xml = await editorApp.getXML().catch(err => fail(err));
        expect(xml).toContain('<?xml');
    });

    it('It can get the SVG representation of the model', async function() {
        let svg = await editorApp.getSVG().catch(err => fail(err));
        expect(svg).toContain('<svg');
    });
});
