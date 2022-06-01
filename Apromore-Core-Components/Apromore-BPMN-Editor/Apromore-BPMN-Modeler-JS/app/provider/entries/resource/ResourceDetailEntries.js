var ResourceTimetableSelectBox = require('./fields/ResourceTimetable'),
    ResourceNameField = require('./fields/ResourceName'),
    ResourceAmountField = require('./fields/ResourceAmount'),
    ResourceCostField = require('./fields/ResourceCost');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {
  var entries = [];
  var getSelectedResource = options.getSelectedResource;

  entries.push(ResourceNameField(bpmnFactory, elementRegistry, translate,
    { getSelectedResource: getSelectedResource }));

  entries.push(ResourceTimetableSelectBox(bpmnFactory, elementRegistry, translate,
    { getSelectedResource: getSelectedResource }));

  entries.push(ResourceAmountField(bpmnFactory, elementRegistry, translate,
    { getSelectedResource: getSelectedResource }));

  entries.push(ResourceCostField(bpmnFactory, elementRegistry, translate,
    { getSelectedResource: getSelectedResource }));

  return entries;
};