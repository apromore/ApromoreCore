const webpack = require("webpack");
const path = require('path');

module.exports = {
    entry: {
        LogAnimationBpmn: './src/loganimation/logAnimationBpmn.js',
        ProcessDiscoverer: './src/processdiscoverer/index.js',
        BPMNEditor: './src/bpmneditor/index.js'
    },
    output: {
        filename: (chunkData) => {
            if (chunkData.chunk.name === 'ProcessDiscoverer') {
                return 'processdiscoverer.js';
            }
            if (chunkData.chunk.name === 'LogAnimationBpmn') {
                return 'loganimationbpmn.js';
            }
            if (chunkData.chunk.name === 'BPMNEditor') {
                return 'bpmneditor.js';
            }
            return chunkData.chunk.name + '.js';
        },
        path: path.resolve(__dirname, 'dist'),
        library: ['Apromore', '[name]'],   // Important
        libraryTarget: 'umd',   // Important
        libraryExport: 'default',
        umdNamedDefine: true
    },
    mode: 'development',
    devtool: 'source-map',
    module: {
        rules: [
            {test: /\.css|\.bpmn|\.xml|\.txt$/, use: 'raw-loader'}
        ]
    },
    resolve: {
        mainFields: [
            'browser',
            'module',
            'main'
        ]
    },

    plugins: [
        new webpack.ProvidePlugin({
            "$":"jquery",
            "$j":"jquery",
            "jQuery":"jquery",
            "window.jQuery":"jquery"
        })
    ],

};
