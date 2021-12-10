var distributionEntries = require('../../DistributionEntries'),
    ElementHelper = require('../../../../helper/ElementHelper'),
    DistributionHelper = require('../../../../helper/DistributionHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var taskId = options.taskId;

  var taskElement = ElementHelper.getElementById(bpmnFactory, elementRegistry, taskId);

  var taskDistribution = DistributionHelper.getDurationDistribution(bpmnFactory, taskElement);

  return distributionEntries(bpmnFactory, elementRegistry, translate, {
    id: taskElement.elementId,
    label: translate('task.distribution'),
    distribution: taskDistribution,
    elementId: taskId
  });
};