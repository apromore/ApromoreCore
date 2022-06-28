var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError;

  module.exports = function (element, bpmnFactory, elementRegistry, translate) {

  var entries = [];

  var variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'variables',
    label: 'Categorical case attributes',
    modelProperties: 'name',
    idGeneration: false,

    createExtensionElement: function (element, extensionElements, _value) {
      var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      var variable = CaseAttributeHelper.createVariable(bpmnFactory, translate);
      return cmdHelper.addElementsTolist(element, variables, 'values', [variable]);
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      var selectedVariable = variables.values[idx];

      if (!variables || !selectedVariable) {
        return {};
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedVariable.id });

      return cmdHelper.removeElementsFromList(element, variables, 'values',
        null, [selectedVariable]);
    },

    getExtensionElements: function (_element) {
      return CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      var selectedVariable = variables.values[idx];

      option.text = selectedVariable && selectedVariable.name ;
    }
  });

  function getSelectedVariable(element, node) {
    var selection = (variableEntry && variableEntry.getSelected(element, node)) || {
      idx: -1
    };

    var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];
    return variables[selection.idx];
  }

  entries.push(variableEntry);

  return {
    entries: entries,
    getSelectedVariable: getSelectedVariable
  };
};