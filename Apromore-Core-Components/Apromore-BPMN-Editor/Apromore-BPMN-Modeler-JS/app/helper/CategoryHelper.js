var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
  ProcessSimulationHelper = require('./ProcessSimulationHelper'),
  createUUID = require('../utils/Utils').createUUID;

var CategoryHelper = {};

CategoryHelper.getCategories = function (bpmnFactory, elementRegistry, options) {
  let enumCategory = [];
  let selectedVariable = options.selectedVariable;
  if (!selectedVariable) {
    return enumCategory;
  }
  let processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  let variables = processSimulationInfo.variables;
  let matchedVariable = variables && variables.values && variables.values.filter(v => v.name === selectedVariable.name);

  if (!matchedVariable || matchedVariable.length == 0 || !(matchedVariable[0].values)) {
    return enumCategory;
  }

  return matchedVariable[0].values;
};

CategoryHelper.createCategory = function (bpmnFactory, elementRegistry, options) {
  
  let categories = CategoryHelper.getCategories(bpmnFactory, elementRegistry, options);
  let index = 1;
  let prefix = 'Category ';
  if (categories && categories.length) {
    let found;
    do {
      found = false;
      for (let i = 1; i <= categories.length; i++) {
        if (categories[i-1].name == (prefix +  index)) {
          found = true;
          index++;
          break;
        }
      }
    } while (found)
  }
  return elementHelper.createElement('qbp:Enum', {
    name: prefix+ index ,
    assignmentProbability: '0',
    rawProbability: ''
  }, null, bpmnFactory);
};

CategoryHelper.addCategoryToCategories = function (enums, enumCategory) {
  enums.values.push(enumCategory);
};

module.exports = CategoryHelper;