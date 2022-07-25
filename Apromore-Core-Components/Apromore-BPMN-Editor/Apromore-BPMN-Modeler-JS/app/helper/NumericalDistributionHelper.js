var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper');
var NumericalDistributionHelper = {};
var currentCaseAttribute = {};

NumericalDistributionHelper.getNumericAttribute = function (bpmnFactory, elementRegistry, options) {
  let selectedVariable = options.selectedVariable;
  if (!selectedVariable) {
    return [];
  }
  let processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  let variables = processSimulationInfo.variables;
  let matchedVariable = variables && variables.values && variables.values.filter(v => v.name === selectedVariable.name);

  if (!matchedVariable || matchedVariable.length == 0 || !(matchedVariable[0].values)) {
    return [];
  }

  return matchedVariable[0].values;
};

NumericalDistributionHelper.createNumericalAttribute = function (bpmnFactory, elementRegistry, options) {

  let selectedVariable = options.selectedVariable;
  if (!selectedVariable || (selectedVariable.values && selectedVariable.values.length == 1 )) {
    return;
  }

  return elementHelper.createElement('qbp:Numeric', {
    type: 'FIXED',
    mean: '0',
    arg1: '0',
    arg2: '0'
  }, selectedVariable, bpmnFactory);
};

NumericalDistributionHelper.addNumericAttributeToList = function (variables, attribute) {
  variables.values.push(attribute);
};

NumericalDistributionHelper.storeCurrentCaseAttribute = function (caseAttribute) {
  currentCaseAttribute.selected = caseAttribute;
}

NumericalDistributionHelper.getCurrentCaseAttribute = function () {
  return (currentCaseAttribute && currentCaseAttribute.selected) || undefined;
}

module.exports = NumericalDistributionHelper;