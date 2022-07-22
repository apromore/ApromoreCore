var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError,
  createValidationError = require('../../../../helper/ValidationErrorHelper').createValidationError;
var  validationHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function (element, bpmnFactory, elementRegistry, translate) {

  var entries = [];

  var variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'variables',
    label: translate('general.categorical.case.attribute.label'),
    modelProperties: 'name',
    idGeneration: false,

    createExtensionElement: function (element, extensionElements, _value) {
      let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      let variable = CaseAttributeHelper.createVariable(bpmnFactory, elementRegistry);
      return cmdHelper.addElementsTolist(element, variables, 'values', [variable]);
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      let selectedVariable = variables.values[idx];

      if (!variables || !selectedVariable) {
        return {};
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: 'Case Attributes' });

      return cmdHelper.removeElementsFromList(element, variables, 'values',
        null, [selectedVariable]);
    },

    getExtensionElements: function (_element) {
      var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];
      return variables;
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      let selectedVariable = variables.values[idx];
      option.text = selectedVariable && selectedVariable.name;
    }

  });

  function getSelectedVariable(element, node) {
    let selection = (variableEntry && variableEntry.getSelected(element, node)) || {
      idx: -1
    };

    let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];
    return variables[selection.idx];
  }
  function doValidation() {
    let errorString = '';
    let validationId = 'Case Attributes';
    let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];
    validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
    
     
    let errorReturn = validationHelper.validateWithAllVariables(
      bpmnFactory,
      elementRegistry,
      translate,
      Object.assign({
        id: validationId,
        label: validationId,
        variables: variables || []
      })
    );

    if (errorReturn && errorReturn.message) {
      errorString += errorReturn.message;
    }

    return errorString;
  }

  entries.push(variableEntry);

  return {
    entries: entries,
    getSelectedVariable: getSelectedVariable,
    doValidation: doValidation
  };
}