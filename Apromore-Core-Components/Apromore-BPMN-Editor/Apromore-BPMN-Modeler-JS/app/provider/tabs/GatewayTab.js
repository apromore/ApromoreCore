var is = require('bpmn-js/lib/util/ModelUtil').is,
    createGatewayGroups = require('../groups/GatewayGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  function shown(element) {
    return is(element, 'bpmn:ExclusiveGateway') || is(element, 'bpmn:InclusiveGateway') ||
      is(element, 'bpmn:Process') || is(element, 'bpmn:SubProcess') ||
      is(element, 'bpmn:Participant') || is(element, 'bpmn:Collaboration');
  }

  return {
    id: 'gatewayTab',
    label: translate('gatewayTab.label'),
    groups: createGatewayGroups(element, bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return shown(element);
    }
  };
};