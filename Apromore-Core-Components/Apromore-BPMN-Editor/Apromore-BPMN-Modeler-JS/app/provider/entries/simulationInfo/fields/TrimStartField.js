var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationHelper = require('../../../../helper/ValidationErrorHelper'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var statsOptions = ProcessSimulationHelper.getStatsOptions(bpmnFactory, elementRegistry);

  var label = translate('scenarioGroup.startExclude.label');

  return entryFactory.textField(translate, {
    id: 'trimStartProcessInstances',
    label: label,
    modelProperty: 'trimStartProcessInstances',

    get: function(_element, _node) {
      return { trimStartProcessInstances: statsOptions.trimStartProcessInstances };
    },

    set: function(element, values, _node) {
      return cmdHelper.updateBusinessObject(element, statsOptions, {
        trimStartProcessInstances: values.trimStartProcessInstances === '' ? undefined : values.trimStartProcessInstances
      });
    },

    validate: function(element, values, _node) {
      var validationId = this.id;

      var error = validationHelper.validateTrimStartProcessInstances(bpmnFactory, elementRegistry, translate, {
        id: validationId,
        label: label,
        trimStartProcessInstances: values.trimStartProcessInstances
      });

      if (!error.message) {
        validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
      }

      return { trimStartProcessInstances: error.message };
    }
  });
};