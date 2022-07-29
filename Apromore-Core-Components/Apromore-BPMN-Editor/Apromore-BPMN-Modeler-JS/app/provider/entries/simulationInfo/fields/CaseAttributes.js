var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError,
  createValidationError = require('../../../../helper/ValidationErrorHelper').createValidationError;
var validationHelper = require('../../../../helper/ValidationErrorHelper');
var NumericalDistributionHelper = require('../../../../helper/NumericalDistributionHelper');
var SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');

module.exports = function (element, bpmnFactory, elementRegistry, translate) {

  var entries = [];

  let currentSelectedCaseAttribute;

  var variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'variables',
    label: translate('general.categorical.case.attribute.label'),
    modelProperties: 'name',
    idGeneration: false,

    createExtensionElement: function (element, extensionElements, _value) {
      let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      let variable = CaseAttributeHelper.createVariable(bpmnFactory, elementRegistry);
      var cmd = cmdHelper.addElementsTolist(element, variables, 'values', [variable]);
      currentSelectedCaseAttribute = variable;
      NumericalDistributionHelper.storeCurrentCaseAttribute(currentSelectedCaseAttribute);
      doCaseValidation();
      return cmd;
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      let selectedVariable = variables.values[idx];

      if (!variables || !selectedVariable) {
        return {};
      }

      if(checkCaseAttributeNameAlreadyExist(selectedVariable)){
        Ap.common.notify(translate('general.attribute.used.in.gateway'), 'error');
        return cmdHelper.removeElementsFromList(element, variables, 'values', null, []);
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedVariable.id });
      currentSelectedCaseAttribute = undefined;
      NumericalDistributionHelper.storeCurrentCaseAttribute(currentSelectedCaseAttribute);

      return cmdHelper.removeElementsFromList(element, variables, 'values',
        null, [selectedVariable]);
    },

    getExtensionElements: function (_element) {
      var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];
      doCaseValidation();
      return variables;
    },

    onSelectionChange: function (element, node, event, scope) {
      currentSelectedCaseAttribute = getSelectedVariable(element, node);
      NumericalDistributionHelper.storeCurrentCaseAttribute(currentSelectedCaseAttribute);
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
      let selectedVariable = variables.values[idx];
      let type = '';
      if (selectedVariable && selectedVariable.type == 'ENUM') {
        type = 'C';
      } else if (selectedVariable && selectedVariable.type == 'NUMERIC') {
        type = 'N';
      }
      option.text = type + ' - ' + (selectedVariable && selectedVariable.name);
    }

  });

  function getSelectedVariable(element, node) {
    let selection = (variableEntry && variableEntry.getSelected(element, node)) || {
      idx: -1
    };
    let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];
    let selectedVariable = variables[selection.idx];
    if (!selectedVariable) {
      selectedVariable = NumericalDistributionHelper.getCurrentCaseAttribute();
    }
    return selectedVariable;
  }

  function getCurrentSelectionCaseAttribute() {
    return currentSelectedCaseAttribute;
  }

  function doCaseValidation() {

    let errorString = '';
    let validationId = 'Case Attributes';
    let variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry).values || [];

    validationHelper.suppressValidationErrorForNonExistentItem(bpmnFactory, elementRegistry, { variables: variables });
    validationHelper.suppressValidationErrorWithOnlyId(bpmnFactory, elementRegistry, { id: validationId });

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

  function doValidation() {
    let validationId = 'Case Attributes';
    let errorString = validationHelper.getValidationErrorDetails(
      bpmnFactory,
      elementRegistry,
      Object.assign({
        id: validationId,
        elementId: (currentSelectedCaseAttribute && currentSelectedCaseAttribute.name) || '',
      })
    );
    return errorString;
  }

  function checkCaseAttributeNameAlreadyExist(selectedVariable) {
    let sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);
    let found = false;
    !found && sequenceFlows && sequenceFlows.values && sequenceFlows.values.forEach(sequenceFlow => {
      if (sequenceFlow && sequenceFlow.values && sequenceFlow.values[0]) {
        let expression = sequenceFlow.values[0];
        if (expression && expression.values) {
          !found && expression.values.forEach(clause => {
            if (clause && selectedVariable.name == clause.variableName) {
              found =  true;
            }
          });
        }
      }
    });
    return found;
  }

  entries.push(variableEntry);

  return {
    entries: entries,
    getSelectedVariable: getSelectedVariable,
    doValidation: doValidation,
    getCurrentSelectionCaseAttribute: getCurrentSelectionCaseAttribute
  };
}