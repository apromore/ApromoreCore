var ProcessInstancesField = require('./fields/ProcessInstancesField'),
    StartDateField = require('./fields/StartDateField'),
    StartTimeField = require('./fields/StartTimeField'),
    TimeTableField= require('./fields/TimeTableField'),
    TrimStartField = require('./fields/TrimStartField'),
    TrimEndField = require('./fields/TrimEndField'),
    DistributionFields = require('./fields/DistributionFields'),
    CurrencySelectBox = require('./fields/CurrencySelectBox');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var entries = [];

  entries = entries.concat(DistributionFields(bpmnFactory, elementRegistry, translate));

  entries.push(
    ProcessInstancesField(bpmnFactory, elementRegistry, translate),
    StartDateField(bpmnFactory, elementRegistry, translate),
    StartTimeField(bpmnFactory, elementRegistry, translate),
    TimeTableField(bpmnFactory, elementRegistry, translate),
    TrimStartField(bpmnFactory, elementRegistry, translate),
    TrimEndField(bpmnFactory, elementRegistry, translate),
    CurrencySelectBox(bpmnFactory, elementRegistry, translate)
  );

  return entries;
};