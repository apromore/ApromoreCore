var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedVariable = options.getSelectedVariable;

  var label = 'Case attribute name';

  return entryFactory.textField(translate, {
    id: 'variable-name',
    label: label,
    modelProperty: 'name',
    hidden : function(element, node){
      var selectedVariable = getSelectedVariable(element, node);
      return !selectedVariable ;
    },
    get: function(element, node) {

      var selectedVariable = getSelectedVariable(element, node);

      return { name: selectedVariable && selectedVariable.name };
    },

    set: function(element, values, node) {

      var selectedVariable = getSelectedVariable(element, node);

      return cmdHelper.updateBusinessObject(element, selectedVariable, {
        name: values.name
      });
    },

    validate: function(element, values, node) {
      var selectedVariable = getSelectedVariable(element, node);
      if (selectedVariable) {
        var validationId = selectedVariable.id + this.id;
        var error = validationErrorHelper.validateVariableName(bpmnFactory, elementRegistry, translate, {
          id : validationId,
          label: label,
          name: values.name,
          resource: selectedVariable,
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { name: error.message };
      }
    }
  });
};