var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    getFlowElementsByType = require('../../helper/FlowElementsHelper'),
    intermediateAndBoundaryEventEntries = require('../entries/IntermediateAndBoundary/IntermediateAndBoundaryEntries');


module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var intermediateEvents = getFlowElementsByType(element, 'bpmn:IntermediateCatchEvent'),
      boundaryEvents = getFlowElementsByType(element, 'bpmn:BoundaryEvent');

  var intermediateAndBoundaryEvents = intermediateEvents.concat(boundaryEvents);

  intermediateAndBoundaryEvents = intermediateAndBoundaryEvents.filter(function(el) {
    return !el.toBeRemoved;
  });

  function eventToGroup(el) {
    var event = getBusinessObject(el),
        groupId = ['event', event.get('id'), 'group'].join('-'),
        groupLabel = event.name || event.id;

    return {
      id: groupId,
      label: function(_element, _node) {
        return groupLabel;
      },
      entries: intermediateAndBoundaryEventEntries(bpmnFactory, elementRegistry, translate, { event: event })
    };
  }

  return intermediateAndBoundaryEvents.map(function(el) {
    return eventToGroup(el);
  });
};