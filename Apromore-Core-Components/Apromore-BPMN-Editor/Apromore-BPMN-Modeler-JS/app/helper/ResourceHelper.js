var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    ProcessSimulationHelper = require('./ProcessSimulationHelper'),
    createUUID = require('../utils/Utils').createUUID;

var ResourceHelper = {};

ResourceHelper.getResources = function(bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var resources = processSimulationInfo.resources;

  if (!resources) {
    resources = elementHelper.createElement('qbp:Resources',
      { values: [] }, processSimulationInfo, bpmnFactory);

    processSimulationInfo.resources = resources;
  }

  return resources;
};

ResourceHelper.createResource = function(bpmnFactory, translate) {
  return elementHelper.createElement('qbp:Resource', {
    id: 'qbp_' + createUUID(),
    name: translate('resource'),
    timetableId: 'DEFAULT_TIMETABLE',
    totalAmount: '',
  }, null, bpmnFactory);
};

ResourceHelper.addResourceToResources = function(resource, resources) {
  resources.values.push(resources);
};

ResourceHelper.createDefaultResource = function(bpmnFactory, elementRegistry, translate) {
  var resources = ResourceHelper.getResources(bpmnFactory, elementRegistry);

  var defaultResource = (resources.get('values').filter(function(r) {
    return r.id === 'QBP_DEFAULT_RESOURCE';
  }) || [])[0];

  if (!defaultResource) {
    defaultResource = elementHelper.createElement('qbp:Resource',
      {
        id: 'QBP_DEFAULT_RESOURCE',
        name: translate('defaultResource.name'),
        totalAmount: '1',
        timetableId: 'DEFAULT_TIMETABLE'
      }, resources, bpmnFactory
    );

    resources.values.push(defaultResource);
  }
};

ResourceHelper.getResourceIds = function(bpmnFactory, element) {
  var resourceIds = element.get('resourceIds');

  if (!resourceIds) {
    resourceIds = elementHelper.createElement('qbp:ResourceIds',
      { resourceId: 'QBP_DEFAULT_RESOURCE' }, element, bpmnFactory);

    element.resourceIds = resourceIds;
  }

  return resourceIds;
};

module.exports = ResourceHelper;