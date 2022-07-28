var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var CategoryHelper = require('../../../../helper/CategoryHelper');
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var validationHelper = require('../../../../helper/ValidationErrorHelper');
var CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper');
var SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');
var isValidNumber = require('../../../../utils/Utils').isValidNumber;

module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  let getSelectedVariable = options.getSelectedVariable;
  let allCategories;
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
      return selectedVariable && selectedVariable.type == 'ENUM';
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

      if (isValidNumber(value.assignmentProbability)) {
        selectedCategory.assignmentProbability = '' + (value.assignmentProbability / 100);
      } else {
        selectedCategory.assignmentProbability = '0';
      }
      return cmdHelper.updateBusinessObject(element, selectedCategory);
    },

    removeElement: function (element, node, idx) {
      let selectedCategory = getSelectedCategory(element, node, idx);

      if (!selectedCategory) {
        return {};
      }
      var selectedVariable = getSelectedVariable(element, node);
      if (checkCategoryNameAlreadyExist(selectedVariable, selectedCategory)) {
        Ap.common.notify(translate('general.category.used.in.gateway'), 'error');
        return cmdHelper.removeElementsFromList(element, selectedVariable, 'values', null, []);
      }
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
      let modifiedCategory = [];
      allCategories.forEach(category => {
        modifiedCategory.push({ name: category.name, assignmentProbability: category.assignmentProbability ? (category.assignmentProbability * 100) + '' : '0' })
      });
      return modifiedCategory;

    }
  }
  );

  function doValidation(element, node) {
    let validationIdGroup = 'Case Attribute';

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
    validationHelper.suppressValidationErrorForCaseAttribute(bpmnFactory, elementRegistry, { id: validationId, elementId: selectedVariable.name });

    let errorString = '';
    updatedCategories.forEach(category => {
      let errorReturn = validationHelper.validateCategory(
        bpmnFactory,
        elementRegistry,
        translate,
        Object.assign({
          id: validationId,
          elementId: selectedVariable.name,
          resource: { name: category.name, assignmentProbability: category.assignmentProbability },
        })
      );
      if (errorReturn && errorReturn.message) {
        errorString = errorReturn.message;
      }
    });

    if (!errorString) {
      validationHelper.suppressValidationErrorForCaseAttribute(bpmnFactory, elementRegistry, { id: validationIdGroup, elementId: selectedVariable.name });
      let errorReturn = validationHelper.validateWithAllCategory(
        bpmnFactory,
        elementRegistry,
        translate,
        Object.assign({
          id: validationIdGroup,
          elementId: selectedVariable.name,
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

  function checkCategoryNameAlreadyExist(selectedVariable, selectedCategory) {
    let sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);
    let found = false;
    !found && sequenceFlows && sequenceFlows.values && sequenceFlows.values.forEach(sequenceFlow => {
      if (sequenceFlow && sequenceFlow.values && sequenceFlow.values[0]) {
        let expression = sequenceFlow.values[0];
        if (expression && expression.values) {
          !found && expression.values.forEach(clause => {
            if (clause && selectedVariable.name == clause.variableName) {
              if (clause.variableEnumValue && clause.variableEnumValue == selectedCategory.name) {
                found = true;
              }
            }
          });
        }
      }
    });
    return found;
  }

  return {
    tableEntry: tableEntry,
    doValidation: doValidation
  }
}