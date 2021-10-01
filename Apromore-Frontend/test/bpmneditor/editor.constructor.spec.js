import * as testFactory from './testFactory';

describe('After the Editor has been created', function () {
    let editor;

    beforeEach(function() {
        editor = testFactory.createEmptyEditor();
    });

    it('It has set up initial data correctly', function() {
        expect(editor).not.toBeUndefined();
        editor.getXML().then(function (result) {
            expect(result).toBeFalsy();
        });
        editor.getSVG().then(function (result) {
            expect(result).toBeFalsy();
        });
        console.log(editor.getSVGContainer());
        expect(editor.getSVGContainer()).toBeUndefined();
        expect(editor.getSVGViewport()).toBeUndefined();
        expect(editor.canUndo()).toBeFalse();
        expect(editor.canRedo()).toBeFalse();
        expect(editor.undo()).toBeFalse();
        expect(editor.redo()).toBeFalse();
        expect(editor.getIncomingFlowId()).toBeFalse();
        expect(editor.getOutgoingFlowId()).toBeFalse();
        expect(editor.zoomIn()).toBeFalse();
        expect(editor.zoomOut()).toBeFalse();
        expect(editor.zoomFitToModel()).toBeFalse();
        expect(editor.zoomDefault()).toBeFalse();
        expect(editor.addCommandStackChangeListener()).toBeFalse();
        expect(editor.addEventBusListener()).toBeFalse();
    });

});
