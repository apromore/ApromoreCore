var TaskResourceSelectBox = require('./fields/TaskResourceSelectBox'),
    DistributionFields = require('./fields/DistributionFields');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.task.id;

  var entries = [];

  entries.push(TaskResourceSelectBox(bpmnFactory, elementRegistry, translate, { taskId: id }));

  entries = entries.concat(DistributionFields(bpmnFactory, elementRegistry, translate, { taskId: id }));

  return entries;
};
