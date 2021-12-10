var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    createUUID = require('../utils/Utils').createUUID;

var RuleHelper = {};

RuleHelper.createRules = function(bpmnFactory) {
  return elementHelper.createElement('qbp:Rules',
    { values: [] }, null, bpmnFactory);
};

RuleHelper.createRule = function(bpmnFactory) {
  return elementHelper.createElement('qbp:Rule', {
    id: createUUID(),
    name: 'Timeslot',
    fromTime: '09:00:00.000+00:00',
    fromWeekDay: 'MONDAY',
    toTime: '17:00:00.000+00:00',
    toWeekDay: 'FRIDAY'
  }, null, bpmnFactory);
};

RuleHelper.addRuleToRules = function(rule, rules) {
  rules.values.push(rule);
};

RuleHelper.getTimetableRules = function(timetable) {
  return timetable && timetable.rules.values || [];
};

RuleHelper.getTimetableRulesElement = function(timetable) {
  return timetable && timetable.rules;
};

module.exports = RuleHelper;