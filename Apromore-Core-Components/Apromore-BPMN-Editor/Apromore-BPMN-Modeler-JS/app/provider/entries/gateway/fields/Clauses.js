var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError;
var SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');
var createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (element, bpmnFactory, elementRegistry, translate, options) {


  var variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'clauses' + createUUID(),
    label: 'Clause',
    modelProperties: 'clause',
    idGeneration: false,

    createExtensionElement: function (element, extensionElements, _value) {
      let expression = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      console.log(expression);
      var clause = SequenceFlowHelper.createClause(bpmnFactory, translate, options.outgoingElementId, true);
      return cmdHelper.addElementsTolist(element, expression, 'values', [clause]);
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      let expression = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      var selectedClause = expression.values[idx];

      if (!expression || !selectedClause) {
        return {};
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedClause.id });

      return cmdHelper.removeElementsFromList(element, expression, 'values',
        null, [selectedClause]);
    },

    getExtensionElements: function (_element) {
      return SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true).values || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      var selectedClause = expression.values[idx];

      option.text = selectedClause && selectedClause.variableName;
    }
  });

  return {
    entries: variableEntry
  };
};