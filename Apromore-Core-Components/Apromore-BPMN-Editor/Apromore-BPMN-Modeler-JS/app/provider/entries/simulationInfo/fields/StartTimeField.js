var { format, utcToZonedTime, zonedTimeToUtc } = require("date-fns-tz");
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    dateTimeField = require('../../DateTimeField'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, config) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  const timeZone = config.zoneId || 'UTC';

  return dateTimeField({
    id: 'startTime',
    type: 'time',
    label: translate('scenarioGroup.startTime.label'),
    modelProperty: 'startTime',

    get: function(_element, _node) {
      const dateZoned = utcToZonedTime(processSimulationInfo.startDateTime, timeZone);
      const startTime = format(dateZoned, "HH:mm");
      return {
        startTime
      };
    },

    set: function(element, values) {
      const dateZoned = utcToZonedTime(processSimulationInfo.startDateTime, timeZone);
      const startDate = format(dateZoned, "yyyy-MM-dd");
      const startTime = format(dateZoned, "HH:mm");

      let newStartDateTime;

      if (values.startTime !== '') {
        newStartDateTime = startDate + ' ' + values.startTime
      } else if (startDate !== '') {
        newStartDateTime = startDate + ' ' + startTime
      } else {
        newStartDateTime = format(new Date(), "yyyy-MM-dd HH:mm");
      }
      let startDateTime = zonedTimeToUtc(newStartDateTime, timeZone).toISOString()

      return cmdHelper.updateBusinessObject(element, processSimulationInfo, {
        startDateTime
      });
    }
  });
};