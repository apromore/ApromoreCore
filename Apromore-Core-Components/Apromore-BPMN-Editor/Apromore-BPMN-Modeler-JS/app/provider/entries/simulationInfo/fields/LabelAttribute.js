var LabelFactory = require('bpmn-js-properties-panel/lib/factory/LabelFactory');

module.exports = function (bpmnFactory, elementRegistry, translate, options) {

    var getSelectedVariable = options.getSelectedVariable;

    return LabelFactory({
            labelText: options.labelText,
            id: options.id,
            getSelectedVariable: getSelectedVariable,
            showLabel: function (element, node) {
                var selectedVariable = getSelectedVariable(element, node);
                return !!selectedVariable;
            }

        });
};