var ProcessInstancesField = require('./fields/ProcessInstancesField'),
    StartTimeField = require('./fields/StartTimeField'),
    StartDateField = require('./fields/StartDateField'),
    TrimStartField = require('./fields/TrimStartField'),
    TrimEndField = require('./fields/TrimEndField'),
    DistributionFields = require('./fields/DistributionFields'),
    CurrencySelectBox = require('./fields/CurrencySelectBox');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var entries = [];

  entries = entries.concat(DistributionFields(bpmnFactory, elementRegistry, translate));

  entries.push(
    ProcessInstancesField(bpmnFactory, elementRegistry, translate),
    StartTimeField(bpmnFactory, elementRegistry, translate),
    StartDateField(bpmnFactory, elementRegistry, translate),
    TrimStartField(bpmnFactory, elementRegistry, translate),
    TrimEndField(bpmnFactory, elementRegistry, translate),
    CurrencySelectBox(bpmnFactory, elementRegistry, translate)
  );

  return entries;
};