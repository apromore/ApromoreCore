var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    getWeekDays = require('./WeekDays'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');


module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedTimetable = options.getSelectedTimetable;
  var getSelectedTimeslot = options.getSelectedTimeslot;

  var weekDays = getWeekDays(translate);

  return entryFactory.selectBox(translate, {
    id: 'timeslot-fromWeekDay',
    label: translate('timeslot.beginDay'),
    modelProperty: 'fromWeekDay',

    selectOptions: weekDays,

    get: function(element, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      return { fromWeekDay: selectedTimeslot && selectedTimeslot.fromWeekDay };
    },

    set: function(element, values, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      if (selectedTimeslot) {

        var fromWeedDay = weekDays.filter(function(day) {
          return day.value === values.fromWeekDay;
        })[0];

        var toWeekDay = weekDays.filter(function(day) {
          return day.value === selectedTimeslot.toWeekDay;
        })[0];

        if (toWeekDay.index >= fromWeedDay.index) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry,
            { id: selectedTimeslot.id });
        }
      }

      return cmdHelper.updateBusinessObject(element, selectedTimeslot,
        { fromWeekDay: values.fromWeekDay });
    }
  });
};