var is = require('bpmn-js/lib/util/ModelUtil').is,
    createCustomGroups = require('../groups/CustomGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  function shown(element) {
    return is(element, 'bpmn:FlowNode');
  }

  return {
    id: 'customTab',
    label: translate('metadata.properties'),
    groups: createCustomGroups(element, bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return shown(element);
    }
  };

};