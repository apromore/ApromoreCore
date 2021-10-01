const webpack = require("webpack");
const path = require('path');
const LowerCaseNamePlugin = require('webpack-lowercase-name');

module.exports = {
    entry: {
        LogAnimationBpmn: './src/loganimation/logAnimationBpmn.js',
        ProcessDiscoverer: './src/processdiscoverer/index.js',
        BPMNEditor: './src/bpmneditor/index.js'
    },
    output: {
        filename: '[lc-name].js',
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
        }),
        new LowerCaseNamePlugin()
    ],

};
