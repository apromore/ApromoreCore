var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    ProcessSimulationHelper = require('./ProcessSimulationHelper'),
    createUUID = require('../utils/Utils').createUUID;

var CaseAttributeHelper = {};

CaseAttributeHelper.getVariables = function(bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var variables = processSimulationInfo.variables;

  if (!variables) {
    variables = elementHelper.createElement('qbp:Variables',
      { values: [] }, processSimulationInfo, bpmnFactory);

    processSimulationInfo.variables = variables;
  }

  return variables;
};

CaseAttributeHelper.createVariable = function(bpmnFactory, translate) {
  //translate('resource')
  return elementHelper.createElement('qbp:Variable', {
    id: 'qbp_var_' + createUUID(),
    name: 'Categorical case attribute',
    type: 'ENUM'
  }, null, bpmnFactory);
};

CaseAttributeHelper.addVariableToVariables = function(variable, variables) {
  variables.values.push(variable);
};

module.exports = CaseAttributeHelper;