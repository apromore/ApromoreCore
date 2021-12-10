var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    ResourceHelper = require('../../../../helper/ResourceHelper'),
    ElementHelper = require('../../../../helper/ElementHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var taskId = options.taskId;

  var selectBoxId = ['task', taskId, 'resource'].join('-');

  function createResourceOptions() {
    var resources = ResourceHelper.getResources(bpmnFactory, elementRegistry).values || [];

    var resourcesWithNotEmptyName = resources.filter(function(resource) {
      return resource.name;
    });

    return resourcesWithNotEmptyName.map(function(resource) {
      return {
        name: resource.name,
        value: resource.id
      };
    });
  }

  var taskElement = ElementHelper.getElementById(bpmnFactory, elementRegistry, taskId),
      resourceIds = ResourceHelper.getResourceIds(bpmnFactory, taskElement);

  return entryFactory.selectBox(translate, {
    id: selectBoxId,
    label: translate('resource'),
    modelProperty: 'resourceId',
    selectOptions: createResourceOptions,

    get: function(_element, _node) {
      return { resourceId: resourceIds.resourceId };
    },

    set: function(element, values, _node) {
      return cmdHelper.updateBusinessObject(element, resourceIds, {
        resourceId: values.resourceId
      });
    }
  });
};