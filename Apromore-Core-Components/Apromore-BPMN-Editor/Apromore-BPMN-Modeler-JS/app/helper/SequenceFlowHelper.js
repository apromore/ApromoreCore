var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    ProcessSimulationHelper = require('./ProcessSimulationHelper');

var SequenceFlowHelper = {};

SequenceFlowHelper.getSequenceFlows = function(bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var sequenceFlows = processSimulationInfo.sequenceFlows;

  if (!sequenceFlows) {
    sequenceFlows = elementHelper.createElement('qbp:SequenceFlows',
      { values: [] }, processSimulationInfo, bpmnFactory
    );

    processSimulationInfo.sequenceFlows = sequenceFlows;
  }

  return sequenceFlows;
};

SequenceFlowHelper.getSequenceFlowById = function(bpmnFactory, elementRegistry, id) {
  var sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);

  var sequenceFlow = (sequenceFlows.get('values').filter(function(el) {
    return el.elementId === id;
  }) || [])[0];

  if (!sequenceFlow) {
    sequenceFlow = elementHelper.createElement(
      'qbp:SequenceFlow',
      {
        elementId: id,
        executionProbability: '',
        rawExecutionProbability: ''
      }, sequenceFlows, bpmnFactory
    );

    sequenceFlows.values.push(sequenceFlow);
  }

  return sequenceFlow;
};

module.exports = SequenceFlowHelper;