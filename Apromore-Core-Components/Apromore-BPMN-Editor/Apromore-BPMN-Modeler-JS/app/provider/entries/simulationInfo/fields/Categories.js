var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  CategoryHelper = require('../../../../helper/CategoryHelper'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError;

module.exports = function (element, bpmnFactory, elementRegistry, translate, options) {

  var entries = [];
  var getSelectedVariable = options.getSelectedVariable;


  var variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'categories',
    label: 'Categories',
    modelProperties: 'name',
    idGeneration: false,
    createExtensionElement: function (element, extensionElements, _value) {
      var selectedVariable = getSelectedVariable(element,extensionElements);
      if(!selectedVariable){
          console.log('No Variable has been selected for category');
          return;
      }
      var categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry,
        { selectedVariable: selectedVariable });
   
      var category = CategoryHelper.createCategory(bpmnFactory, translate, { selectedVariable: selectedVariable });

      return cmdHelper.addElementsTolist(element, categories, 'values', [category]);
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      var variables = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { getSelectedVariable: getSelectedVariable });
      var selectedVariable = variables.values[idx];

      if (!variables || !selectedVariable) {
        return {};
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedVariable.id });

      return cmdHelper.removeElementsFromList(element, variables, 'values',
        null, [selectedVariable]);
    },

    getExtensionElements: function (_element) {
      var selectedVariable = options.selectedVariable;
        if (!selectedVariable) {
          return [];
        }
      return CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable }).values || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      var variables = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { getSelectedVariable: getSelectedVariable });
      var selectedVariable = variables.values[idx];

      option.text = selectedVariable && selectedVariable.name || translate('N/A');
    }
  });

  function getSelectedCategory(element, node) {
    var selection = (variableEntry && variableEntry.getSelected(element, node)) || {
      idx: -1
    };

    var variables = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { getSelectedVariable: getSelectedVariable }).values || [];

    return variables[selection.idx];
  }
  entries.push(variableEntry);

  return {
    entries: entries,
    getSelectedCategory: getSelectedCategory
  };
};