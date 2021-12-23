var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
    ResourceHelper = require('../../../helper/ResourceHelper'),
    suppressValidationError = require('../../../helper/ValidationErrorHelper').suppressValidationError;

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var entries = [];

  if (!ResourceHelper.getResources(bpmnFactory, elementRegistry).values.length) {
    ResourceHelper.createDefaultResource(bpmnFactory, elementRegistry, translate);
  }

  var resourceEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'simulationResources',
    label: translate('resources.label'),
    modelProperties: 'name',
    idGeneration: false,

    createExtensionElement: function(element, extensionElements, _value) {
      var resources = ResourceHelper.getResources(bpmnFactory, elementRegistry);

      var resource = ResourceHelper.createResource(bpmnFactory, translate);

      return cmdHelper.addElementsTolist(element, resources, 'values', [resource]);
    },

    removeExtensionElement: function(element, _extensionElements, value, idx) {
      var resources = ResourceHelper.getResources(bpmnFactory, elementRegistry);
      var selectedResource = resources.values[idx];

      if (!resources || !selectedResource) {
        return {};
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedResource.id });

      return cmdHelper.removeElementsFromList(element, resources, 'values',
        null, [selectedResource]);
    },

    getExtensionElements: function(_element) {
      return ResourceHelper.getResources(bpmnFactory, elementRegistry).values || [];
    },

    setOptionLabelValue: function(element, _node, option, _property, _value, idx) {
      var resources = ResourceHelper.getResources(bpmnFactory, elementRegistry);
      var selectedResource = resources.values[idx];

      option.text = selectedResource && selectedResource.name || translate('N/A');
    }
  });

  function getSelectedResource(element, node) {
    var selection = (resourceEntry && resourceEntry.getSelected(element, node)) || {
      idx: -1
    };

    var resources = ResourceHelper.getResources(bpmnFactory, elementRegistry).values || [];

    return resources[selection.idx];
  }

  entries.push(resourceEntry);

  return {
    entries: entries,
    getSelectedResource: getSelectedResource
  };
};