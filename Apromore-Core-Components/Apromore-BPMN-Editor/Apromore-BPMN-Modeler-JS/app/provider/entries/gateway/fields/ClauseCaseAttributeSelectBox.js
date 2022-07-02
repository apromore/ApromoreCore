var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  ResourceHelper = require('../../../../helper/ResourceHelper'),
  ElementHelper = require('../../../../helper/ElementHelper'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  createUUID = require('../../../../utils/Utils').createUUID,
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');


module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  var selectBoxId = ['clause', createUUID(), 'case-attribute'].join('-');

  function createVariableOptions() {
    var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);

    var variableWithNotEmptyName = variables.values.filter(function (variable) {
      return variable.name;
    });

    return variableWithNotEmptyName.map(function (variable) {
      return {
        name: variable.name,
        value: variable.name
      };
    });
  }


  let currentSelection;

  return entryFactory.selectBox(translate, {
    id: selectBoxId,
    label: 'Case Attribute',
    modelProperty: 'variableName',
    selectOptions: createVariableOptions,

    get: function (_element, _node) {
      let sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      if (!sequenceFlow || !sequenceFlow.values || sequenceFlow.values.length == 0) {
        return;
      }
      let expression = sequenceFlow.values[0];

      if (!expression) {
        return;
      }
      var selected = expression.values.filter(function (clause) {
        return currentSelection && clause.variableName == currentSelection.variableName;
      });
      let variableName ='';
      if(selected && selected.length >0 ){
        variableName = selected[0].variableName ;
      }

      return { variableName: variableName };
    },

    set: function (element, values, _node) {
      let sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
      let clause = expression && expression.values.length >0 && expression.values[0];
      if (clause) {
        clause.variableName = values.variableName;
        currentSelection = clause;
      }else{
        currentSelection = null;
      }
     
    }
  });
};