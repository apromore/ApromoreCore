import savePlugin from './apromoreSave';
import exportPlugin from './export';
import pdfPlugin from './pdf';
import sharePlugin from './share';
import simModelPlugin from './simulateModel';
import simPanelPlugin from './simulationPanel';
import toolbarPlugin from './toolbar';
import undoPlugin from './undo';
import viewPlugin from './view';

//Add plugins to list
let Plugins = {
    ApromoreSave: savePlugin,
    Export: exportPlugin,
    File: pdfPlugin,
    Share: sharePlugin,
    SimulateModel: simModelPlugin,
    SimulationPanel: simPanelPlugin,
    Toolbar: toolbarPlugin,
    Undo: undoPlugin,
    View: viewPlugin
};

export default Plugins;
