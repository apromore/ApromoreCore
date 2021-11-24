var distributionEntries = require('../../DistributionEntries'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper'),
    DistributionHelper = require('../../../../helper/DistributionHelper');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  var processDistribution = DistributionHelper.getArrivalRateDistribution(bpmnFactory, elementRegistry);

  return distributionEntries(bpmnFactory, elementRegistry, translate, {
    id: processSimulationInfo.id,
    elementName: translate('scenarioGroup.distribution.label'),
    distribution: processDistribution,
    label: translate('scenarioGroup.distribution.label')
  });
};