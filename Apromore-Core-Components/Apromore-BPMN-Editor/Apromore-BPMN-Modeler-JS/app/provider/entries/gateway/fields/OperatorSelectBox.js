var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');

var createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (bpmnFactory, elementRegistry, translate, options) {


  let expression ;
  

  return entryFactory.selectBox(translate, {
    id: 'operator_' + createUUID(),
    label: 'Operator',
    modelProperty: 'operator',
    selectOptions: createOperatorOptions(),

    get: function (_element, _node) {
      if(!expression){
        expression = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      }
      console.log(expression);
      return { operator: expression && expression.operator };
    },

    set: function (element, values, _node) {
      expression = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      console.log(expression);
      var result = cmdHelper.updateBusinessObject(element, expression, {
        operator: values.operator
      });
      expression = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      console.log(result);
      return result;
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