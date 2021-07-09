let webpackConfig = require('./webpack.config.js');

module.exports = function (config) {
    config.set({
        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',

        plugins: ['@metahub/karma-jasmine-jquery', 'karma-*'],
        frameworks: ['jasmine-jquery'],

        // list of files / patterns to load in the browser
        files: [
            'src/bpmneditor/editor/bpmnio/bpmn-modeler.development.js',
            'src/bpmneditor/libs/iscroll.js',
            'src/bpmneditor/libs/ext-2.0.2/adapter/ext/ext-base.js',
            'src/bpmneditor/libs/ext-2.0.2/ext-all.js',
            'src/bpmneditor/libs/ext-2.0.2/ext-core.js',
            'src/bpmneditor/libs/ext-2.0.2/color-field.js',
            'src/bpmneditor/editor/i18n/translation_en.js',
            'src/bpmneditor/editor/i18n/translation_signavio_en.js',

            {pattern: 'test/loganimation/*.spec.js', watched: false},
            {pattern: 'test/loganimation/fixtures/*.html', watched: false, served: true, included: false},
            {pattern: 'test/loganimation/fixtures/*.bpmn', watched: false, served: true, included: false}
        ],

        preprocessors: {
            './test/loganimation/*.spec.js': ['webpack'],
        },
        webpack: webpackConfig,
        webpackMiddleware: {
            noInfo: true
        },

        exclude: [],
        reporters: ['progress'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_DISABLE,
        autoWatch: true,
        browsers: ['ChromeHeadless'],
        singleRun: true,
    });
};
