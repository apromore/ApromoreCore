import Editor from "../../src/bpmneditor/editor";
import CONFIG from "../../src/bpmneditor/config";
import Utils from "../../src/bpmneditor/utils";
import BpmnJS from "../../src/bpmneditor/editor/bpmnio/bpmn-modeler.development";

describe('After the Editor has attached an instance of bpmn.io', function () {
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
