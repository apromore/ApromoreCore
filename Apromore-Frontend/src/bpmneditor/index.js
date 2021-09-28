import config from './config';
import editorApp from './editorapp';
import plugins from './plugins/plugins';

let BPMNEditor = {
    CONFIG: config,
    Plugins: plugins,
    EditorApp: editorApp
};

export default BPMNEditor;
