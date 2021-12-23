var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    ProcessSimulationHelper = require('./ProcessSimulationHelper'),
    RuleHelper = require('./RuleHelper'),
    createUUID = require('../utils/Utils').createUUID;

var TimetableHelper = {};

TimetableHelper.getTimetables = function(bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var timetables = processSimulationInfo.timetables;

  if (!timetables) {
    timetables = elementHelper.createElement('qbp:Timetables',
      { values : [] }, null, bpmnFactory);

    processSimulationInfo.timetables = timetables;
  }

  return timetables;
};

TimetableHelper.createTimetable = function(bpmnFactory, elementRegistry) {
  var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry);

  var timetable = elementHelper.createElement('qbp:Timetable', {
    name: 'Timetable',
    id: 'qbp_' + createUUID(),
  }, timetables, bpmnFactory);

  var rules = RuleHelper.createRules(bpmnFactory);
  var rule = RuleHelper.createRule(bpmnFactory);
  RuleHelper.addRuleToRules(rule, rules);

  timetable.rules = rules;

  return timetable;
};

TimetableHelper.createDefaultTimetable = function(bpmnFactory, elementRegistry, translate) {
  var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry);

  var defaultTimetable = (timetables.get('values').filter(function(t) {
    return t.default === 'true';
  }) || [])[0];

  if (!defaultTimetable) {
    defaultTimetable = elementHelper.createElement('qbp:Timetable',
      {
        id: 'DEFAULT_TIMETABLE',
        default: 'true',
        name: translate('arrivalTimetable.name'),
      }, timetables, bpmnFactory);

    var rules = RuleHelper.createRules(bpmnFactory);
    var rule = RuleHelper.createRule(bpmnFactory);
    RuleHelper.addRuleToRules(rule, rules);

    defaultTimetable.rules = rules;

    timetables.values.push(defaultTimetable);
  }
};

module.exports = TimetableHelper;