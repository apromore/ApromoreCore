import config from './scripts/config';
import utils from './scripts/utils';
import log from './scripts/logger';
import plugins from './scripts/plugins/plugins';
import editor from './scripts/editor';
import editorApp from './scripts/editorapp';

let BPMNEditor = {
    CONFIG: config,
    Utils: utils,
    Log: log,
    Plugins: plugins,
    Editor: editor,
    EditorApp: editorApp
};

export default BPMNEditor;
