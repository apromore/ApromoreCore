var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    extensionElementsHelper = require('bpmn-js-properties-panel/lib/helper/ExtensionElementsHelper'),
    createUUID = require('../utils/Utils').createUUID,
    getRoot = require('../utils/Utils').getRoot;

var ProcessSimulationHelper = {
  prevRoot: undefined
};

ProcessSimulationHelper.getProcessSimulationInfo = function(bpmnFactory, elementRegistry) {
  var root = getRoot(elementRegistry);

  if (this.prevRoot === undefined) {
    this.prevRoot = root;
  }

  if (this.prevRoot.id !== root.id) {
    root.extensionElements = this.prevRoot.extensionElements;
    this.prevRoot.extensionElements = undefined;
    this.prevRoot = root;
  }

  var extensionElements = root.extensionElements;

  if (!extensionElements) {
    extensionElements = elementHelper.createElement('bpmn:ExtensionElements',
      { values: [] }, root, bpmnFactory);

    root.extensionElements = extensionElements;
  }

  var processSimulationInfo = (extensionElementsHelper.getExtensionElements(root,
    'qbp:ProcessSimulationInfo') || [])[0];

  if (!processSimulationInfo) {
    processSimulationInfo = createProcessSimulationInfo(root, bpmnFactory);
    extensionElements.values.push(processSimulationInfo);
  }

  return processSimulationInfo;
};

ProcessSimulationHelper.getStatsOptions = function(bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var statsOptions = processSimulationInfo.statsOptions;

  if (!statsOptions) {
    statsOptions = elementHelper.createElement('qbp:StatsOptions',
      {}, processSimulationInfo, bpmnFactory);

    processSimulationInfo.statsOptions = statsOptions;
  }

  return statsOptions;
};


function createProcessSimulationInfo(root, bpmnFactory) {
  var todayDate = new Date();
  todayDate.setHours(9, 0, 0, 0);

  var processSimulationInfo = elementHelper.createElement('qbp:ProcessSimulationInfo',
    {
      processInstances: '',
      id: 'qbp_' + createUUID(),
      startDateTime: todayDate.toISOString(),
      currency: 'EUR',
    }, root, bpmnFactory);

  processSimulationInfo.timetables = elementHelper.createElement('qbp:Timetables',
    { values: [] }, processSimulationInfo, bpmnFactory);

  processSimulationInfo.resources = elementHelper.createElement('qbp:Resources',
    { values: [] }, processSimulationInfo, bpmnFactory);

  var defaultSequenceFlow = elementHelper.createElement('qbp:SequenceFlow',
    {}, null, bpmnFactory);

  processSimulationInfo.sequenceFlows = elementHelper.createElement('qbp:SequenceFlows',
    { values: [defaultSequenceFlow] }, processSimulationInfo, bpmnFactory);

  return processSimulationInfo;
}

module.exports = ProcessSimulationHelper;