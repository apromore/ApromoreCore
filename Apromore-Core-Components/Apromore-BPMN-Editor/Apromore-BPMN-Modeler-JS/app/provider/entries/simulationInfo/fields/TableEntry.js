var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var CategoryHelper = require('../../../../helper/CategoryHelper');
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var validationHelper = require('../../../../helper/ValidationErrorHelper');
var CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper');

module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  let getSelectedVariable = options.getSelectedVariable;
  let allCategories;
  let currentSelectedCategory;
  var tableEntry = entryFactory.table(translate, {
    id: 'categoriesTable',
    labels: [translate('general.caseAttribute.category.name'), translate('general.caseAttribute.category.probality')],
    addLabel: translate('general.categories'),
    modelProperties: ['name', 'assignmentProbability'],
    canBeShown: function () {
      return true;
    },
    show: function (element, entryNode, node, scopeNode) {
      var selectedVariable = getSelectedVariable(element, node);
      return selectedVariable;
    },
    canRemove: function () {
      return true;
    },
    editable: function (element, rowNode, input, prop, value, idx) {
      return true;
    },
    updateElement: function (element, value, node, idx) {
      let selectedCategory = getSelectedCategory(element, node, idx);

      if (!selectedCategory) {
        selectedCategory = {};
      }
      selectedCategory.name = value.name;
      selectedCategory.assignmentProbability = value.assignmentProbability;
      currentSelectedCategory = selectedCategory;
      return cmdHelper.updateBusinessObject(element, selectedCategory);
    },
    get: function (element, node) {
      var selectedVariable = getSelectedVariable(element, node);
      if (!selectedVariable) {
        return [];
      }

      var categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
      return categories;
    },
    removeElement: function (element, node, idx) {
      let selectedCategory = getSelectedCategory(element, node, idx);

      if (!selectedCategory) {
        return {};
      }
      var selectedVariable = getSelectedVariable(element, node);
      currentSelectedCategory = undefined;

      return cmdHelper.removeElementsFromList(element, selectedVariable, 'values',
        null, [selectedCategory]);
    },
    addElement: function (element, node, event, scopeNode) {
      var selectedVariable = getSelectedVariable(element, node);
      if (!selectedVariable) {
        return false;
      }
      var category = CategoryHelper.createCategory(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
      if (!selectedVariable.values) {
        selectedVariable.values = [];
      }
      return cmdHelper.addElementsTolist(element, selectedVariable, 'values', [category]);

    },
    getElements: function (element, node) {
      var selectedVariable = getSelectedVariable(element, node);
      if (!selectedVariable) {
        return [];
      }
      var categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
      allCategories = categories || [];
      return allCategories;

    },

    setControlValue: function (element, entryNode, input, prop, value, idx) {
      if (input) {
        input.value = value;
      }
      return true;
    }
  }


  );

  function doValidation(element, node) {
    let validationIdGroup = 'Case Attribute';
    validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationIdGroup });

    var selectedVariable = getSelectedVariable(element, node);
    if (!selectedVariable) {
      return;
    }
    var updatedCategories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
    if (!updatedCategories || !updatedCategories.length) {
      return;
    }

    let validationId = 'Category';
    //reset first
    validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });

    let errorString = '';
    updatedCategories.forEach(category => {
      let errorReturn = validationHelper.validateCategory(
        bpmnFactory,
        elementRegistry,
        translate,
        Object.assign({
          id: validationId,
          resource: { name: category.name, assignmentProbability: category.assignmentProbability },
        })
      );
      if (errorReturn && errorReturn.message) {
        errorString += errorReturn.message + ', ';
      }
    });

    if (!errorString) {
      validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationIdGroup });
      let errorReturn = validationHelper.validateWithAllCategory(
        bpmnFactory,
        elementRegistry,
        translate,
        Object.assign({
          id: validationIdGroup,
          label: 'Case Attribute',
          allCategories: updatedCategories || []
        })
      );

      if (errorReturn && errorReturn.message) {
        errorString += errorReturn.message;
      }
    }
    return errorString;
  }


  function getSelectedCategory(element, node, idx) {
    let selectedVariable = getSelectedVariable(element, node);
    if (!selectedVariable) {
      return;
    }
    let categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
    return categories && categories.length > 0 && categories[idx];
  }

  return {
    tableEntry: tableEntry,
    doValidation: doValidation
  }
}