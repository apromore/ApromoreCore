var { format, utcToZonedTime, zonedTimeToUtc } = require("date-fns-tz");
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    dateTimeField = require('../../DateTimeField'),
    ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, config) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);
  const timeZone = config.zoneId || 'UTC';

  return dateTimeField({
    id: 'startDate',
    type: 'date',
    label: translate('scenarioGroup.startDate.label'),
    modelProperty: 'startDate',

    get: function(_element, _node) {
      const dateZoned = utcToZonedTime(processSimulationInfo.startDateTime, timeZone);
      const startDate = format(dateZoned, "yyyy-MM-dd");

      return {
        startDate
      };
    },

    set: function(element, values) {
      const dateZoned = utcToZonedTime(processSimulationInfo.startDateTime, timeZone);
      const startDate = format(dateZoned, "yyyy-MM-dd");
      const startTime = format(dateZoned, "HH:mm");

      let newStartDateTime;

      if (values.startDate !== '') {
        newStartDateTime = values.startDate + ' ' + startTime
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