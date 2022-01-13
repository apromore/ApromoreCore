var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var properties = require('bpmn-js-properties-panel/lib/provider/camunda/parts/PropertiesProps');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {
  var bo = getBusinessObject(element),
      groupId = ['bo', bo.get('id'), 'group'].join('-'),
      groupLabel = bo.name || bo.id;

  var customGroup = {
    id : groupId,
    label: groupLabel,
    entries: []
  };
  properties(customGroup, element, bpmnFactory, translate);

  return [
    customGroup
  ];
};