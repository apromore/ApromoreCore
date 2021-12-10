var is = require('bpmn-js/lib/util/ModelUtil').is,
    createTaskGroups = require('../groups/TaskGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  function shown(element) {
    return is(element, 'bpmn:Task') || is(element, 'bpmn:Process') || is(element, 'bpmn:SubProcess') ||
      is(element, 'bpmn:Participant') || is(element, 'bpmn:Collaboration');
  }

  return {
    id: 'taskTab',
    label: translate('taskTab.label'),
    groups: createTaskGroups(element, bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return shown(element);
    }
  };
};