var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedResource = options.getSelectedResource;

  var label = translate('resource.costPerHour');

  return entryFactory.textField(translate, {
    id: 'resource-cost',
    label: label,
    modelProperty: 'costPerHour',

    get: function(element, node) {

      var selectedResource = getSelectedResource(element, node);

      return { costPerHour : selectedResource && selectedResource.costPerHour || '' };
    },

    set: function(element, values, node) {

      var selectedResource = getSelectedResource(element, node);

      return cmdHelper.updateBusinessObject(element, selectedResource, {
        costPerHour: values.costPerHour || undefined
      });
    },

    validate: function(element, values, node) {

      var selectedResource = getSelectedResource(element, node);

      if (selectedResource) {

        var validationId = selectedResource.id + this.id;

        var error = validationErrorHelper.validateResourceCostPerHour(bpmnFactory, elementRegistry, translate, {
          id: validationId,
          costPerHour: values.costPerHour,
          resource: selectedResource,
          label: label
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { costPerHour: error.message };
      }
    }
  });
};