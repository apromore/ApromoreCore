var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    getWeekDays = require('./WeekDays'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');


module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedTimetable = options.getSelectedTimetable;
  var getSelectedTimeslot = options.getSelectedTimeslot;

  var weekDays = getWeekDays(translate);

  return entryFactory.selectBox(translate, {
    id: 'timeslot-toWeekDay',
    label: translate('timeslot.endDay'),
    modelProperty: 'toWeekDay',

    selectOptions: weekDays,

    get: function(element, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      return { toWeekDay: selectedTimeslot && selectedTimeslot.toWeekDay };
    },

    set: function(element, values, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      return cmdHelper.updateBusinessObject(element, selectedTimeslot, { toWeekDay: values.toWeekDay });
    },

    validate: function(element, values, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      if (selectedTimeslot) {

        var fromWeekDay = weekDays.filter(function(day) {
          return day.value === selectedTimeslot.fromWeekDay;
        })[0];

        var toWeekDay = weekDays.filter(function(day) {
          return day.value === values.toWeekDay;
        })[0];

        var error = validationErrorHelper.validateWeekDays(bpmnFactory, elementRegistry, translate, {
          fromWeekDay: fromWeekDay,
          toWeekDay: toWeekDay,
          timeslot: selectedTimeslot
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: error.id });
        }

        return { toWeekDay: error.message };
      }
    }
  });
};