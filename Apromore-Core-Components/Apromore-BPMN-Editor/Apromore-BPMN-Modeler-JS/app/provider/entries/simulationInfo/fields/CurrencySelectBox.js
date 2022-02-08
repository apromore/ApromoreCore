var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  return entryFactory.selectBox(translate, {
    id: 'currency',
    label: translate('scenarioGroup.currency.label'),
    modelProperty: 'currency',
    selectOptions: createCurrencyOptions(),

    get: function(_element, _node) {
      return { currency: processSimulationInfo.currency };
    },

    set: function(element, values, _node) {
      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        currency: values.currency
      });
    }
  });
};

function createCurrencyOptions() {
  return [
    {
      name: 'EUR',
      value: 'EUR'
    },
    {
      name: 'USD',
      value: 'USD'
    },
    {
      name: 'AUD',
      value: 'AUD'
    },
    {
      name: 'GBP',
      value: 'GBP'
    },
    {
      name: 'JPY',
      value: 'JPY'
    }
  ];
}