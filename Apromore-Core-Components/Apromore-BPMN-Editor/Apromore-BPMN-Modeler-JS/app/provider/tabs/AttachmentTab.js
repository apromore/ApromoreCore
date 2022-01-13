var is = require('bpmn-js/lib/util/ModelUtil').is,
    createAuxGroups = require('../groups/AttachmentGroups');

module.exports = function(element, bpmnFactory, elementRegistry, translate, bpmnjs) {

  function shown(element) {
    return is(element, 'bpmn:FlowNode') && !is(element, 'bpmn:Process');
  }

  return {
    id: 'attachmentTab',
    label: translate('attachments'),
    groups: createAuxGroups(element, bpmnFactory, elementRegistry, translate, bpmnjs),
    enabled: function(element) {
      return shown(element);
    }
  };

};