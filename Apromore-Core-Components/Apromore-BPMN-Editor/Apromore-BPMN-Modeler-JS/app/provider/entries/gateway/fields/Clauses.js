var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError,
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper'),
  createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (element, bpmnFactory, elementRegistry, translate, options, sequenceFlow) {

  let clause;
  var variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'clauses-' + createUUID(),
    label: 'Clause',
    idGeneration: true,

    createExtensionElement: function (element, extensionElements, _value) {
        clause = SequenceFlowHelper.createClause(bpmnFactory, elementRegistry, sequenceFlow, true);
        if(clause){
          let expression = getExpression();
          return cmdHelper.addElementsTolist(element, expression, 'values', [clause]);
        }
      },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      let expression = getExpression();
      let selectedClause = expression.values[idx];
      if (!expression || !selectedClause) {
        return {};
      }
      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedClause.ope });
      return cmdHelper.removeElementsFromList(element, expression, 'values',
        null, [selectedClause]);
    },

    getExtensionElements: function (_element) {
      let expression = getExpression();
      return  expression && expression.values || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      let expression = getExpression();
      let selectedClause = expression && expression.values && expression.values[idx];
      option.text = (selectedClause && selectedClause.variableName ) || (clause &&  clause.variableName) ;
    }
  });

  function getExpression() {
    if (!sequenceFlow || !sequenceFlow.values || sequenceFlow.values.length == 0) {
      return;
    }
    return sequenceFlow.values[0];
  }

  function getSelectedClause(element, node) {
    var selection = (variableEntry && variableEntry.getSelected(element, node)) || {
      idx: -1
    };
    var expression = getExpression();
    return expression && expression.values && expression.values[selection.idx];
  }

  return {
    entries: variableEntry,
    getSelectedClause: getSelectedClause
  };
};