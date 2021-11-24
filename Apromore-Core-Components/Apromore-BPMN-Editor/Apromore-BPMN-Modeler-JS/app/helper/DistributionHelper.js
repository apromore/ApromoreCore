var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    ProcessSimulationHelper = require('./ProcessSimulationHelper');

var DistributionHelper = {};

DistributionHelper.getDurationDistribution = function(bpmnFactory, element) {

  var durationDistribution = element.durationDistribution;

  if (!durationDistribution) {
    durationDistribution = elementHelper.createElement('qbp:DurationDistribution', {
      type: 'FIXED',
      mean: 'NaN',
      arg1: 'NaN',
      arg2: 'NaN',
      rawMean: 'NaN',
      rawArg1: 'NaN',
      rawArg2: 'NaN',
      timeUnit: 'seconds'
    }, element, bpmnFactory);

    element.durationDistribution = durationDistribution;
  }

  return durationDistribution;
};

DistributionHelper.getArrivalRateDistribution = function(bpmnFactory, elementRegistry) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var arrivalRateDistribution = processSimulationInfo.arrivalRateDistribution;

  if (!arrivalRateDistribution) {
    arrivalRateDistribution = elementHelper.createElement('qbp:ArrivalRateDistribution', {
      type: 'FIXED',
      mean: 'NaN',
      arg1: 'NaN',
      arg2: 'NaN',
      rawMean: 'NaN',
      rawArg1: 'NaN',
      rawArg2: 'NaN',
      timeUnit: 'seconds'
    }, processSimulationInfo, bpmnFactory);

    processSimulationInfo.arrivalRateDistribution = arrivalRateDistribution;
  }

  return arrivalRateDistribution;
};

module.exports = DistributionHelper;