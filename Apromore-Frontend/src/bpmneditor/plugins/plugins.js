import savePlugin from './apromoreSave';
import exportPlugin from './export';
import pdfPlugin from './pdf';
import publishModelPlugin from './publishModel';
import sharePlugin from './share';
import simModelPlugin from './simulateModel';
import toolbarPlugin from './toolbar';
import undoPlugin from './undo';
import viewPlugin from './view';

//Add plugins to list
let Plugins = {
    ApromoreSave: savePlugin,
    Export: exportPlugin,
    File: pdfPlugin,
    PublishModel: publishModelPlugin,
    Share: sharePlugin,
    SimulateModel: simModelPlugin,
    Toolbar: toolbarPlugin,
    Undo: undoPlugin,
    View: viewPlugin
};

export default Plugins;
