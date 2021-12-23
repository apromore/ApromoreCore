var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');

var ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper'),
    validationHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var label = translate('scenarioGroup.processInstances.label');

  return entryFactory.textField(translate, {
    id: 'processInstances',
    label: label,
    modelProperty: 'processInstances',

    get: function(_element, _node) {
      return { processInstances: processSimulationInfo.processInstances };
    },

    set: function(element, values, _node) {
      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        processInstances: values.processInstances
      });
    },

    validate: function(element, values, _node) {
      var validationId = this.id;

      var error = validationHelper.validateProcessInstances(bpmnFactory, elementRegistry, translate, {
        id: validationId,
        label: label,
        processInstances: values.processInstances,
      });

      if (!error.message) {
        validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
      }

      return { processInstances: error.message };
    }
  });
};