var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    dateTimeField = require('../../DateTimeField'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  return dateTimeField({
    id: 'startTime',
    type: 'time',
    label: translate('scenarioGroup.startTime.label'),
    modelProperty: 'startTime',

    get: function(_element, _node) {
      return {
        startTime: processSimulationInfo.startDateTime.split('T')[1].slice(0, 5)
      };
    },

    set: function(element, values) {
      var startTime = new Date(processSimulationInfo.startDateTime);

      if (values.startTime && values.startTime !== '') {
        var time = values.startTime.split(':');
        startTime.setUTCHours(time[0], time[1]);
      } else {
        startTime.setUTCHours(9, 0, 0, 0);
      }

      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        startDateTime: startTime.toISOString()
      });
    }
  });
};