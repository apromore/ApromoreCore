var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  let getSelectedCategory = options.getSelectedCategory;
  let getAllCategories = options.getAllCategories;

  let label = 'Probability';

  return entryFactory.textField(translate, {
    id: 'category-probability',
    label: label,
    modelProperty: 'assignmentProbability',
    hidden : function(element, node) {
      return !getSelectedCategory(element, node);
    }
    ,
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
        let allCategories = getAllCategories(element, node);
        var error = validationErrorHelper.validateCategory(bpmnFactory, elementRegistry, translate, {
          id : validationId,
          label: label,
          name: values.name,
          resource: selectedCategory,
          allCategories: allCategories
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { name: error.message };
      }
    }
  });
};