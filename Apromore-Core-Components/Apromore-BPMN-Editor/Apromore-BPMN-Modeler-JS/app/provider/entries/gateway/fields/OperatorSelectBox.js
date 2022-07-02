var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');

var createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  let sequenceFlow;

  return entryFactory.selectBox(translate, {
    id: 'operator_' + createUUID(),
    label: 'Operator',
    modelProperty: 'operator',
    selectOptions: createOperatorOptions(),

    get: function (_element, _node) {
      if (!sequenceFlow) {
        sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      }
      let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
      return { operator: expression && expression.operator };
    },

    set: function (element, values, _node) {
      sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
      if (expression) {
        expression.operator = values.operator;
      }
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