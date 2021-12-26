var is = require('bpmn-js/lib/util/ModelUtil').is,
    createAuxGroups = require('../groups/AuxGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  function shown(element) {
    return is(element, 'bpmn:FlowNode');
    // return is(element, 'bpmn:BaseElement');
  }

  return {
    id: 'attachmentTab',
    label: translate('attachments'),
    groups: createAuxGroups(element, bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return shown(element);
    }
  };

};