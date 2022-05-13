import savePlugin from './apromoreSave';
import exportPlugin from './export';
import pdfPlugin from './pdf';
import publishModelPlugin from './publishModel';
import attachmentPlugin from './attachment';
import sharePlugin from './share';
import simModelPlugin from './simulateModel';
import toolbarPlugin from './toolbar';
import undoPlugin from './undo';
import viewPlugin from './view';
import fontChangePlugin from './fontChange';

//Add plugins to list
let Plugins = {
    Attachment: attachmentPlugin,
    ApromoreSave: savePlugin,
    Export: exportPlugin,
    File: pdfPlugin,
    PublishModel: publishModelPlugin,
    Share: sharePlugin,
    SimulateModel: simModelPlugin,
    Toolbar: toolbarPlugin,
    Undo: undoPlugin,
    View: viewPlugin,
	FontChange:fontChangePlugin	
};

export default Plugins;
