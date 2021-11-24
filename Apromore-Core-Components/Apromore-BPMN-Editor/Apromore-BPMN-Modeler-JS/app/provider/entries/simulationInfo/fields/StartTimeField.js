var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    dateTimeField = require('../../DateTimeField'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  return dateTimeField({
    id: 'startDate',
    type: 'date',
    label: translate('scenarioGroup.startDate.label'),
    modelProperty: 'startDate',

    get: function(_element, _node) {
      return {
        startDate: processSimulationInfo.startDateTime.split('T')[0]
      };
    },

    set: function(element, values) {
      var startDate = new Date(processSimulationInfo.startDateTime);

      if (values.startDate !== '') {
        var date = values.startDate.split('-');
        startDate.setUTCFullYear(date[0]);
        startDate.setUTCMonth(date[1] - 1);
        startDate.setUTCDate(date[2]);
      } else {
        var today = new Date();
        startDate.setUTCFullYear(today.getUTCFullYear());
        startDate.setUTCMonth(today.getUTCMonth());
        startDate.setUTCDate(today.getUTCDate());
      }

      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        startDateTime: startDate.toISOString()
      });
    }
  });
};