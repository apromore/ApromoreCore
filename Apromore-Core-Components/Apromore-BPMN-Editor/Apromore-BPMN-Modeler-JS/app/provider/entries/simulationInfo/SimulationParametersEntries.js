var ProcessInstancesField = require('./fields/ProcessInstancesField'),
    StartDateField = require('./fields/StartDateField'),
    StartTimeField = require('./fields/StartTimeField'),
    TimeTableField= require('./fields/TimeTableField'),
    TrimStartField = require('./fields/TrimStartField'),
    TrimEndField = require('./fields/TrimEndField'),
    DistributionFields = require('./fields/DistributionFields'),
    CurrencySelectBox = require('./fields/CurrencySelectBox'),
    SimulationVariables = require('./fields/SimulationVariables');

module.exports = function(element, bpmnFactory, elementRegistry, translate, config) {
  var entries = [];
 

  entries = entries.concat(DistributionFields(bpmnFactory, elementRegistry, translate));

  entries.push(ProcessInstancesField(bpmnFactory, elementRegistry, translate));
  entries = entries.concat(SimulationVariables(element,bpmnFactory, elementRegistry, translate));

  entries.push(
    StartDateField(bpmnFactory, elementRegistry, translate, config),
    StartTimeField(bpmnFactory, elementRegistry, translate, config),
    TimeTableField(bpmnFactory, elementRegistry, translate),
    TrimStartField(bpmnFactory, elementRegistry, translate),
    TrimEndField(bpmnFactory, elementRegistry, translate),
    CurrencySelectBox(bpmnFactory, elementRegistry, translate),
   
  );

  return entries;
};