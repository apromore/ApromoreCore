import * as testFactory from './testFactory';

describe('After the Editor has loaded successfully a BPMN model', function () {
    let editor;

    beforeEach(async function() {
        editor = await testFactory.createEditorWithData();
    });

    it('It can set up initial UI correctly', async function() {
        expect(editor).not.toBeUndefined();
        expect(editor.getSVGContainer()).toBeTruthy();
        expect(editor.getSVGViewport()).toBeTruthy();
        expect(editor.canUndo()).toBeFalse();
        expect(editor.canRedo()).toBeFalse();
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
