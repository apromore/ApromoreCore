var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedResource = options.getSelectedResource;

  var label = translate('resource.name');

  return entryFactory.textField(translate, {
    id: 'resource-name',
    label: label,
    modelProperty: 'name',

    get: function(element, node) {

      var selectedResource = getSelectedResource(element, node);

      return { name: selectedResource && selectedResource.name };
    },

    set: function(element, values, node) {

      var selectedResource = getSelectedResource(element, node);

      return cmdHelper.updateBusinessObject(element, selectedResource, {
        name: values.name
      });
    },

    validate: function(element, values, node) {

      var selectedResource = getSelectedResource(element, node);

      if (selectedResource) {
        var validationId = selectedResource.id + this.id;

        var error = validationErrorHelper.validateResourceName(bpmnFactory, elementRegistry, translate, {
          id : validationId,
          label: label,
          name: values.name,
          resource: selectedResource,
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { name: error.message };
      }
    }
  });
};