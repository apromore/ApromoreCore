var is = require('bpmn-js/lib/util/ModelUtil').is,
    createAuxGroups = require('../groups/AttachmentGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate, bpmnjs, eventBus) {

  function shown(element) {
    return is(element, 'bpmn:FlowNode') && !is(element, 'bpmn:Process');
  }

  return {
    id: 'attachmentTab',
    label: translate('attachments'),
    groups: createAuxGroups(element, bpmnFactory, elementRegistry, translate, bpmnjs, eventBus),
    enabled: function(element) {
      return shown(element);
    }
  };

};