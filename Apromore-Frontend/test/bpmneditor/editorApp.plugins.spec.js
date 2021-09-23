import EditorApp from "../../src/bpmneditor/editorapp";
import CONFIG from "../../src/bpmneditor/config";
import Utils from "../../src/bpmneditor/utils";
import Toolbar from "../../src/bpmneditor/plugins/toolbar";
import Undo from "../../src/bpmneditor/plugins/undo";
import ApromoreSave from "../../src/bpmneditor/plugins/apromoreSave";
import * as testFactory from "./testFactory"
import Export from "../../src/bpmneditor/plugins/export";
import View from "../../src/bpmneditor/plugins/view";
import Share from "../../src/bpmneditor/plugins/share";
import File from "../../src/bpmneditor/plugins/pdf";
import SimulationPanel from "../../src/bpmneditor/plugins/simulationPanel";

describe('After the EditorApp has been initialized with a BPMN model and loaded plugins', function () {
    let editorApp;

    beforeEach(async function() {
        editorApp = await testFactory.createEditorAppWithModelAndPlugins();
    });

    it('It was able to load all plugins correctly', async function() {
        // let plugins = editorApp.getActivatedPlugins();
        // expect(plugins.length).toEqual(8);
        // expect(plugins[0]).toBeInstanceOf(Toolbar);
        // expect(plugins[1]).toBeInstanceOf(Undo);
        // expect(plugins[2]).toBeInstanceOf(ApromoreSave);
        // expect(plugins[3]).toBeInstanceOf(Export);
        // expect(plugins[4]).toBeInstanceOf(File);
        // expect(plugins[5]).toBeInstanceOf(View);
        // expect(plugins[6]).toBeInstanceOf(Share);
        // expect(plugins[7]).toBeInstanceOf(SimulationPanel);
    });

    it('It is able to set up initial status of toolbar buttons correctly', async function() {
        // expect(plugins[1]).toBeInstanceOf(Undo);

    });
});
