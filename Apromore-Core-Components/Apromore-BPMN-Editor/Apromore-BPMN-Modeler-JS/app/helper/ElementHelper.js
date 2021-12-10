var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    ProcessSimulationHelper = require('./ProcessSimulationHelper'),
    ValidationErrorHelper = require('./ValidationErrorHelper'),
    is = require('bpmn-js/lib/util/ModelUtil').is;

var ElementHelper = {};

ElementHelper.getElementById = function(bpmnFactory, elementRegistry, id) {
  var elements = ElementHelper.getElements(bpmnFactory, elementRegistry);

  var element = (elements.get('values').filter(function(el) {
    return el.elementId === id;
  }) || [])[0];

  if (!element) {
    element = elementHelper.createElement('qbp:Element',
      { elementId: id }, elements, bpmnFactory);

    elements.values.push(element);
  }

  return element;
};

ElementHelper.getElements = function(bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var elements = processSimulationInfo.elements;

  function filterRemovedQBPElements() {
    var elementRegistryEvents = elementRegistry.filter(function(el) {
      return isQBPElement(el);
    });

    var elementRegistryEventIds = elementRegistryEvents.map(function(el) {
      return el.id;
    });

    if (elementRegistryEventIds.length) {
      elements.values = elements.values.filter(function(el) {
        var isElementPresent = elementRegistryEventIds.indexOf(el.elementId) > -1;

        if (!isElementPresent) {
          ValidationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { elementId: el.elementId });
        }

        return isElementPresent;
      });
    }
  }

  function createQBPElements() {
    elements = elementHelper.createElement('qbp:Elements',
      { values: [] }, processSimulationInfo, bpmnFactory);

    processSimulationInfo.elements = elements;
  }

  if (elements && elements.values) {
    filterRemovedQBPElements();
  } else {
    createQBPElements();
  }

  return elements;
};

function isQBPElement(element) {
  return is(element, 'bpmn:Task') ||
    is(element, 'bpmn:IntermediateCatchEvent') ||
    is(element, 'bpmn:BoundaryEvent');
}


module.exports = ElementHelper;