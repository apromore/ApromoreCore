var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');

var createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (bpmnFactory, elementRegistry, translate, options, sequenceFlow) {

  return entryFactory.selectBox(translate, {
    id: 'operator_select_' + options.outgoingElementId,
    label: translate('gateway.expression.operator.label'),
    modelProperty: 'operator',
    selectOptions: createOperatorOptions(),
    get: function (_element, _node) {
      let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
      return { operator: expression && expression.operator || 'AND' };
    },

    set: function (_element, values, _node) {
      let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
      if (!expression) {
        sequenceFlow = SequenceFlowHelper.createExpression(bpmnFactory, elementRegistry, sequenceFlow, true);
        expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
      }
      var cmd = cmdHelper.updateBusinessObject(_element, expression, {
        operator: values.operator
      });
      return cmd;
    }

  });
};

function createOperatorOptions() {
  return [
    {
      name: 'AND',
      value: 'AND'
    },
    {
      name: 'OR',
      value: 'OR'
    }
  ];
}