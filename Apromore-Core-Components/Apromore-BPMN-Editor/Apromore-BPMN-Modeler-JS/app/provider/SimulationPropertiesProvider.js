var inherits = require('inherits');

var PropertiesActivator = require('bpmn-js-properties-panel/lib/PropertiesActivator');

var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    is = require('bpmn-js/lib/util/ModelUtil').is;

var ElementHelper = require('../helper/ElementHelper'),
    SequenceFlowHelper = require('../helper/SequenceFlowHelper'),
    suppressValidationError = require('../helper/ValidationErrorHelper').suppressValidationError;

var createSimulationParametersTab = require('./tabs/SimulationParametersTab'),
    createTaskTab = require('./tabs/TaskTab'),
    createTimetableTab = require('./tabs/TimetableTab'),
    createResourceTab = require('./tabs/ResourceTab'),
    createGatewayTab = require('./tabs/GatewayTab'),
    createIntermediateAndBoundaryEventsTab = require('./tabs/IntermediateAndBoundaryEventsTab');

var properties = require('bpmn-js-properties-panel/lib/provider/camunda/parts/PropertiesProps');

function removeTasks(element, bpmnFactory, elementRegistry) {
  var elements = ElementHelper.getElements(bpmnFactory, elementRegistry);

  if (elements && elements.values) {
    elements.values = elements.values.filter(function(task) {
      return task.elementId !== element.id;
    });
  }
}

function removeSequenceFlows(bpmnFactory, elementRegistry) {
  var scenarioSequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);

  var modelSequenceFlows = elementRegistry.filter(function(el) {
    return is(el, 'bpmn:SequenceFlow');
  });

  if (scenarioSequenceFlows && scenarioSequenceFlows.values) {
    scenarioSequenceFlows.values = scenarioSequenceFlows.values.filter(function(scenarioSequenceFlow) {
      var modelSequenceFlow = modelSequenceFlows.filter(function(modelFlow) {
        return modelFlow.id === scenarioSequenceFlow.elementId;
      });

      return modelSequenceFlow.length > 0;
    });
  }
}

function createExtensionElementsGroups(element, bpmnFactory, elementRegistry, translate) {

  var propertiesGroup = {
    id : 'extensionElements-properties',
    label: translate('Properties'),
    entries: []
  };
  properties(propertiesGroup, element, bpmnFactory, translate);

  return [
    propertiesGroup
  ];
}

function SimulationPropertiesProvider(eventBus, canvas, bpmnFactory, elementRegistry, translate) {

  PropertiesActivator.call(this, eventBus);

  this.getTabs = function(element) {

    console.log(element);

    var simulationParametersTab = createSimulationParametersTab(element, bpmnFactory, elementRegistry, translate);
    var taskTab = createTaskTab(element, bpmnFactory, elementRegistry, translate);
    var timetableTab = createTimetableTab(element, bpmnFactory, elementRegistry, translate);
    var resourcesTab = createResourceTab(element, bpmnFactory, elementRegistry, translate);
    var gatewayTab = createGatewayTab(element, bpmnFactory, elementRegistry, translate);
    var intermediateAndBoundaryEventsTab = createIntermediateAndBoundaryEventsTab(element, bpmnFactory, elementRegistry, translate);

    var extensionsTab = {
      id: 'extensionElements',
      label: translate('properties'),
      groups: createExtensionElementsGroups(element, bpmnFactory, elementRegistry, translate)
    };

    function getDefaultTabs() {
      return [simulationParametersTab, taskTab, timetableTab, resourcesTab, gatewayTab, intermediateAndBoundaryEventsTab, extensionsTab];
    }

    function getTaskTabs() {
      return [taskTab, simulationParametersTab, timetableTab, resourcesTab, gatewayTab, intermediateAndBoundaryEventsTab, extensionsTab];
    }

    function getGatewayTabs() {
      return [gatewayTab, taskTab, timetableTab, resourcesTab, simulationParametersTab, intermediateAndBoundaryEventsTab, extensionsTab];
    }

    function getEventTabs() {
      return [intermediateAndBoundaryEventsTab, simulationParametersTab, gatewayTab, taskTab, timetableTab, resourcesTab, extensionsTab];
    }

    var tabs = {
      'bpmn:Task': getTaskTabs(),
      'bpmn:ExclusiveGateway': getGatewayTabs(),
      'bpmn:InclusiveGateway': getGatewayTabs(),
      'bpmn:IntermediateCatchEvent': getEventTabs(),
      'bpmn:BoundaryEvent': getEventTabs(),
      'default': getDefaultTabs()
    };

    return element && tabs[element.type] || tabs['default'];
  };

  function isInvokedOnQBPElement(event) {
    return is(event.element, 'bpmn:Task') ||
      is(event.element, 'bpmn:BoundaryEvent') ||
      is(event.element, 'bpmn:IntermediateCatchEvent');
  }

  function isInvokedOnGateway(event) {
    return is(event.element, 'bpmn:ExclusiveGateway') ||
      is(event.element, 'bpmn:InclusiveGateway');
  }

  // 1500 is the HIGHEST PRIORITY for the listener
  eventBus.on('shape.remove', 1500, function(event) {
    if (isInvokedOnQBPElement(event)) {
      suppressValidationError(bpmnFactory, elementRegistry, { elementId: event.element.id });
      removeTasks(event.element, bpmnFactory, elementRegistry);
    } else if (isInvokedOnGateway(event)) {
      suppressValidationError(bpmnFactory, elementRegistry, { elementId: event.element.id });
    }

    removeSequenceFlows(bpmnFactory, elementRegistry);

    getBusinessObject(event.element).toBeRemoved = true;
  });
}

SimulationPropertiesProvider.$inject = [
  'eventBus',
  'canvas',
  'bpmnFactory',
  'elementRegistry',
  'translate'
];

inherits(SimulationPropertiesProvider, PropertiesActivator);

module.exports = SimulationPropertiesProvider;