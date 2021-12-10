var is = require('bpmn-js/lib/util/ModelUtil').is,
    intermediateAndBoundaryEventGroups = require('../groups/IntermediateAndBoundaryEventGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {
  return {
    id: 'intermediateAndBoundaryEventsTab',
    label: translate('intermediateAndBoundaryEventsTab.label'),
    groups: intermediateAndBoundaryEventGroups(element, bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return is(element, 'bpmn:BoundaryEvent') || is(element, 'bpmn:IntermediateCatchEvent') ||
        is(element, 'bpmn:Process') || is(element, 'bpmn:SubProcess') ||
        is(element, 'bpmn:Collaboration') || is(element, 'bpmn:Participant');
    }
  };
};