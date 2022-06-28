var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
  ProcessSimulationHelper = require('./ProcessSimulationHelper'),
  createUUID = require('../utils/Utils').createUUID;

var CategoryHelper = {};

CategoryHelper.getCategories = function (bpmnFactory, elementRegistry, options) {
  let categories = [];
  let selectedVariable = options.selectedVariable;
  console.log(selectedVariable);
  if (!selectedVariable) {
    return categories;
  }
  let processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  let variables = processSimulationInfo.variables;
  let matchedVariable = variables && variables.filter(v => v.id === selectedVariable.id);

  if (!matchedVariable) {
    return categories;
  }

  if (!matchedVariable.categories) {
    categories = elementHelper.createElement('qbp:Categories',
      { values: [] }, matchedVariable, bpmnFactory);

    matchedVariable.categories = categories;
  }
  return categories;
};

CategoryHelper.createCategory = function (bpmnFactory, translate, options) {
  return elementHelper.createElement('qbp:Category', {
    id: 'qbp_' + createUUID(),
    name: translate('resource'),
    type: 'ENUM'
  }, null, bpmnFactory);
};

CategoryHelper.addCategoryToCategories = function (categories, category) {
  categories.values.push(category);
};

module.exports = CategoryHelper;