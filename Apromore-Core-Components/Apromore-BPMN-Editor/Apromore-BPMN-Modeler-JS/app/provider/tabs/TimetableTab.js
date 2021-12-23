var is = require('bpmn-js/lib/util/ModelUtil').is,
    createTimetableGroups = require('../groups/TimetableGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  function shown(element) {
    return is(element, 'bpmn:Process') || is(element, 'bpmn:SubProcess') ||
      is(element, 'bpmn:Participant') || is(element, 'bpmn:Collaboration');
  }

  return {
    id: 'timetableTab',
    label: translate('timetableTab.label'),
    groups: createTimetableGroups(element, bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return shown(element);
    }
  };
};