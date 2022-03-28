var is = require('bpmn-js/lib/util/ModelUtil').is,
    createCustomGroups = require('../groups/CustomGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate, config) {

  function shown(element) {
    return is(element, 'bpmn:FlowNode') ||
        is(element, 'bpmn:Process') ||
        is(element, 'bpmn:DataObject') ||
        is(element, 'bpmn:DataObjectReference') ||
        is(element, 'bpmn:DataStoreReference') ||
        is(element, 'bpmn:Participant') ||
        is(element, 'bpmn:Collaboration') ||
        is(element, 'bpmn:Lane');
  }

  return {
    id: 'customTab',
    label: translate('metadata.properties'),
    groups: createCustomGroups(element, bpmnFactory, elementRegistry, translate, config),
    enabled: function(element) {
      return shown(element);
    }
  };

};