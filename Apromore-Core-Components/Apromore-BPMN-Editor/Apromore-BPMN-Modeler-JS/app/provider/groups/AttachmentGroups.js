var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var createAuxEntries = require('../entries/attachment/AttachmentEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate, bpmnjs, eventBus) {
  var bo = getBusinessObject(element),
      groupId = ['bo', bo.get('id'), 'group'].join('-'),
      groupLabel = bo.name || bo.id;

  var auxGroup = {
    id : groupId,
    label: groupLabel,
    entries: createAuxEntries(element, bpmnFactory, elementRegistry, translate, bpmnjs, eventBus)
  };

  return [
    auxGroup
  ];
};