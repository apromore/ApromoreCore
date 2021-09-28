import * as testFactory from './testFactory';

describe('After the Editor has attached an instance of bpmn.io', function () {
    let editor;

    beforeEach(async function() {
        editor = await testFactory.createEditorWithBPMNIO().catch(err => fail(err));
    });

    it('It has set up initial data correctly', async function() {
        expect(editor).not.toBeUndefined();
        expect(editor.getSVGContainer()).not.toBeUndefined();
        expect(editor.getSVGViewport()).not.toBeUndefined();
        expect(editor.canUndo()).toBeFalse();
        expect(editor.canRedo()).toBeFalse();
        expect(editor.undo()).toBeTrue();
        expect(editor.redo()).toBeTrue();
        expect(editor.getIncomingFlowId()).toBeFalse();
        expect(editor.getOutgoingFlowId()).toBeFalse();
        expect(editor.zoomIn()).toBeTrue();
        expect(editor.zoomOut()).toBeTrue();
        expect(editor.zoomFitToModel()).toBeTrue();
        expect(editor.zoomDefault()).toBeTrue();
        expect(editor.addCommandStackChangeListener(() => {})).toBeTrue();
        expect(editor.addEventBusListener('test',() => {})).toBeTrue();
    });

});
