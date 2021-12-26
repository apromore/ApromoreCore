var createAuxEntries = require('../entries/aux/AuxEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {
  var bo = getBusinessObject(element),
      groupId = ['bo', bo.get('id'), 'group'].join('-'),
      groupLabel = bo.name || bo.id;

  var auxGroup = {
    id : groupId,
    label: groupLabel,
    entries: createAuxEntries(element, bpmnFactory, elementRegistry, translate)
  };

  return [
    auxGroup
  ];
};