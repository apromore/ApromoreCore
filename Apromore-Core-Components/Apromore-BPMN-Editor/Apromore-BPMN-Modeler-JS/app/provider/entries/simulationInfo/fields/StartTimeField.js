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
        startTime: new Date(processSimulationInfo.startDateTime).toLocaleTimeString('en-GB').slice(0, 5)
      };
    },

    set: function(element, values) {
      // Get the data model's date time
      var modelStartDateTime = new Date(processSimulationInfo.startDateTime);

      if (values.startTime && values.startTime !== '') {

        // Get the local date from the data model's date time
        var localDateArr = modelStartDateTime.toLocaleDateString('en-GB').split('/');

        // Construct the utc date time based on the local date above, and the local time from the UI control
        modelStartDateTime = new Date(localDateArr[2] + '-' + localDateArr[1] + '-' + localDateArr[0] + 'T' + values.startTime);

      } else {
        modelStartDateTime = new Date();
      }

      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        startDateTime: modelStartDateTime.toISOString()
      });
    }
  });
};