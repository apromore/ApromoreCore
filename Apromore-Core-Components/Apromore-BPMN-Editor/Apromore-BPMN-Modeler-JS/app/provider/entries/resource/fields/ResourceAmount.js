var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedResource = options.getSelectedResource;

  var label = translate('resource.totalAmount');

  return entryFactory.textField(translate, {
    id: 'resource-amount',
    label: label,
    modelProperty: 'totalAmount',

    get: function(element, node) {

      var selectedResource = getSelectedResource(element, node);

      return { totalAmount: selectedResource && selectedResource.totalAmount };
    },

    set: function(element, values, node) {
      var selectedResource = getSelectedResource(element, node);

      return cmdHelper.updateBusinessObject(element, selectedResource,
        { totalAmount: values.totalAmount }
      );
    },

    validate: function(element, values, node) {

      var selectedResource = getSelectedResource(element, node);

      if (selectedResource) {
        var validationId = selectedResource.id + this.id;

        var error = validationErrorHelper.validateResourceNumber(bpmnFactory, elementRegistry, translate, {
          id: validationId,
          label: label,
          resource: selectedResource,
          totalAmount: values.totalAmount,
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { totalAmount: error.message };
      }
    }
  });
};