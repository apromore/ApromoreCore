var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var  ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');
var createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (bpmnFactory, elementRegistry, translate, options) {


  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  return entryFactory.selectBox(translate, {
    id: 'operator_'+ createUUID(),
    label: translate('scenarioGroup.currency.label'),
    modelProperty: 'operator',
    selectOptions: createOperatorOptions(),

    get: function (_element, _node) {
      return { operator: processSimulationInfo.operator };
    },

    set: function (element, values, _node) {
      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        operator: values.operator
      });
    }
  });
};

function createOperatorOptions() {
  return [
    {
      name: 'AND',
      value: 'AND'
    },
    {
      name: 'OR',
      value: 'OR'
    }
  ];
}