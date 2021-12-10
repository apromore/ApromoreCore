var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationHelper = require('../../../../helper/ValidationErrorHelper'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var statsOptions = ProcessSimulationHelper.getStatsOptions(bpmnFactory, elementRegistry);

  var label = translate('scenarioGroup.endExclude.label');

  return entryFactory.textField(translate, {
    id: 'trimEndProcessInstances',
    label: label,
    modelProperty: 'trimEndProcessInstances',

    get: function(_element, _node) {
      return { trimEndProcessInstances: statsOptions.trimEndProcessInstances };
    },

    set: function(element, values, _node) {
      return cmdHelper.updateBusinessObject(element, statsOptions, {
        trimEndProcessInstances: values.trimEndProcessInstances === '' ? undefined : values.trimEndProcessInstances
      });
    },

    validate: function(element, values, _node) {
      var validationId = this.id;

      var error = validationHelper.validateTrimEndProcessInstances(bpmnFactory, elementRegistry, translate, {
        id: validationId,
        label: label,
        trimEndProcessInstances: values.trimEndProcessInstances
      });

      if (!error.message) {
        validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
      }

      return { trimEndProcessInstances: error.message };
    }
  });
};