import Editor from "../../src/bpmneditor/editor";
import CONFIG from "../../src/bpmneditor/config";
import Utils from "../../src/bpmneditor/utils";
import BpmnJS from "../../src/bpmneditor/editor/bpmnio/bpmn-modeler.development";

describe('After the Editor has loaded successfully a BPMN model', function () {
    let editor;

    beforeEach(async function() {
        $(window.document.body).empty();
        let editorContainer = $('<div id="editorContainer"></div>');
        let propertiesPanelContainer = $('<div id="js-properties-panel"></div>');
        $(window.document.body).append(editorContainer[0]);
        $(window.document.body).append(propertiesPanelContainer[0]);

        editor =  new Editor({
            width: CONFIG.CANVAS_WIDTH,
            height: CONFIG.CANVAS_HEIGHT,
            id: Utils.provideId(),
            parentNode: editorContainer[0],
            preventFitDelay: true
        });

        await editor.attachEditor(new BpmnJS({
            container: '#' + editor.rootNode.id,
            langTag: 'en',
            propertiesPanel: { parent: '#js-properties-panel' }
        }));

        let bpmn = require('./fixtures/simpleMap.bpmn');
        await editor.importXML(bpmn.default, () => {}).catch(err => fail(err));

    });

    it('It can get the XML representation of the model', async function() {
        let xml = await editor.getXML().catch(err => fail(err));
        expect(xml).toContain('<?xml');
    });

    it('It can get the SVG representation of the model', async function() {
        let svg = await editor.getSVG().catch(err => fail(err));
        expect(svg).toContain('<svg');
    });

    it('It can get the incoming flow ID of a node', async function() {
        let edgeId = editor.getIncomingFlowId('node_68572fd8-d526-4d0d-bb50-836d33826255');
        expect(edgeId).not.toBeFalsy();

        edgeId = editor.getIncomingFlowId('non-existent nodeId');
        expect(edgeId).toBeUndefined();
    });

    it('It can get the outgoing flow ID of a node', async function() {
        let edgeId = editor.getOutgoingFlowId('node_68572fd8-d526-4d0d-bb50-836d33826255');
        expect(edgeId).not.toBeFalsy();

        edgeId = editor.getOutgoingFlowId('non-existent nodeId');
        expect(edgeId).toBeUndefined();
    });

});
