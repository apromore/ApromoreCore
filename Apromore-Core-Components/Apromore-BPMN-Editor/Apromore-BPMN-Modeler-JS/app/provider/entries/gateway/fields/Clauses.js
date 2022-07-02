var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError,
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper'),
  createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (element, bpmnFactory, elementRegistry, translate, options) {


  var variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'clauses' + createUUID(),
    label: 'Clause',
    modelProperties: 'clause',
    idGeneration: false,

    createExtensionElement: function (element, extensionElements, _value) {
      let sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      console.log(sequenceFlow);
      if (!sequenceFlow || !sequenceFlow.values || sequenceFlow.values.length == 0) {
        return;
      }
      let expression = sequenceFlow.values[0];
      var clause = SequenceFlowHelper.createClause(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      if (!clause) {
        return [];
      }
      return cmdHelper.addElementsTolist(element, expression, 'values', clause);
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      let sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      if (!sequenceFlow || !sequenceFlow.values || sequenceFlow.values.length == 0) {
        return;
      }
      let expression = sequenceFlow.values[0];
      var selectedClause = expression.values[idx];
      if (!expression || !selectedClause) {
        return {};
      }
      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedClause.id });
      return cmdHelper.removeElementsFromList(element, expression, 'values',
        null, [selectedClause]);
    },

    getExtensionElements: function (_element) {
      let sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      if (!sequenceFlow || !sequenceFlow.values || sequenceFlow.values.length == 0) {
        return;
      }
      let expression = sequenceFlow.values[0];
      return expression.values || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      let sequenceFlow = SequenceFlowHelper.getExpressionBySequenceFlowId(bpmnFactory, elementRegistry, options.outgoingElementId, true);
      if (!sequenceFlow || !sequenceFlow.values || sequenceFlow.values.length == 0) {
        return;
      }
      let expression = sequenceFlow.values[0];
      var selectedClause = expression.values[idx];
      option.text = selectedClause && selectedClause.variableName;
    }
  });

  return {
    entries: variableEntry
  };
};