var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;

module.exports = function getFlowElementsByType(element, type) {
  var businessObject = getBusinessObject(element);

  var flowElements = {
    'bpmn:Process': getProcessElements,
    'bpmn:SubProcess': getProcessElements,
    'bpmn:Participant': getParticipantElements,
    'bpmn:Collaboration': getCollaborationElements,
    'default': function() {
      return [];
    }
  };

  flowElements[type] = getSelf;

  function getSelf() {
    return [element];
  }

  function getProcessElements() {
    var flowElements = businessObject && businessObject.flowElements || [],
        elements = [];

    flowElements.forEach(function(el) {
      var flowElementsByType = getFlowElementsByType(el, type);
      elements = elements.concat(flowElementsByType);
    });

    return elements;
  }

  function getParticipantElements() {
    var process = businessObject.processRef;
    return getFlowElementsByType(process, type);
  }

  function getCollaborationElements() {
    var participants = businessObject && businessObject.participants || [],
        elements = [];

    participants.forEach(function(el) {
      elements = elements.concat(getFlowElementsByType(el, type));
    });

    return elements;
  }

  return (businessObject && flowElements[businessObject.$type] || flowElements['default'])();
};