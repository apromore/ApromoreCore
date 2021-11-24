var is = require('bpmn-js/lib/util/ModelUtil').is,
    createResourceGroups = require('../groups/ResourceGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  function shown(element) {
    return is(element, 'bpmn:Process') || is(element, 'bpmn:SubProcess') ||
      is(element, 'bpmn:Participant') || is(element, 'bpmn:Collaboration');
  }

  return {
    id: 'resourceTab',
    label: translate('resourceTab.label'),
    groups: createResourceGroups(element, bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return shown(element);
    }
  };
};