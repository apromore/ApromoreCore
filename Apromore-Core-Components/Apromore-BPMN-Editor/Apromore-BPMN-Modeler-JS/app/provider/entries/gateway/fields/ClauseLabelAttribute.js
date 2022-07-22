var LabelFactory = require('bpmn-js-properties-panel/lib/factory/LabelFactory');

module.exports = function (bpmnFactory, elementRegistry, translate, options) {

    var getSelectedClause = options.getSelectedClause;

    return LabelFactory({
            labelText: options.labelText,
            id: options.id,
            getSelectedClause: getSelectedClause,
            showLabel: function (element, node) {
                return !!getSelectedClause(element, node);               
            }

        });
};