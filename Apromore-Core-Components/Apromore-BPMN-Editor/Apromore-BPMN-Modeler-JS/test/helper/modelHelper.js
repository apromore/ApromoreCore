var ProcessSimulationHelper = require('../../app/helper/ProcessSimulationHelper'),
    TimetableHelper = require('../../app/helper/TimetableHelper'),
    ResourceHelper = require('../../app/helper/ResourceHelper'),
    ElementHelper = require('../../app/helper/ElementHelper'),
    SequenceFlowHelper = require('../../app/helper/SequenceFlowHelper');

var getInterArrivalTimeDistribution = function(bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  return processSimulationInfo && processSimulationInfo.arrivalRateDistribution;
};

var getStatsOptions = function(bpmnFactory, elementRegistry) {
  return ProcessSimulationHelper.getStatsOptions(bpmnFactory, elementRegistry);
};

var getTimetables = function(bpmnFactory, elementRegistry) {
  var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry);

  return timetables && timetables.values || [];
};

var getTimetableByName = function(bpmnFactory, elementRegistry, name) {
  var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry);

  return (timetables.values.filter(function(timetable) {
    return timetable.name === name;
  }) || [])[0];
};

var getResources = function(bpmnFactory, elementRegistry) {
  var resources = ResourceHelper.getResources(bpmnFactory, elementRegistry);

  return resources && resources.get('values') || [];
};

var getFirstResourceByName = function(bpmnFactory, elementRegistry, name) {
  var resources = getResources(bpmnFactory, elementRegistry);

  return (resources && resources.filter(function(resource) {
    return resource.name === name;
  }) || [])[0];
};

var getFirstTaskById = function(bpmnFactory, elementRegistry, id) {
  var tasks = ElementHelper.getElements(bpmnFactory, elementRegistry);

  return (tasks && tasks.values.filter(function(el) {
    return el.elementId === id;
  }) || [])[0];
};

var getSequenceFlowById = function(bpmnFactory, elementRegistry, id) {
  var sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);
  return (sequenceFlows && sequenceFlows.values.filter(function(el) {
    return el.elementId === id;
  }) || [])[0];
};

module.exports = {
  getInterArrivalTimeDistribution: getInterArrivalTimeDistribution,
  getStatsOptions: getStatsOptions,
  getTimetableByName: getTimetableByName,
  getTimetables: getTimetables,
  getResources: getResources,
  getFirstResourceByName: getFirstResourceByName,
  getFirstTaskById: getFirstTaskById,
  getSequenceFlowById: getSequenceFlowById
};