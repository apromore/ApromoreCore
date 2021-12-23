var ElementHelper = require('../../../helper/ElementHelper'),
    DistributionHelper = require('../../../helper/DistributionHelper'),
    distributionEntries = require('../DistributionEntries');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var entries = [],
      id = options.event.id;

  var eventElement = ElementHelper.getElementById(bpmnFactory, elementRegistry, id);

  eventElement.durationDistribution = DistributionHelper.getDurationDistribution(bpmnFactory, eventElement);

  entries = entries.concat(distributionEntries(bpmnFactory, elementRegistry, translate, {
    id: eventElement.elementId,
    label: translate('task.distribution'),
    distribution: eventElement.durationDistribution,
    elementId: id
  }));

  return entries;
};