var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  validationErrorHelper = require('../../../../helper/ValidationErrorHelper'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper');

module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  var getSelectedVariable = options.getSelectedVariable;


  return entryFactory.textField(translate, {
    id: 'variable-name',
    label: translate('general.categorical.case.attribute.entry'),
    modelProperty: 'name',
    hidden: function (element, node) {
      var selectedVariable = getSelectedVariable(element, node);
      return !selectedVariable;
    },
    get: function (element, node) {

      var selectedVariable = getSelectedVariable(element, node);

      return { name: selectedVariable && selectedVariable.name };
    },

    set: function (element, values, node) {

      let selectedVariable = getSelectedVariable(element, node);

      let variables = CaseAttributeHelper.getAllVariables(bpmnFactory, elementRegistry);

      let errorMessage;
      if (values.name && variables && variables.length > 0) {
        variables.forEach(function (variable) {
          if (variable.name.trim() === values.name.trim()) {
            errorMessage = 'Duplicate';
          }

        });
      }
      let newValue = values.name;
      if (errorMessage) {
        newValue = selectedVariable.name;
      }

      return cmdHelper.updateBusinessObject(element, selectedVariable, {
        name: newValue
      });
    },

    validate: function (element, values, node) {
      var selectedVariable = getSelectedVariable(element, node);
      if (selectedVariable) {
        var validationId = 'Case Attribute';
        var error = validationErrorHelper.validateVariableName(bpmnFactory, elementRegistry, translate, {
          id: validationId,
          label: translate('general.categorical.case.attribute.entry'),
          name: values.name,
          elementId: selectedVariable.name ,
          resource: selectedVariable,
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationErrorForCaseAttribute(bpmnFactory, elementRegistry, { id: validationId,elementId: selectedVariable.name });
        }

        return { name: error.message };
      }
    }
  });
};