var createResourceEntry = require('../entries/resource/ResourceEntry'),
    createResourceDetailEntries = require('../entries/resource/ResourceDetailEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var resourceEntry = createResourceEntry(element, bpmnFactory, elementRegistry, translate);

  var resourceGroup = {
    id: 'resources',
    label: '',
    entries: resourceEntry.entries
  };

  var getSelectedResource = resourceEntry.getSelectedResource;

  var resourceDetailEntries = createResourceDetailEntries(bpmnFactory, elementRegistry, translate,
    { getSelectedResource: getSelectedResource });

  var resourceDetailsGroup = {
    id: 'resource-details',
    entries: resourceDetailEntries,
    enabled: function(element, node) {
      return getSelectedResource(element, node);
    },
    label: translate('details')
  };

  return [
    resourceGroup,
    resourceDetailsGroup
  ];
};