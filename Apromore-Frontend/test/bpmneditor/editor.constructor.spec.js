import Editor from "../../src/bpmneditor/editor";
import CONFIG from "../../src/bpmneditor/config";
import Utils from "../../src/bpmneditor/utils";

describe('After the Editor has been created', function () {
    let editor;

    beforeEach(async function() {
        //window.document.body.innerHTML = ''; // make sure other loaded fixtures in the document don't affect this test
        $(window.document.body).empty();
        let editorContainer = $('<div id="editorContainer"></div>');
        $(window.document.body).append(editorContainer[0]);

        editor =  new Editor({
            width: CONFIG.CANVAS_WIDTH,
            height: CONFIG.CANVAS_HEIGHT,
            id: Utils.provideId(),
            parentNode: editorContainer[0],
            preventFitDelay: true
        });
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
