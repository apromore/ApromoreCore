var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedCategory = options.getSelectedCategory;

  var label = 'Probability';

  return entryFactory.textField(translate, {
    id: 'category-probability',
    label: label,
    modelProperty: 'assignmentProbability',

    get: function(element, node) {

      var selectedCategory = getSelectedCategory(element, node);

      return { assignmentProbability: selectedCategory && selectedCategory.assignmentProbability };
    },

    set: function(element, values, node) {

      var selectedCategory = getSelectedCategory(element, node);

      return cmdHelper.updateBusinessObject(element, selectedCategory, {
        assignmentProbability: values.assignmentProbability
      });
    },

    validate: function(element, values, node) {

      var selectedCategory = getSelectedCategory(element, node);

      if (selectedCategory) {
        var validationId = selectedCategory.id + this.id;

        var error = validationErrorHelper.validateVariableName(bpmnFactory, elementRegistry, translate, {
          id : validationId,
          label: label,
          name: values.name,
          resource: selectedCategory,
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { name: error.message };
      }
    }
  });
};