var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    dateTimeField = require('../../DateTimeField'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');


module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedTimetable = options.getSelectedTimetable;
  var getSelectedTimeslot = options.getSelectedTimeslot;

  return dateTimeField({
    id: 'timeslot-toTime',
    type: 'time',
    label: translate('timeslot.endTime'),
    modelProperty: 'toTime',

    get: function(element, node) {

      var timetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, timetable);

      return { toTime: selectedTimeslot && selectedTimeslot.toTime && selectedTimeslot.toTime.slice(0, 5) };
    },

    set: function(element, values, node) {

      var timetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, timetable);

      var fromTime = values.toTime ? values.toTime + ':00.000+00:00' : undefined;

      return cmdHelper.updateBusinessObject(element, selectedTimeslot, { toTime: fromTime });
    },

    validate: function(element, values, node) {

      var timetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, timetable);

      if (selectedTimeslot) {
        var error = validationErrorHelper.validateToTime(bpmnFactory, elementRegistry, translate, {
          toTime: values.toTime,
          timeslot: selectedTimeslot
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: error.id });
        }

        return { toTime: error.message };
      }
    }
  });
};