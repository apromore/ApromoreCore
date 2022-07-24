var { format, utcToZonedTime, zonedTimeToUtc } = require("date-fns-tz");
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    dateTimeField = require('../../DateTimeField'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, config) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  return dateTimeField({
    id: 'startDate',
    type: 'date',
    label: translate('scenarioGroup.startDate.label'),
    modelProperty: 'startDate',

    get: function(_element, _node) {
      const timeZone = config.zoneId || 'UTC';
      const dateZoned = utcToZonedTime(processSimulationInfo.startDateTime, timeZone);
      const startDate = format(dateZoned, "yyyy-MM-dd");

      return {
        startDate
      };
    },

    set: function(element, values) {
      // Get the data model's date time
      var modelStartDateTime = new Date(processSimulationInfo.startDateTime);

      if (values.startDate !== '') {
        // Get the local time from the data model's date time
        var localStartTime = modelStartDateTime.toLocaleTimeString('en-GB');

        // Construct the utc date time based on the local date from the UI control,
        // and the converted local time from the data model above
        modelStartDateTime = new Date(values.startDate + 'T' + localStartTime);

      } else {
        modelStartDateTime = new Date();
      }
      var startDateTime = modelStartDateTime.toISOString()

      values.startTime

      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        startDateTime
      });
    }
  });
};