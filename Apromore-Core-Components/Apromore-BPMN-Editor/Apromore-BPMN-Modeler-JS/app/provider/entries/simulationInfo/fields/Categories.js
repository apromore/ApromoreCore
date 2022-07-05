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
          return;
      }
      var categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry,
        { selectedVariable: selectedVariable });
   
      var category = CategoryHelper.createCategory(bpmnFactory, translate, { selectedVariable: selectedVariable });

      return cmdHelper.addElementsTolist(element, selectedVariable, 'values', [category]);
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {

      var selectedVariable = getSelectedVariable(element,_extensionElements);
      if(!selectedVariable){
           return;
      }

      var categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
      var selectedCategory = !categories && categories.length > 0 && categories[idx];

      if (!selectedCategory) {
        return {};
      }

      suppressValidationError(bpmnFactory, elementRegistry, { name: selectedCategory.name });

      return cmdHelper.removeElementsFromList(element, selectedVariable, 'values',
        null, [selectedCategory]);
    },

    getExtensionElements: function (_element,node) {
      var selectedVariable = getSelectedVariable(_element,node);
      if(!selectedVariable){
           return [];
      }
      suppressValidationError(bpmnFactory, elementRegistry, { name: 'Test' });

      var categories=CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
      return categories || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      var selectedVariable = getSelectedVariable(element,_node);
      if(selectedVariable){
        var categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
        var selectedVariable = categories && categories.length > 0 && categories[idx];
        option.text = selectedVariable && selectedVariable.name ;
      } else{
        option.text = '';
      }
     
    }
  });

  function getSelectedCategory(element, node) {
    var selection = (variableEntry && variableEntry.getSelected(element, node)) || {
      idx: -1
    };

    var selectedVariable = getSelectedVariable(element,node);
    if(selectedVariable){
      var categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable });
      return categories[selection.idx];
    }
   
  }
  entries.push(variableEntry);

  return {
    entries: entries,
    getSelectedCategory: getSelectedCategory
  };
};