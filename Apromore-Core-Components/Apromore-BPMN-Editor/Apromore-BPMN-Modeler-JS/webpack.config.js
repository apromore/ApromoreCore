const webpack = require("webpack");
var path = require('path');
var CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  entry: {
    bundle: ['./app/CustomModeler.js']
  },
  optimization: {
    minimize: false
  },
  mode: 'development',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bpmn-modeler.development.js',
    libraryTarget: 'umd',
    library: 'BpmnJS',
    umdNamedDefine: true,
    libraryExport: 'default',
  },
  resolve: {
    mainFields: ['browser', 'module', 'main'],
  },
  module: {
    rules: [
      {
        test: /\.bpmn$/,
        use: {
          loader: 'raw-loader'
        }
      },
      {
        test: /\.less$/,
        use: [
          { loader: 'style-loader' },
          { loader: 'css-loader' },
          { loader: 'less-loader' }
        ],
      }
    ]
  },
  plugins: [
    new webpack.ProvidePlugin({
      "$":"jquery",
      "$j":"jquery",
      "jQuery":"jquery",
      "window.jQuery":"jquery"
    }),
    new CopyWebpackPlugin({
      patterns: [
        { from: 'assets/**', to: 'vendor/bpmn-js', context: 'node_modules/bpmn-js/dist/' }
      ]
    }),
  ],
};