var is = require('bpmn-js/lib/util/ModelUtil').is,
    scenarioSpecificationGroups = require('../groups/ScenarioSpecificationGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  function shown(element) {
    return is(element, 'bpmn:Process') || is(element, 'bpmn:SubProcess') ||
      is(element, 'bpmn:Collaboration') || is(element, 'bpmn:Participant');
  }

  return {
    id: 'simulationTab',
    label: translate('simulationTab.label'),
    groups: scenarioSpecificationGroups(element, bpmnFactory, elementRegistry, translate, shown),
    enabled: function(element) {
      return shown(element);
    }
  };
};