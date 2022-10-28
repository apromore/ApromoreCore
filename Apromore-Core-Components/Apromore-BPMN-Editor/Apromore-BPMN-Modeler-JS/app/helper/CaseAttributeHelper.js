var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
  ProcessSimulationHelper = require('./ProcessSimulationHelper'),
  createUUID = require('../utils/Utils').createUUID;

var CaseAttributeHelper = {};

CaseAttributeHelper.getVariables = function (bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var variables = processSimulationInfo.variables;

  if (!variables) {
    variables = elementHelper.createElement('qbp:Variables',
      { values: [] }, processSimulationInfo, bpmnFactory);

    processSimulationInfo.variables = variables;
  }

  return variables;
};

CaseAttributeHelper.createVariable = function (bpmnFactory, elementRegistry, translate) {
  let variables = CaseAttributeHelper.getAllVariables(bpmnFactory, elementRegistry);
  let index = 1;
  let prefix = translate('general.categorical.case.attribute.prefix');
  if (variables && variables.length) {
    let found;
    do {
      found = false;
      for (let i = 1; i <= variables.length; i++) {
        if (variables[i-1].name == (prefix +  index)) {
          found = true;
          index++;
          break;
        }
      }
    } while (found)
  }
  return elementHelper.createElement('qbp:Variable', {
    name: prefix + index,
    type: 'ENUM'
  }, null, bpmnFactory);
};

CaseAttributeHelper.addVariableToVariables = function (variable, variables) {
  variables.values.push(variable);
};



CaseAttributeHelper.getAllVariables = function (bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  var variables = processSimulationInfo.variables;

  if (!variables) {
    return [];
  }

  return variables.values;
};

module.exports = CaseAttributeHelper;